package com.changhong.openvdb.driver.api;

import com.changhong.openvdb.driver.api.sql.SQL;
import com.changhong.openvdb.driver.api.sql.SQLParsedStatement;
import com.changhong.openvdb.driver.utils.ResultSets;
import com.changhong.utils.Captor;
import com.changhong.utils.Optional;
import com.changhong.utils.collection.Lists;
import com.changhong.openvdb.driver.api.exception.DriverException;
import com.changhong.openvdb.driver.api.sql.SQLExecutor;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.changhong.utils.string.StaticLibrary.strfmt;

/**
 * JDBC 驱动抽象层。
 * <p>
 * 封装不同数据库的驱动能力入口，提供统一的访问方式。该类不负责连接管理、不负责 SQL 解析，
 * 仅作为能力适配与分发入口，将底层 {@link DataSource} 与上层回调逻辑解耦。
 * <p>
 * <b>职责范围：</b>
 * <ul>
 *   <li>作为数据库能力入口（SQL 执行 / 元数据 / DDL 操作）</li>
 *   <li>绑定数据源以获取底层连接</li>
 *   <li>为不同数据库实现提供统一抽象基类（可通过继承或组合扩展）</li>
 * </ul>
 * <b>职责边界：</b>
 * <ul>
 *   <li>不负责连接池管理（由 {@link DataSource} 负责）</li>
 *   <li>不负责 SQL 解析与语义分析（由上层模块负责）</li>
 *   <li>不直接暴露具体数据库实现细节</li>
 * </ul>
 * <b>扩展方式：</b>
 * <ul>
 *   <li>MySQL / Oracle / PostgreSQL 等可通过继承 {@code Driver} 并重写相关方法实现</li>
 *   <li>或通过组合方式提供具体能力模块</li>
 * </ul>
 *
 * @see DataSource
 * @see Statement
 * @see ResultSet
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 *
 */
@SuppressWarnings({"SpellCheckingInspection", "SqlSourceToSinkFlow"})
public abstract class Driver implements SQLExecutor
{
        /**
         * 底层数据源，用于获取数据库连接。
         * <p>
         * 该引用为 {@code protected}，允许子类直接访问以支持更灵活的连接管理。
         */
        protected final DataSource dataSource;

        /**
         * 执行任务列表
         */
        protected final Map<Long, Statement> taskQueue = new ConcurrentHashMap<>();

        /**
         * 数据库产品元数据
         */
        @Getter
        protected final ProductMetaData productMetaData;

        /**
         * 数据库方言转换器
         */
        @Getter
        protected final Dialect dialect;

        /**
         * 执行 {@link Statement#execute(String)} 或 {@link Statement#execute(String, int)}
         * 等返回布尔值方法的回调接口。
         *
         * @see Statement#execute(String)
         */
        public interface StatementExecuteCallback
        {
                /**
                 * 使用给定的 {@link Statement} 执行数据库操作。
                 *
                 * @param connection JDBC 连接对象
                 * @param statement JDBC 语句对象，由调用方创建并管理生命周期
                 * {@code true} 表示执行结果为结果集，{@code false} 表示更新计数或 DDL 语句
                 * @throws SQLException 如果数据库访问错误发生
                 */
                void execute(Connection connection, Statement statement) throws SQLException;
        }

        /**
         * 执行 {@link Statement#executeUpdate(String)} 或 {@link Statement#executeUpdate(String, int)}
         * 等返回更新行数方法的回调接口。
         *
         * @see Statement#executeUpdate(String)
         */
        public interface StatementExecuteUpdateCallback
        {
                /**
                 * 使用给定的 {@link Statement} 执行更新操作。
                 *
                 * @param connection JDBC 连接对象
                 * @param statement JDBC 语句对象
                 * @return 受影响的记录行数（与 {@link Statement#executeUpdate(String)} 语义一致）
                 * @throws SQLException 如果数据库访问错误发生
                 */
                int executeUpdate(Connection connection, Statement statement) throws SQLException;
        }

        /**
         * 执行 {@link Statement#executeQuery(String)} 等返回结果集方法的回调接口。
         *
         * @see Statement#executeQuery(String)
         */
        public interface StatementExecuteQueryCallback
        {
                /**
                 * 使用给定的 {@link Statement} 执行查询操作。
                 *
                 * @param connection JDBC 连接对象
                 * @param statement JDBC 语句对象
                 * @return 查询结果集数据表对象
                 * @throws SQLException 如果数据库访问错误发生
                 */
                DataGrid executeQuery(Connection connection, Statement statement) throws SQLException;
        }

        /**
         * 构造一个新的驱动实例。
         *
         * @param dataSource 数据源，用于获取数据库连接（不能为 {@code null}）
         * @throws NullPointerException 如果 {@code dataSource} 为 {@code null}
         */
        public Driver(DataSource dataSource)
        {
                this.dataSource = Objects.requireNonNull(dataSource, "DataSource must not be null");

                try (var conn = dataSource.getConnection()) {
                        DatabaseMetaData db = conn.getMetaData();
                        productMetaData = new ProductMetaData();
                        productMetaData.setProductName(Optional.ifError(db::getDatabaseProductName, "ERROR"));
                        productMetaData.setVersion(Optional.ifError(db::getDatabaseProductVersion, "ERROR"));
                        productMetaData.setMajorVersion(Optional.ifError(db::getDatabaseMajorVersion, -1));
                        productMetaData.setMinorVersion(Optional.ifError(db::getDatabaseMinorVersion, -1));
                } catch (Exception e) {
                        throw new DriverException(e);
                }

                this.dialect = createDialect();
        }

        /**
         * 创建并返回当前环境适用的数据库方言实例。
         * <p>
         * 该方法是一个模板方法（Template Method），由子类实现以提供具体的方言对象。
         * 方言实例封装了特定数据库的 SQL 语法差异，用于生成分页语句、DDL 适配、
         * 标识符转义等数据库特定操作。
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>子类必须实现该方法，返回与目标数据库匹配的 {@link Dialect} 实例</li>
         *   <li>返回的方言实例应为无状态（stateless）且线程安全的，可被多个并发线程共享</li>
         *   <li>通常采用单例模式返回同一个方言实例，避免重复创建开销</li>
         *   <li>实现可根据数据源 URL、数据库产品名称或配置参数动态选择具体方言子类</li>
         * </ul>
         *
         * @return 适用于当前数据库的方言实例（永不返回 {@code null}）
         * @throws IllegalStateException    如果无法根据当前环境确定合适的方言（如数据库类型未知）
         * @throws UnsupportedOperationException 如果当前数据库不被支持
         * @see Dialect
         */
        protected abstract Dialect createDialect();

        /**
         * 获取数据库连接。
         * <p>
         * 调用无参版本等效于 {@link #getConnection(Session) getConnection(null)}，
         * 即不设置任何会话级别的 catalog 或 schema。
         *
         * @return 从底层 {@link DataSource} 获取的数据库连接
         * @throws SQLException 如果数据源无法返回连接或发生数据库访问错误
         * @see #getConnection(Session)
         */
        public Connection getConnection() throws SQLException {
                return getConnection(null);
        }

        /**
         * 获取数据库连接，并可选择性地设置当前会话的 catalog 和 schema。
         * <p>
         * 该方法首先通过底层 {@link DataSource} 获取连接。如果传入的 {@code session} 参数非空，
         * 且其 {@code catalog()} 或 {@code schema()} 返回值不为 {@code null}，则分别调用
         * {@link Connection#setCatalog(String)} 和 {@link Connection#setSchema(String)} 进行设置。
         * <p>
         * <b>注意：</b> 调用方有责任在使用完毕后关闭返回的 {@link Connection} 实例，
         * 推荐使用 try-with-resources 语句以确保资源释放。
         *
         * @param session 会话上下文，包含可选的 catalog 和 schema 信息；可为 {@code null}
         * @return 配置好会话属性的数据库连接
         * @throws SQLException 如果数据源无法返回连接、设置 catalog/schema 失败，
         *                      或发生其他数据库访问错误
         * @see Connection#setCatalog(String)
         * @see Connection#setSchema(String)
         */
        public Connection getConnection(Session session) throws SQLException {
                Connection connection = dataSource.getConnection();

                if (session != null) {
                        if (session.catalog() != null)
                                connection.setCatalog(session.catalog());

                        if (session.schema() != null)
                                connection.setSchema(session.schema());
                }

                return connection;
        }

        /**
         * 生成用于查看指定表的创建语句的 SQL。
         * <p>
         * 不同数据库获取表定义 DDL 的语法差异较大，该方法应返回针对当前方言适配后的可执行 SQL。
         * <p>
         * <b>常见数据库实现示例：</b>
         * <ul>
         *   <li>MySQL: {@code SHOW CREATE TABLE table_name}</li>
         *   <li>PostgreSQL: {@code SELECT pg_get_tabledef('schema.table_name')} 或使用 {@code pg_dump} 相关函数</li>
         *   <li>Oracle: {@code SELECT DBMS_METADATA.GET_DDL('TABLE', 'table_name') FROM DUAL}</li>
         *   <li>SQL Server: {@code sp_helptext 'table_name'} 或查询系统视图</li>
         * </ul>
         *
         * @param session 会话上下文，包含 catalog 和 schema 信息以定位表（不能为 {@code null}）
         * @param table   表名称（不能为 {@code null} 或空白字符串）
         * @return 可执行的 SQL 语句字符串，执行后可获取表的完整创建 DDL
         * @throws NullPointerException     如果 {@code session} 或 {@code table} 为 {@code null}
         * @throws IllegalArgumentException 如果 {@code table} 为空白字符串
         * @throws UnsupportedOperationException 如果底层数据库不支持获取表的创建语句
         */
        public abstract String showCreateTable(Session session, String table);

        /**
         * 获取当前数据库实例中所有可用的 catalog（目录）名称列表。
         * <p>
         * 该方法通过 {@link DatabaseMetaData#getCatalogs()} 获取结果集，
         * 并提取列名为 {@code "TABLE_CAT"} 的值。所有 {@link SQLException}
         * 会被捕获并包装为 {@link DriverException} 后重新抛出。
         *
         * @return 不可变的 catalog 名称列表（可能为空，但不为 {@code null}）
         * @throws DriverException 如果数据库元数据访问失败
         * @see DatabaseMetaData#getCatalogs()
         */
        public List<String> getCatalogs() {
                List<String> catalogs = Lists.newArrayList();

                try (Connection connection = getConnection()) {
                        DatabaseMetaData metadata = connection.getMetaData();
                        ResultSet rs = metadata.getCatalogs();

                        while (rs.next())
                                catalogs.add(rs.getString("TABLE_CAT"));

                        return catalogs;
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * 获取当前数据库实例中所有可用的 schema（模式）名称列表。
         * <p>
         * 该方法通过 {@link DatabaseMetaData#getSchemas()} 获取结果集，
         * 并提取列名为 {@code "TABLE_SCHEM"} 的值。所有 {@link SQLException}
         * 会被捕获并包装为 {@link DriverException} 后重新抛出。
         *
         * @return 不可变的 schema 名称列表（可能为空，但不为 {@code null}）
         * @throws DriverException 如果数据库元数据访问失败
         * @see DatabaseMetaData#getSchemas()
         */
        public List<String> getSchemas() {
                List<String> schemas = Lists.newArrayList();

                try (Connection connection = getConnection()) {
                        DatabaseMetaData metadata = connection.getMetaData();
                        ResultSet rs = metadata.getSchemas();

                        while (rs.next())
                                schemas.add(rs.getString("TABLE_SCHEM"));

                        return schemas;
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * 获取指定会话上下文中所有用户定义的表名称列表。
         * <p>
         * 该方法调用 {@link DatabaseMetaData#getTables(String, String, String, String[])}，
         * 参数使用：catalog 和 schema 取自 {@code session}，表名模式为 {@code "%"}（匹配所有），
         * 类型数组限定为 {@code "TABLE"}（仅返回基本表，不包括视图、系统表等）。
         * <p>
         * 注意：如果 {@code session.catalog()} 或 {@code session.schema()} 返回 {@code null}，
         * 则对应的参数将按照 JDBC 规范解释（通常表示不限制该层级）。
         * <p>
         * 由于不同的数据库表结构的规范是不相同的，所以需要子类自己实现数据库表结构的查询和生成，
         * 避免 JDBC 标准查出来数据信息过少。
         *
         * @param session 会话上下文，包含 catalog 和 schema 过滤条件（不能为 {@code null}）
         * @return 表名列表（可能为空，但不为 {@code null}）
         * @throws NullPointerException 如果 {@code session} 为 {@code null}
         * @throws DriverException 如果数据库元数据访问失败
         * @see DatabaseMetaData#getTables(String, String, String, String[])
         */
        public abstract List<Table> getTables(Session session);

        /**
         * 获取指定数据库表的列元信息列表。
         * <p>
         * 根据当前方言的实现，从数据库元数据中提取指定表的所有列的详细信息，
         * 包括列名、数据类型、是否可空、是否主键、默认值、注释等。
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>返回的列表顺序应与表定义中的列顺序一致（通常按 {@code ORDINAL_POSITION} 升序）</li>
         *   <li>应通过 {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)} 获取原始元数据</li>
         *   <li>对于不支持 catalog 或 schema 的数据库，对应的 {@code session} 参数中的属性可为 {@code null}</li>
         * </ul>
         * <p>
         * <b>使用示例：</b>
         * <pre>{@code
         * Session session = new Session("my_db", "public");
         * List<Column> columns = dialect.getColumns(session, "user_table");
         * for (Column col : columns) {
         *     System.out.println(col.name() + " : " + col.type());
         * }
         * }</pre>
         *
         * @param session 会话上下文，包含 catalog 和 schema 信息以定位表（不能为 {@code null}）
         * @param table   表名称（不能为 {@code null} 或空白字符串）
         * @return 包含表所有列元数据的不可变列表（若表不存在或无权限，返回空列表）
         * @throws NullPointerException     如果 {@code session} 或 {@code table} 为 {@code null}
         * @throws IllegalArgumentException 如果 {@code table} 为空白字符串
         * @throws DriverException   如果底层 JDBC 访问发生错误（包装 {@link java.sql.SQLException}）
         * @see java.sql.DatabaseMetaData#getColumns(String, String, String, String)
         * @see Column
         */
        public abstract List<Column> getColumns(Session session, String table);

        public List<Column> getColumns(Session session, Table table)
        {
                return getColumns(session, table.getName());
        }

        /**
         * 获取指定数据库表的所有索引信息。
         * <p>
         * 该方法通过 {@link java.sql.DatabaseMetaData#getIndexInfo(String, String, String, boolean, boolean)}
         * 获取目标表上定义的所有索引（包括主键索引、唯一索引、普通索引等），并将每个索引封装为 {@link Index} 对象。
         * <p>
         * <b>返回的索引信息包含：</b>
         * <ul>
         *   <li>索引名称（{@link Index#getName()} ()}）</li>
         *   <li>索引类型（唯一索引、普通索引、全文索引等）</li>
         *   <li>索引的排序方向（ASC/DESC）</li>
         *   <li>索引的过滤条件（部分索引，如 PostgreSQL 的 WHERE 子句）</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>应按照索引名称和列在索引中的位置（ORDINAL_POSITION）进行排序返回</li>
         *   <li>主键索引可能被某些数据库视为特殊的索引（如 MySQL 中主键约束对应 {@code PRIMARY} 索引），应一并返回</li>
         *   <li>应过滤掉系统生成的内部索引（如外键自动创建的索引），避免信息冗余</li>
         *   <li>若表不存在或无任何索引，返回空列表（而非 {@code null}）</li>
         *   <li>需要处理不同数据库对索引元数据返回的差异（如 Oracle 的索引与约束关系）</li>
         * </ul>
         *
         * @param session 会话上下文，包含 catalog 和 schema 信息以定位表（不能为 {@code null}）
         * @param table   目标表元数据（包含表名及所在 catalog/schema，不能为 {@code null}）
         * @return 包含表所有索引信息的列表，按索引名称及列顺序排列；若无索引则返回空列表（永不为 {@code null}）
         * @throws NullPointerException      如果 {@code session} 或 {@code table} 为 {@code null}
         * @throws DriverException    如果获取元数据过程中发生 {@link java.sql.SQLException}
         * @see java.sql.DatabaseMetaData#getIndexInfo(String, String, String, boolean, boolean)
         * @see Index
         */
        public abstract List<Index> getIndexes(Session session, Table table);

        /**
         * 获取当前数据库方言支持的所有索引类型名称。
         * <p>
         * 不同数据库支持不同的索引类型，该方法返回的类型名称应与数据库内部定义的索引类型关键字一致。
         * <p>
         * <b>常见索引类型示例：</b>
         * <ul>
         *   <li>MySQL: {@code BTREE}, {@code HASH}, {@code FULLTEXT}, {@code SPATIAL}</li>
         *   <li>PostgreSQL: {@code BTREE}, {@code HASH}, {@code GIST}, {@code GIN}, {@code BRIN}, {@code SPGIST}</li>
         *   <li>Oracle: {@code NORMAL} (B-Tree), {@code BITMAP}, {@code FUNCTION-BASED}, {@code DOMAIN}</li>
         *   <li>SQL Server: {@code CLUSTERED}, {@code NONCLUSTERED}, {@code COLUMNSTORE}, {@code XML}</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>返回的集合应为不可变集合（如 {@link java.util.Collections#unmodifiableSet}）或副本，避免调用方修改</li>
         *   <li>集合中不应包含重复元素</li>
         *   <li>若数据库支持动态扩展索引类型（如通过插件），实现类应能动态获取或至少返回内置支持的类型</li>
         *   <li>类型名称应使用大写形式，与数据库系统表或 {@code CREATE INDEX} 语法中的关键字保持一致</li>
         * </ul>
         * <p>
         * <b>使用示例：</b>
         * <pre>{@code
         * Set<String> types = dialect.getIndexTypes();
         * if (types.contains("BTREE")) {
         *     // 可以创建 B-Tree 索引
         * }
         * }</pre>
         *
         * @return 包含所有支持的索引类型名称的不可变集合（永不为 {@code null}，可能为空集合表示不支持显式指定索引类型）
         */
        public abstract Set<String> getIndexTypes();

        /**
         * 删除指定的数据库表。
         * <p>
         * 执行 DDL 操作移除整个表及其所有数据、索引、约束等。不同数据库的删除语法基本一致
         * （{@code DROP TABLE table_name}），但可能需要处理 {@code IF EXISTS} 子句或级联选项。
         * <p>
         * <b>实现注意事项：</b>
         * <ul>
         *   <li>应考虑数据库是否支持 {@code IF EXISTS} 子句以避免表不存在时抛出异常</li>
         *   <li>可能需要处理 {@code CASCADE} 选项以删除依赖该表的视图、外键等（如 PostgreSQL）</li>
         *   <li>该操作不可逆，实现时应注意事务语义（通常 DDL 在大多数数据库中隐式提交）</li>
         * </ul>
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param table 要删除的表名（不能为 {@code null} 或空白字符串）
         * @throws NullPointerException     如果 {@code table} 为 {@code null}
         * @throws IllegalArgumentException 如果 {@code table} 为空白字符串
         * @throws DriverException   如果执行 DDL 失败（包装 {@link java.sql.SQLException}）
         */
        public abstract void dropTable(Session session, String table);

        public void dropTable(Session session, Table table) {
                dropTable(session, table.getName());
        }

        /**
         * 删除指定表中的多个列。
         * <p>
         * 执行 DDL 操作从表中移除一个或多个列。不同数据库对删除列的支持差异较大：
         * <ul>
         *   <li>MySQL: {@code ALTER TABLE table_name DROP COLUMN col1, DROP COLUMN col2}</li>
         *   <li>PostgreSQL: 支持在单个 {@code ALTER TABLE} 中多次使用 {@code DROP COLUMN}</li>
         *   <li>Oracle: 每个 {@code DROP COLUMN} 需要单独的 {@code ALTER TABLE} 语句或使用 {@code SET UNUSED}</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>应尽量在单条 DDL 语句中完成所有列的删除（若数据库支持）以提高性能</li>
         *   <li>需处理列不存在的情况（根据方言策略选择忽略或抛出异常）</li>
         *   <li>注意删除列可能导致的依赖对象失效（如索引、约束），可考虑 {@code CASCADE} 选项</li>
         * </ul>
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param table   目标表元数据（包含表名及所在 catalog/schema，不能为 {@code null}）
         * @param columns 要删除的列集合（不能为 {@code null} 或空集合）
         * @throws NullPointerException     如果 {@code table} 或 {@code columns} 为 {@code null}
         * @throws IllegalArgumentException 如果 {@code columns} 为空集合，或任一列未绑定到该表
         * @throws DriverException   如果执行 DDL 失败
         */
        public abstract void dropColumns(Session session, String table, Collection<Column> columns);

        public void dropColumns(Session session, Table table, Collection<Column> columns) {
                dropColumns(session, table.getName(), columns);
        }

        /**
         * 删除指定表上的多个索引。
         * <p>
         * 执行 DDL 操作移除一个或多个索引。不同数据库对删除索引的语法和支持程度存在差异：
         * <ul>
         *   <li>MySQL: {@code DROP INDEX index_name ON table_name}，支持在单条语句中删除多个索引（需重复子句）</li>
         *   <li>PostgreSQL: {@code DROP INDEX index_name1, index_name2}（需指定索引名，无需表名，因为索引在数据库中全局或 schema 内唯一）</li>
         *   <li>Oracle: {@code DROP INDEX index_name}（索引独立于表，需指定索引名）</li>
         *   <li>SQL Server: {@code DROP INDEX table_name.index_name} 或 {@code DROP INDEX index_name ON table_name}</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>需要根据数据库方言选择正确的语法格式（表名前缀、是否支持多索引删除）</li>
         *   <li>若某个索引不存在，实现应根据策略选择忽略或抛出异常（建议提供配置选项）</li>
         * </ul>
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param table 目标表元数据（包含表名及所在 catalog/schema，不能为 {@code null}）
         * @param selectionItems 要删除的索引集合（每个 {@link Index} 应至少包含索引名称，不能为 {@code null} 或空集合）
         * @throws NullPointerException 如果 {@code table} 或 {@code selectionItems} 为 {@code null}
         * @throws IllegalArgumentException 如果 {@code selectionItems} 为空集合，或任一索引缺少必要的名称信息
         * @throws UnsupportedOperationException 如果数据库方言不支持删除索引操作
         * @throws DriverException 如果执行 DDL 失败（包装 {@link java.sql.SQLException}）
         * @see Index
         */
        public abstract void dropIndexKeys(Session session, String table, Collection<Index> selectionItems);

        public void dropIndexKeys(Session session, Table table, Collection<Index> selectionItems) {
                dropIndexKeys(session, table.getName(), selectionItems);
        }

        /**
         * 删除指定表的主键约束。
         * <p>
         * 执行 DDL 操作移除表的主键约束。不同数据库的语法略有差异：
         * <ul>
         *   <li>MySQL: {@code ALTER TABLE table_name DROP PRIMARY KEY}</li>
         *   <li>PostgreSQL: {@code ALTER TABLE table_name DROP CONSTRAINT constraint_name}</li>
         *   <li>Oracle: {@code ALTER TABLE table_name DROP PRIMARY KEY}</li>
         *   <li>SQL Server: {@code ALTER TABLE table_name DROP CONSTRAINT constraint_name}</li>
         * </ul>
         * <p>
         * <b>参数 {@code primaryKeys} 说明：</b>
         * <ul>
         *   <li>该参数用于安全校验：如果提供且非空，实现应验证当前表的主键列集合与 {@code primaryKeys}
         *       中的列集合完全匹配（名称、顺序），匹配成功才执行删除操作，否则抛出 {@link IllegalArgumentException}。</li>
         *   <li>如果 {@code primaryKeys} 为 {@code null} 或空集合，则直接删除主键约束（不进行校验）。</li>
         *   <li>这种设计可避免因表结构变更或误解导致的误删除。</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>删除主键前应先获取当前主键约束名称（通过 {@link java.sql.DatabaseMetaData#getPrimaryKeys}）</li>
         *   <li>若表没有主键约束，实现应静默忽略或根据策略抛出异常（建议抛出 {@link IllegalArgumentException}）</li>
         *   <li>删除主键后，之前依赖该主键的外键约束行为取决于数据库的级联设置，实现应给出明确日志或警告</li>
         *   <li>建议将操作包装在事务中（如果数据库支持 DDL 事务）以保证一致性</li>
         * </ul>
         * <p>
         * <b>使用示例：</b>
         * <pre>{@code
         * Session session = new Session("my_db", "public");
         * List<Column> expectedPk = List.of(new Column("id"));
         * // 安全删除：只有当主键确为 (id) 时才执行
         * dialect.dropPrimaryKey(session, "user_table", expectedPk);
         *
         * // 强制删除：不校验现有主键
         * dialect.dropPrimaryKey(session, "user_table", null);
         * }</pre>
         *
         * @param session     会话上下文，用于获取连接及设置 catalog/schema（不能为 {@code null}）
         * @param table       目标表名称（不能为 {@code null} 或空白字符串）
         * @throws NullPointerException      如果 {@code session} 或 {@code table} 为 {@code null}
         * @throws IllegalArgumentException  如果 {@code table} 为空白字符串；
         *                                   或 {@code primaryKeys} 非空但与现有主键不匹配；
         *                                   或表不存在主键但方法要求删除（根据实现策略）
         * @throws UnsupportedOperationException 如果数据库方言不支持删除主键约束
         * @throws DriverException    如果执行 DDL 失败（包装 {@link java.sql.SQLException}）
         * @see #alterPrimaryKey(Session, String, Collection)
         * @see java.sql.DatabaseMetaData#getPrimaryKeys(String, String, String)
         */
        public abstract void dropPrimaryKey(Session session, String table);

        public void dropPrimaryKey(Session session, Table table) {
                dropPrimaryKey(session, table.getName());
        }

        /**
         * 修改表的主键约束。
         * <p>
         * 重新定义指定表的主键，通常需要先删除旧的主键约束，再添加新的主键约束。
         * 不同数据库的语法差异示例：
         * <ul>
         *   <li>MySQL: {@code ALTER TABLE table_name DROP PRIMARY KEY, ADD PRIMARY KEY (col1, col2)}</li>
         *   <li>PostgreSQL: {@code ALTER TABLE table_name DROP CONSTRAINT pk_name, ADD PRIMARY KEY (col1, col2)}</li>
         *   <li>Oracle: 类似 PostgreSQL，需要知道主键约束名称</li>
         * </ul>
         * <p>
         * <b>实现注意事项：</b>
         * <ul>
         *   <li>若 {@code primaryKeys} 为空集合，表示删除所有主键（即表不再有主键）</li>
         *   <li>实现应能自动获取当前主键约束名称（通过 {@link DatabaseMetaData#getPrimaryKeys}）</li>
         *   <li>添加新主键前应验证所有列存在于表中且数据类型合适</li>
         *   <li>建议将操作包装在事务中（如果数据库支持 DDL 事务）以保证原子性</li>
         * </ul>
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param table 目标表元数据（不能为 {@code null}）
         * @param primaryKeys 新主键列的集合（按顺序），为空表示删除现有主键；集合中列的顺序决定了复合主键中各列的顺序
         * @throws NullPointerException     如果 {@code table} 或 {@code primaryKeys} 为 {@code null}
         * @throws IllegalArgumentException 如果任一列不属于该表，或列数量为 0 但数据库不允许无主键表
         * @throws DriverException   如果执行 DDL 失败
         */
        public abstract void alterPrimaryKey(Session session, String table, Collection<Column> primaryKeys);

        public void alterPrimaryKey(Session session, Table table, Collection<Column> primaryKeys) {
                alterPrimaryKey(session, table.getName(), primaryKeys);
        }

        /**
         * 修改指定表上的多个索引定义（如索引列、索引类型等）。
         * <p>
         * 该方法用于变更一个或多个索引的结构，典型场景包括：
         * <ul>
         *   <li>为现有索引添加或删除列（改变索引键）</li>
         *   <li>修改索引类型（如从 B-Tree 改为 Hash）</li>
         *   <li>调整索引的排序方向（ASC/DESC）</li>
         *   <li>更改索引的存储参数或并发选项</li>
         * </ul>
         * <p>
         * <b>实现注意事项：</b>
         * <ul>
         *   <li>大多数数据库不直接支持“修改索引”操作，通常的实现方式是：
         *       <ol>
         *         <li>根据 {@link Index} 对象中的新定义生成创建索引的 DDL</li>
         *         <li>删除旧索引（如果名称相同但定义不同）</li>
         *         <li>创建新索引</li>
         *       </ol>
         *   </li>
         *   <li>应考虑操作的原子性：如果可能，将操作包装在事务中；否则需提供回滚或错误恢复建议</li>
         *   <li>对于生产环境，建议使用 {@code CONCURRENTLY} 等选项避免锁表（如 PostgreSQL 支持）</li>
         *   <li>若索引不存在或新定义与现有定义相同，应忽略或根据策略抛出异常</li>
         *   <li>应优先使用数据库原生的 {@code ALTER INDEX} 语法（如 Oracle 的 {@code ALTER INDEX ... REBUILD}，
         *       但该语法通常不修改索引键，仅重建）—— 实际修改键时仍需删除重建</li>
         * </ul>
         * <p>
         * <b>参数 {@code indexes} 说明：</b>
         * <ul>
         *   <li>每个 {@link Index} 对象应包含索引名称以及完整的索引新定义（包含列名、索引类型等）</li>
         *   <li>若某个 {@code Index} 对象中的索引名称在表上不存在，实现应抛出 {@link IllegalArgumentException}</li>
         *   <li>若 {@code Index} 中的定义与其当前定义相同，可跳过该索引的修改</li>
         * </ul>
         * <p>
         * <b>使用示例：</b>
         * <pre>{@code
         * Session session = new Session("my_db", "public");
         * Table userTable = ...;
         * Index newIndexDef = Index.builder()
         *         .name("idx_username")
         *         .columns(List.of("last_name", "first_name"))  // 修改为复合索引
         *         .type(IndexType.BTREE)
         *         .build();
         * dialect.alterIndexKeys(session, userTable, List.of(newIndexDef));
         * }</pre>
         *
         * @param session 会话上下文，用于获取连接及设置 catalog/schema（不能为 {@code null}）
         * @param table   目标表元数据（包含表名及所在 catalog/schema，不能为 {@code null}）
         * @param indexes 需要修改的索引定义集合（不能为 {@code null} 或空集合），每个元素包含索引名称及新定义
         * @throws NullPointerException              如果 {@code session}、{@code table} 或 {@code indexes} 为 {@code null}
         * @throws IllegalArgumentException          如果 {@code indexes} 为空集合，或任一索引缺少名称，或索引不存在于表上，
         *                                           或新定义与现有定义相同但策略要求不忽略
         * @throws UnsupportedOperationException     如果数据库方言不支持索引修改（包括删除重建的方式）
         * @throws DriverException            如果执行 DDL 失败（包装 {@link java.sql.SQLException}）
         * @see Index
         */
        public abstract void alterIndexKeys(Session session, String table, Collection<Index> indexes);

        public void alterIndexKeys(Session session, Table table, Collection<Index> indexes) {
                alterIndexKeys(session, table.getName(), indexes);
        }

        /**
         * 修改表中多个列的定义（变更列属性）。
         * <p>
         * 该方法用于执行列的“更改”操作，包括修改列的数据类型、默认值、是否可空、注释等，
         * 但不包括删除列或重命名列（重命名应使用专门的 {@code renameColumn} 方法）。
         * <p>
         * <b>典型变更内容：</b>
         * <ul>
         *   <li>修改数据类型：{@code ALTER TABLE t MODIFY col VARCHAR(255)}</li>
         *   <li>修改默认值：{@code ALTER TABLE t ALTER COLUMN col SET DEFAULT 0}</li>
         *   <li>修改可空性：{@code ALTER TABLE t MODIFY col NOT NULL}</li>
         *   <li>修改注释：{@code ALTER TABLE t MODIFY col COMMENT 'new comment'}</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>应尽量生成单条 DDL 语句完成所有列的修改（若数据库支持）</li>
         *   <li>不同数据库的语法差异很大（MySQL 的 {@code MODIFY}，PostgreSQL 的 {@code ALTER COLUMN}，
         *       Oracle 的 {@code MODIFY} 等），实现类需针对目标数据库适配</li>
         *   <li>仅修改 {@link Column} 对象中实际发生变化的属性，避免不必要的变更</li>
         *   <li>对于不支持某些修改操作的数据库，应抛出 {@code UnsupportedOperationException}</li>
         * </ul>
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param table   目标表名称（不能为 {@code null} 或空白字符串）
         * @param columns 需要变更的列集合，每个 {@link Column} 应包含完整的新定义（不能为 {@code null} 或空集合）
         * @throws NullPointerException             如果 {@code table} 或 {@code columns} 为 {@code null}
         * @throws IllegalArgumentException         如果 {@code columns} 为空集合，或任一列未包含必要的标识信息（如列名）
         * @throws UnsupportedOperationException    如果数据库方言不支持列修改操作
         * @throws DriverException           如果执行 DDL 失败
         */
        public abstract void alterChange(Session session, String table, Collection<Column> columns);

        public void alterChange(Session session, Table table, Collection<Column> columns) {
                alterChange(session, table.getName(), columns);
        }

        /**
         * 修改指定表上多个索引的可见性状态。
         * <p>
         * 该方法用于启用或禁用一个或多个索引，而不删除索引定义。被禁用的索引将不会被查询优化器使用，
         * 但索引定义仍保留在数据库系统表中，可随时重新启用。
         * <p>
         * <b>适用场景：</b>
         * <ul>
         *   <li>临时禁用索引以评估其对查询性能的影响</li>
         *   <li>在大规模数据导入前禁用索引以提高写入性能，导入后重新启用</li>
         *   <li>故障排查时隔离特定索引以定位性能问题</li>
         * </ul>
         * <p>
         * <b>数据库支持差异：</b>
         * <ul>
         *   <li>Oracle: 支持 {@code ALTER INDEX index_name VISIBLE/INVISIBLE}</li>
         *   <li>MySQL 8.0+: 支持 {@code ALTER INDEX index_name VISIBLE/INVISIBLE}</li>
         *   <li>PostgreSQL: 不直接支持索引可见性，需通过 {@code pg_index.indisvisible} 或重新创建索引</li>
         *   <li>SQL Server: 支持 {@code ALTER INDEX index_name ON table_name DISABLE/REBUILD}</li>
         * </ul>
         * <p>
         * <b>实现要求：</b>
         * <ul>
         *   <li>每个 {@link Index} 对象应包含索引名称及目标可见性状态（通过 {@link Index#isVisible()} 或类似方法）</li>
         *   <li>应尽量生成单条 DDL 语句完成所有索引的可见性修改（若数据库支持）</li>
         *   <li>若数据库不支持索引可见性修改，应抛出 {@link UnsupportedOperationException}</li>
         *   <li>若指定索引不存在，应根据策略选择忽略或抛出 {@link IllegalArgumentException}</li>
         *   <li>注意：禁用索引可能导致依赖该索引的约束失效，实现时应考虑级联影响</li>
         *   <li>建议在操作前后记录日志，便于问题追踪</li>
         * </ul>
         *
         * @param session 会话上下文，用于获取连接及设置 catalog/schema（不能为 {@code null}）
         * @param table   目标表名称（不能为 {@code null} 或空白字符串）
         * @param indexes 需要修改可见性的索引集合，每个元素包含索引名称及目标可见性状态（不能为 {@code null} 或空集合）
         * @throws NullPointerException              如果 {@code session}、{@code table} 或 {@code indexes} 为 {@code null}
         * @throws IllegalArgumentException          如果 {@code table} 为空白字符串，或 {@code indexes} 为空集合，
         *                                           或任一索引缺少名称，或指定索引不存在于表上
         * @throws UnsupportedOperationException     如果数据库方言不支持索引可见性修改
         * @throws DriverException            如果执行 DDL 失败（包装 {@link java.sql.SQLException}）
         * @see Index#isVisible()
         */
        public abstract void alterVisible(Session session, String table, Collection<Index> indexes);

        public void alterVisible(Session session, Table table, Collection<Index> indexes) {
                alterVisible(session, table.getName(), indexes);
        }

        /**
         * 执行返回布尔值的数据库操作（如 DDL、部分存储过程调用）。
         * <p>
         * 该方法会从 {@link DataSource} 获取连接，设置会话的 catalog 和 schema，
         * 然后通过回调执行具体语句。
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param executeCallback 回调接口，定义具体执行逻辑
         * @throws DriverException 如果发生 {@link SQLException}（包装后抛出）
         * @see Statement#execute(String)
         */
        public void execute(Session session, StatementExecuteCallback executeCallback)
        {
                try (Connection connection = getConnection(session)) {
                        try (Statement statement = new StatementProxy(connection.createStatement())) {
                                executeCallback.execute(connection, statement);
                        }
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * 执行更新操作（INSERT / UPDATE / DELETE / DDL 等），返回受影响行数。
         *
         * @param session 当前会话上下文
         * @param executeUpdateCallback 回调接口，返回更新计数
         * @return 受影响的行数（对于 DDL 可能返回 0）
         * @throws DriverException 如果发生 {@link SQLException}
         * @see Statement#executeUpdate(String)
         */
        public int executeUpdate(Session session, StatementExecuteUpdateCallback executeUpdateCallback)
        {
                try (Connection connection = getConnection(session)) {
                        try (Statement statement = new StatementProxy(connection.createStatement())) {
                                return executeUpdateCallback.executeUpdate(connection, statement);
                        }
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * 执行查询操作，返回结果集。
         * <p>
         * <b>注意：</b> 调用方必须负责关闭返回的 {@link ResultSet} 及其关联的 {@link Statement}
         * 和 {@link Connection}。推荐使用 try-with-resources 模式。
         *
         * @param session 当前会话上下文
         * @param executeQueryCallback 回调接口，返回查询结果集
         * @return 查询结果集数据表对象
         * @throws DriverException 如果发生 {@link SQLException}
         * @see Statement#executeQuery(String)
         */
        public DataGrid executeQuery(Session session, StatementExecuteQueryCallback executeQueryCallback)
        {
                try (Connection connection = getConnection(session)) {
                        try (Statement statement = new StatementProxy(connection.createStatement())) {
                                return executeQueryCallback.executeQuery(connection, statement);
                        }
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        /* *********************************************************************************** */
        /*                                SQL EXECUTOR IMPLEMENTS                              */
        /* *********************************************************************************** */

        @Override
        public DataGrid selectByPage(Session session, String table, int off, int size)
        {
                String sql = strfmt("SELECT * FROM %s", dialect.quote(table));
                return execute(session, new SQL(dialect.limit(sql, off, size)));
        }

        @Override
        public DataGrid execute(long jobId, Session session, SQL sql)
        {
                return executeQuery(session, (connection, statement) -> {
                        DataGrid dataGrid = new DataGrid(session, this, sql);

                        taskQueue.put(jobId, statement);

                        SQLParsedStatement eps = sql.popupEnd();

                        for (SQLParsedStatement ps : sql) {
                                switch (ps.getCommand()) {
                                        case EXECUTE -> statement.execute(ps.toString());
                                        case EXECUTE_UPDATE -> statement.executeUpdate(ps.toString());
                                        case EXECUTE_QUERY -> {}
                                }
                        }

                        sql.pushback(eps);

                        switch (eps.getCommand()) {
                                case EXECUTE -> statement.execute(eps.toString());
                                case EXECUTE_UPDATE -> statement.executeUpdate(eps.toString());
                                case EXECUTE_QUERY -> {
                                        ResultSet rs = statement.executeQuery(eps.toString());
                                        ResultSets.toDataGrid(connection, eps, rs, dialect, dataGrid);
                                        return dataGrid;
                                }
                        }

                        return null;
                });
        }

        @Override
        public void cancel(long jobId)
        {
                if (taskQueue.containsKey(jobId))
                        Captor.call(() -> taskQueue.remove(jobId).cancel());
        }

}
