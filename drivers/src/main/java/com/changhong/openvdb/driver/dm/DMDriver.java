package com.changhong.openvdb.driver.dm;

import com.changhong.openvdb.driver.api.*;
import com.changhong.openvdb.driver.api.exception.DriverException;
import com.changhong.openvdb.driver.api.sql.SQL;
import com.changhong.utils.Captor;
import com.changhong.utils.collection.Lists;
import com.changhong.utils.collection.Sets;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.changhong.utils.TypeConverter.atobool;
import static com.changhong.utils.TypeConverter.atos;
import static com.changhong.utils.collection.Lists.beg;
import static com.changhong.utils.string.StaticLibrary.*;
import static com.changhong.utils.string.StaticLibrary.fmt;

/**
 * @author Luo Tiansheng
 * @since 2026/4/13
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class DMDriver extends Driver
{
        public DMDriver(DataSource dataSource)
        {
                super(dataSource);
        }

        @Override
        public DbType getType()
        {
                return DbType.dm;
        }

        @Override
        protected Dialect createDialect()
        {
                return new DMDialect();
        }

        @Override
        public List<String> getCatalogs()
        {
                /* 达梦没有 CATALOG 概念，只有 SCHEMA 模式的概念，所以将 CATALOG
                   映射为 SCHEMA 方便接口统一 */
                return getSchemas();
        }

        @Override
        public String showCreateTable(Session session, String table)
        {
                DataGrid dataGrid = execute(session, new SQL(
                        fmt("""
                                SELECT DBMS_METADATA.GET_DDL('TABLE', '%s', '%s') AS "DDL" FROM DUAL
                                """, table, session.schema())
                ));

                return beg(beg(dataGrid.getRows()));
        }

        @Override
        public List<Table> getTables(Session session)
        {
                List<Table> tables = Lists.newArrayList();

                try (Connection connection = getConnection(session);
                     Statement statement = connection.createStatement()) {
                        String sql = """
                                    SELECT
                                        t.TABLE_NAME AS name,
                                        o.CRTDATE AS createTime,
                                        TABLE_ROWCOUNT('%s', t.TABLE_NAME) AS "rows",
                                        TABLE_USED_SPACE('%s', t.TABLE_NAME) AS usedPages,
                                        c.COMMENTS AS "comment",
                                        (SELECT PARA_VALUE FROM V$DM_INI WHERE PARA_NAME = 'GLOBAL_PAGE_SIZE') AS pageSize
                                    FROM
                                        ALL_TABLES t
                                        LEFT JOIN ALL_TAB_COMMENTS c
                                            ON t.OWNER = c.OWNER AND t.TABLE_NAME = c.TABLE_NAME
                                        LEFT JOIN SYS.SYSOBJECTS o
                                            ON o.NAME = t.TABLE_NAME
                                            AND o.SCHID = (SELECT ID FROM SYS.SYSOBJECTS WHERE NAME = '%s' AND TYPE$ = 'SCH')
                                            AND o.SUBTYPE$ IN ('UTAB', 'STAB')
                                    WHERE
                                        t.OWNER = '%s'
                                """.formatted(session.schema(), session.schema(), session.schema(), session.schema());

                        try (var rs = statement.executeQuery(sql)) {
                                int pageSize = 0;
                                boolean pageSizeFetched = false;

                                while (rs.next()) {
                                        // 获取页大小（同一结果集中每行都相同，只需取一次）
                                        if (!pageSizeFetched) {
                                                pageSize = rs.getInt("pageSize");
                                                pageSizeFetched = true;
                                        }

                                        // 计算表大小（KB）
                                        long usedPages = rs.getLong("usedPages");
                                        float sizeInKB = (usedPages * pageSize) / 1024.0f;

                                        // 处理创建时间
                                        Timestamp createTimestamp = rs.getTimestamp("createTime");
                                        Date createDate = createTimestamp != null ? new Date(createTimestamp.getTime()) : null;

                                        tables.add(new Table(
                                                rs.getString("name"),
                                                createDate,
                                                null,
                                                null,
                                                sizeInKB,
                                                rs.getInt("rows"),
                                                rs.getString("comment")
                                        ));
                                }
                        }
                } catch (SQLException e) {
                        throw new DriverException(e);
                }

                return tables;
        }

        @Override
        public List<Index> getIndexes(Session session, String table)
        {
                var sql = """
                        SELECT
                          I.INDEX_NAME,
                          WM_CONCAT(IC.COLUMN_NAME) AS COLUMNS_TEXT,
                          CASE
                              WHEN I.UNIQUENESS = 'UNIQUE' AND I.INDEX_TYPE = 'NORMAL' THEN 'UNIQUE'
                              ELSE UPPER(I.INDEX_TYPE)
                          END AS INDEX_TYPE,
                          CASE WHEN I.STATUS = 'VALID' THEN 'ON' ELSE 'OFF' END AS VISIBLE
                        FROM
                          USER_INDEXES I
                          JOIN USER_IND_COLUMNS IC
                              ON I.INDEX_NAME = IC.INDEX_NAME
                              AND I.TABLE_NAME = IC.TABLE_NAME
                        WHERE
                          I.TABLE_NAME = '%s'
                          AND I.INDEX_NAME NOT LIKE 'ROWID%%'
                          AND I.INDEX_NAME NOT IN (
                            SELECT INDEX_NAME FROM USER_CONSTRAINTS
                              WHERE TABLE_NAME = '%s'
                                AND CONSTRAINT_TYPE = 'P'
                          )
                        GROUP BY
                          I.INDEX_NAME, I.UNIQUENESS, I.INDEX_TYPE, I.STATUS
                        ;
                        """;

                DataGrid dataGrid = execute(session, sql, table, table);

                List<Index> indexes = Lists.newArrayList();
                for (int i = 0; i < dataGrid.size(); i++) {
                        Index index = new Index();
                        index.setName(dataGrid.getRowValue(i, 0));
                        index.setColumnsText(dataGrid.getRowValue(i, 1));
                        index.setType(dataGrid.getRowValue(i, 2));
                        index.setVisible(atobool(dataGrid.getRowValue(0, 3)));
                        index.setOriginalName(index.getName());
                        index.setOriginalVisible(index.isVisible());
                        index.finalIntegrityCode();
                        indexes.add(index);
                }

                return indexes;
        }

        @Override
        public Set<String> getIndexTypes()
        {
                return Sets.newLinkedHashSet(
                        "NORMAL",
                        "UNIQUE",
                        "BITMAP",
                        "CLUSTER"
                );
        }

        @Override
        public void dropTable(Session session, String table)
        {
                execute(session, "DROP TABLE %s", dialect.quote(table));
        }

        @Override
        public void dropColumns(Session session, String table, Collection<Column> columns)
        {

        }

        @Override
        public void dropIndexKeys(Session session, String table, Collection<Index> selectionItems)
        {
                StringBuilder batch = new StringBuilder();

                for (Index selectionItem : selectionItems) {
                        var sqlText = fmt("DROP INDEX IF EXISTS %s.%s;",
                                dialect.quote(session.schema()),
                                dialect.quote(selectionItem.getName()));
                        batch.append(sqlText);
                }

                execute(session, batch);
        }

        @SuppressWarnings("TrailingWhitespacesInTextBlock")
        private String getConstraintId(Session session, String table)
        {
                var sql = fmt("""
                        SELECT
                          CONSTRAINT_NAME
                        FROM
                          USER_CONSTRAINTS 
                        WHERE 1=1
                          AND TABLE_NAME = '%s' 
                          AND CONSTRAINT_TYPE = 'P'
                          AND INDEX_OWNER = '%s'
                        ;
                        """, table, session.schema());

                DataGrid dataGrid = execute(session, sql);
                return dataGrid.getRowValue(0, 0);
        }

        @Override
        public void dropPrimaryKey(Session session, String table)
        {
                String constraintId = getConstraintId(session, table);

                if (strempty(constraintId))
                        return;

                String temp = "ALTER TABLE %s DROP CONSTRAINT %s;";
                var dropSql = fmt(temp, dialect.quote(table), dialect.quote(constraintId));

                execute(session, dropSql);
        }

        @Override
        public void addPrimaryKey(Session session, String table, Collection<Column> primaryKeys)
        {
                StringBuilder builder = new StringBuilder();

                builder.append(fmt("ALTER TABLE %s ADD CONSTRAINT PRIMARY KEY (", dialect.quote(table)));

                for (Column pk : primaryKeys)
                        builder.append(dialect.quote(pk.getName())).append(",");

                builder.delete(builder.length() - 1, builder.length());
                builder.append(");");

                execute(session, builder);
        }

        @Override
        public void alterIndexKeys(Session session, String table, Collection<Index> indexes)
        {

        }

        @Override
        public void alterChange(Session session, String table, Collection<Column> columns)
        {
                List<String> sqls = Lists.newArrayList();

                // DM 和 MySQL 列名修改是不同的语句，MySQL 支持一条语句同时修改
                // 列的信息和列名称。但 DM 不行，所以需要便利两次字段信息来分别不
                // 同执行列名的修改和列结构的修改
                for (Column column : columns) {
                        if (strne(column.getOriginalName(), column.getName())) {
                                sqls.add(fmt("ALTER TABLE %s RENAME COLUMN %s TO %s;",
                                        dialect.quote(table),
                                        dialect.quote(column.getOriginalName()),
                                        dialect.quote(column.getName())
                                ));
                        }
                }

                // 删除自增
                Captor.icall(() ->
                        execute(session, "ALTER TABLE %s DROP IDENTITY;", dialect.quote(table))
                );

                // 设置自增
                for (Column column : columns) {
                        if (column.isAutoIncrement()) {
                                var sqlfmt = "ALTER TABLE %s ADD COLUMN %s IDENTITY(1, 1);";
                                execute(session, sqlfmt, dialect.quote(table), dialect.quote(column.getName()));
                        }
                }

                // 配置列信息
                for (Column column : columns) {
                        // 达梦不允许修改自增列
                        if (column.isAutoIncrement())
                                continue;

                        // MODIFY
                        StringBuilder modifyBuilder = new StringBuilder();

                        modifyBuilder.append(fmt("ALTER TABLE %s MODIFY %s ",
                                dialect.quote(table),
                                dialect.quote(column.getName())
                        ));

                        modifyBuilder.append(column.getType());

                        modifyBuilder.append(column.isNotNull() ? " NOT NULL" : " NULL");

                        if (strnempty(column.getDefaultValue()))
                                modifyBuilder.append(" DEFAULT ").append(column.getDefaultValue());

                        modifyBuilder.append(";");

                        sqls.add(atos(modifyBuilder));

                        // COMMENT
                        var comment = column.getComment();
                        sqls.add(fmt("COMMENT ON COLUMN %s.%s IS '%s';",
                                dialect.quote(table),
                                dialect.quote(column.getName()),
                                strempty(comment) ? "" : comment));
                }

                /* 批量执行 */
                try (Connection connection = getConnection(session);
                     Statement statement = connection.createStatement()) {
                        if (sqls.isEmpty())
                                return;

                        for (String sql : sqls)
                                statement.addBatch(sql);

                        statement.executeBatch();
                } catch (SQLException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void alterVisible(Session session, String table, Collection<Index> indexes)
        {
                for (Index index : indexes) {
                        execute(session, "ALTER INDEX %s %s;",
                                dialect.quote(index.getName()),
                                index.isVisible() ? "VISIBLE" : "INVISIBLE");
                }
        }
}
