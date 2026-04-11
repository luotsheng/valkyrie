package com.changhong.driver.api;

import com.changhong.collection.Lists;
import com.changhong.driver.api.exception.DriverException;
import com.changhong.exception.SystemRuntimeException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Objects;

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
@SuppressWarnings("SpellCheckingInspection")
public abstract class Driver
{
        /**
         * 底层数据源，用于获取数据库连接。
         * <p>
         * 该引用为 {@code protected}，允许子类直接访问以支持更灵活的连接管理。
         */
        protected final DataSource dataSource;

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
        }

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
         * @throws SystemRuntimeException   如果底层 JDBC 访问发生错误（包装 {@link java.sql.SQLException}）
         * @see java.sql.DatabaseMetaData#getColumns(String, String, String, String)
         * @see Column
         */
        public abstract List<Column> getColumns(Session session, String table);

        /**
         * 执行返回布尔值的数据库操作（如 DDL、部分存储过程调用）。
         * <p>
         * 该方法会从 {@link DataSource} 获取连接，设置会话的 catalog 和 schema，
         * 然后通过回调执行具体语句。
         *
         * @param session 当前会话上下文，包含 catalog 和 schema 信息
         * @param executeCallback 回调接口，定义具体执行逻辑
         * @throws SystemRuntimeException 如果发生 {@link SQLException}（包装后抛出）
         * @see Statement#execute(String)
         */
        public void execute(Session session, StatementExecuteCallback executeCallback)
        {
                try (Connection connection = getConnection(session)) {
                        try (Statement statement = connection.createStatement()) {
                                executeCallback.execute(connection, statement);
                        }
                } catch (SQLException e) {
                        throw new SystemRuntimeException(e);
                }
        }

        /**
         * 执行更新操作（INSERT / UPDATE / DELETE / DDL 等），返回受影响行数。
         *
         * @param session 当前会话上下文
         * @param executeUpdateCallback 回调接口，返回更新计数
         * @return 受影响的行数（对于 DDL 可能返回 0）
         * @throws SystemRuntimeException 如果发生 {@link SQLException}
         * @see Statement#executeUpdate(String)
         */
        public int executeUpdate(Session session, StatementExecuteUpdateCallback executeUpdateCallback)
        {
                try (Connection connection = getConnection(session)) {
                        try (Statement statement = connection.createStatement()) {
                                return executeUpdateCallback.executeUpdate(connection, statement);
                        }
                } catch (SQLException e) {
                        throw new SystemRuntimeException(e);
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
         * @throws SystemRuntimeException 如果发生 {@link SQLException}
         * @see Statement#executeQuery(String)
         */
        public DataGrid executeQuery(Session session, StatementExecuteQueryCallback executeQueryCallback)
        {
                try (Connection connection = getConnection(session)) {
                        try (Statement statement = connection.createStatement()) {
                                return executeQueryCallback.executeQuery(connection, statement);
                        }
                } catch (SQLException e) {
                        throw new SystemRuntimeException(e);
                }
        }
}
