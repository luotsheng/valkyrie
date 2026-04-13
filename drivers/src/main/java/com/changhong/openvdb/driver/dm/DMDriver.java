package com.changhong.openvdb.driver.dm;

import com.changhong.openvdb.driver.api.*;
import com.changhong.openvdb.driver.api.exception.DriverException;
import com.changhong.openvdb.driver.api.sql.SQL;
import com.changhong.utils.collection.Lists;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.changhong.utils.collection.Lists.beg;
import static com.changhong.utils.string.StaticLibrary.strfmt;

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

                return beg(dataGrid.getRows()).get(0);
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
        public List<Column> getColumns(Session session, String table)
        {
                return List.of();
        }

        @Override
        public List<Index> getIndexes(Session session, Table table)
        {
                return List.of();
        }

        @Override
        public Set<String> getIndexTypes()
        {
                return Set.of();
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

        }

        @Override
        public void alterVisible(Session session, String table, Collection<Index> indexes)
        {

        }
}
