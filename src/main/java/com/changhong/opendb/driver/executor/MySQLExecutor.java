package com.changhong.opendb.driver.executor;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.exception.CatcherException;
import com.changhong.opendb.driver.*;
import com.changhong.opendb.driver.datasource.VirtualDataSource;
import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.ResultSetUtils;
import net.sf.jsqlparser.JSQLParserException;

import java.sql.*;
import java.util.*;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({
        "SqlSourceToSinkFlow",
        "SqlDialectInspection",
        "SqlNoDataSourceInspection"
})
public class MySQLExecutor extends SQLExecutor
{
        public MySQLExecutor(String name, VirtualDataSource ds)
        {
                super(name, ds);
        }

        @Override
        public List<String> databases()
        {
                String sql = "SHOW DATABASES;";

                try (Connection connection = ds.getConnection();
                     Statement statement = connection.createStatement()) {
                        List<String> ret = new ArrayList<>();
                        ResultSet resultSet = statement.executeQuery(sql);

                        while (resultSet.next())
                                ret.add(resultSet.getString(1));

                        return ret;
                } catch (SQLException e) {
                        EventBus.publish(e);
                }

                return List.of();
        }

        @Override
        public List<TableMetadata> tables(String db)
        {
                String sql = strfmt("""
                    SELECT
                    	`TABLE_NAME` AS `name`,
                    	`CREATE_TIME` AS `createTime`,
                    	`UPDATE_TIME` AS `updateTime`,
                    	`ENGINE` AS `engine`,
                    	ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024, 2) AS `size`,
                    	`TABLE_ROWS` AS `rows`,
                    	`TABLE_COMMENT` AS `comment`
                    FROM
                    	information_schema.TABLES
                    WHERE
                    	TABLE_SCHEMA = '%s'
                """, db);

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, db)) {
                        ResultSet rs = statement.executeQuery(sql);
                        return ResultSetUtils.rs2jlist(rs, TableMetadata.class);
                } catch (SQLException e) {
                        EventBus.publish(e);
                }

                return List.of();
        }

        private QueryResultSet executeQuery(Connection connection,
                                            Statement statement,
                                            String db,
                                            String sql,
                                            SQLParsedStatement pm) throws SQLException
        {
                ResultSet rs = statement.executeQuery(sql);

                ResultSetMetaData rsMeta = rs.getMetaData();
                DatabaseMetaData dbMeta = connection.getMetaData();

                Map<String, ColumnMetaData> colMetas = new LinkedHashMap<>();

                for (int i = 1; i <= rsMeta.getColumnCount(); i++) {

                        ColumnMetaData c = new ColumnMetaData();

                        c.setIndex(i - 1);

                        c.setLabel(rsMeta.getColumnLabel(i));

                        c.setName(rsMeta.getColumnName(i));

                        c.setType(rsMeta.getColumnTypeName(i));

                        c.setJdbcType(rsMeta.getColumnType(i));

                        c.setLength(rsMeta.getPrecision(i));

                        c.setScale(rsMeta.getScale(i));

                        c.setNullable(
                                rsMeta.isNullable(i) == ResultSetMetaData.columnNullable
                        );

                        c.setTable(rsMeta.getTableName(i));

                        c.setSchema(rsMeta.getSchemaName(i));

                        colMetas.put(c.getName(), c);

                }

                boolean editable = false;

                if (pm != null && pm.isOnlyOneTable()) {

                        Set<String> pks = new HashSet<>();
                        String onlyTable = pm.getOnlyTable();

                        try (ResultSet pk = dbMeta.getPrimaryKeys(db, connection.getSchema(), onlyTable)) {
                                while (pk.next())
                                        pks.add(pk.getString("COLUMN_NAME"));
                        }

                        editable = !pks.isEmpty();

                        pks.forEach(c -> {

                                ColumnMetaData meta = colMetas.get(c);
                                if (meta != null)
                                        meta.setPrimary(true);

                        });

                        Map<String, Map<String, Object>> columnInfo = new HashMap<>();

                        try (ResultSet col = dbMeta.getColumns(db, null, onlyTable, null)) {

                                while (col.next()) {

                                        Map<String, Object> m = new HashMap<>();

                                        m.put("autoIncrement",
                                                "YES".equals(col.getString("IS_AUTOINCREMENT")));

                                        m.put("default",
                                                col.getString("COLUMN_DEF"));

                                        m.put("comment",
                                                col.getString("REMARKS"));

                                        columnInfo.put(col.getString("COLUMN_NAME"), m);

                                }

                        }

                        for (ColumnMetaData c : colMetas.values()) {
                                Map<String, Object> m = columnInfo.get(c.getName());

                                if (m == null)
                                        continue;

                                c.setAutoIncrement((Boolean) m.get("autoIncrement"));

                                c.setDefaultValue((String) m.get("default"));

                                c.setComment((String) m.get("comment"));
                        }

                }

                QueryResultSet qrs = ResultSetUtils.rs2qrs(List.copyOf(colMetas.values()), rs);

                qrs.setEditable(editable);

                return qrs;
        }

        @Override
        public void drop(String db, String name) throws SQLException
        {
                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, db)) {
                        statement.execute(strfmt("DROP TABLE `%s`;", name));
                }
        }

        @Override
        public QueryResultSet execute(SQL sql, ExecuteCallback callback)
        {
                SQLParsedStatement current = null;

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, sql.getDb())) {

                        queue.put(sql.getTaskId(), statement);

                        for (SQLParsedStatement stat : sql) {

                                current = stat;
                                boolean skip = false;

                                /* DQL 并且必须是最后一个 SQL 语句才执行查询 */
                                if (stat.getType() == SQLCommandType.DQL && stat.isLast()) {

                                        QueryResultSet qrs = executeQuery(
                                                connection,
                                                statement,
                                                sql.getDb(),
                                                stat.getScript(),
                                                stat
                                        );

                                        callback.doCallback(stat.getScript(), SQLExecutorStatus.OK);

                                        return qrs;

                                }

                                if (stat.getType() == SQLCommandType.DQL)
                                        skip = true;

                                statement.execute(stat.getScript());

                                callback.doCallback(stat.getScript(), skip ? SQLExecutorStatus.SKIP : SQLExecutorStatus.OK);
                        }

                        queue.remove(sql.getTaskId());

                } catch (SQLException e) {

                        if (current != null)
                                callback.doCallback(current.getScript(), SQLExecutorStatus.ERROR);

                        throw new CatcherException(e);

                }

                return null;
        }

        public QueryResultSet select(String db, TableMetadata tbMeta, int start, int size)
                throws SQLException
        {
                QueryResultSet qrs;

                String sql = strfmt("SELECT * FROM %s LIMIT %d OFFSET %d;", tbMeta.getName(), size, start);

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, db)) {
                        qrs = executeQuery(connection, statement, db, sql, new SQLParsedStatement(sql));
                }

                qrs.setEditable(true);
                qrs.setAddable(true);

                return qrs;
        }

        @Override
        public void cancel(Long id)
        {
                Catcher.tryCall(() -> {
                        if (queue.containsKey(id))
                                queue.get(id).cancel();
                });
        }
}
