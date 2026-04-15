package com.changhong.openvdb.driver.dm;

import com.changhong.openvdb.driver.api.*;
import com.changhong.openvdb.driver.api.exception.DriverException;
import com.changhong.openvdb.driver.api.sql.SQL;
import com.changhong.utils.collection.Lists;
import com.changhong.utils.collection.Sets;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.changhong.utils.TypeConverter.atos;
import static com.changhong.utils.collection.Lists.beg;
import static com.changhong.utils.string.StaticLibrary.*;

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
                        strfmt("""
                                SELECT DBMS_METADATA.GET_DDL('TABLE', '%s', '%s') AS "DDL" FROM DUAL
                                """, table, session.schema())
                ));

                return beg(beg(dataGrid.getRows()));
        }

        @Override
        public List<Table> getTables(Session session)
        {
                List<Table> tables = Lists.newArrayList();

                execute(session, (connection, statement) -> {
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
                        } catch (SQLException e) {
                                throw new DriverException(e);
                        }
                });

                return tables;
        }

        @Override
        public List<Index> getIndexes(Session session, Table table)
        {
                return List.of();
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

        }

        @Override
        public void dropColumns(Session session, String table, Collection<Column> columns)
        {

        }

        @Override
        public void dropIndexKeys(Session session, String table, Collection<Index> selectionItems)
        {

        }

        @Override
        public void dropPrimaryKey(Session session, String table)
        {

        }

        @Override
        public void alterPrimaryKey(Session session, String table, Collection<Column> primaryKeys)
        {

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
                                sqls.add(strfmt("ALTER TABLE %s RENAME COLUMN %s TO %s;",
                                        dialect.quote(table),
                                        dialect.quote(column.getOriginalName()),
                                        dialect.quote(column.getName())
                                ));
                        }
                }

                // 配置列信息
                for (Column column : columns) {
                        // MODIFY
                        StringBuilder modifyBuilder = new StringBuilder();

                        modifyBuilder.append(strfmt("ALTER TABLE %s MODIFY %s ",
                                dialect.quote(table),
                                dialect.quote(column.getName())
                        ));

                        modifyBuilder.append(column.getType());

                        if (column.isAutoIncrement())
                                modifyBuilder.append(" IDENTITY(1, 1) ");

                        modifyBuilder.append(!column.isNullable() ? " NOT NULL" : " NULL");

                        if (strnempty(column.getDefaultValue()))
                                modifyBuilder.append(" DEFAULT ").append(column.getDefaultValue());

                        modifyBuilder.append(";");

                        sqls.add(atos(modifyBuilder));

                        // COMMENT
                        var comment = column.getComment();
                        sqls.add(strfmt("COMMENT ON COLUMN %s.%s IS '%s';",
                                dialect.quote(table),
                                dialect.quote(column.getName()),
                                strempty(comment) ? "" : comment));
                }

                /* 批量执行 */
                executeBatch(session, ((connection, statement) -> {
                        if (sqls.isEmpty())
                                return new int[] {0};

                        for (String sql : sqls)
                                statement.addBatch(sql);

                        return statement.executeBatch();
                }));
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
