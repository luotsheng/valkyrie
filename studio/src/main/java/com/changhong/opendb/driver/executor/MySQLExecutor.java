package com.changhong.opendb.driver.executor;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.exception.CatcherException;
import com.changhong.opendb.driver.*;
import com.changhong.opendb.driver.datasource.VirtualDataSource;
import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.ResultSets;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        , "RedundantSuppression"})
public class MySQLExecutor extends SQLExecutor
{
        private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);

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
        public List<TableMetaData> tables(String db)
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
                        List<TableMetaData> metas = ResultSets.toJavaList(rs, TableMetaData.class);
                        metas.forEach(e -> e.setDatabase(db));
                        return metas;
                } catch (SQLException e) {
                        EventBus.publish(e);
                }

                return List.of();
        }

        @Override
        public List<ColumnMetaData> getColumns(TableMetaData table)
        {
                try {
                        return select(table, 0, 0).getColumns();
                } catch (Exception e) {
                        Catcher.ithrow(e);
                        return List.of();
                }
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
        @SuppressWarnings("resource")
        public MutableDataGrid execute(SQL sql, ExecuteCallback callback)
        {
                SQLParsedStatement current = null;

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, sql.getDb())) {

                        queue.put(sql.getTaskId(), statement);

                        for (SQLParsedStatement ps : sql) {

                                current = ps;
                                boolean skip = false;

                                LOG.info("Execute sql: \n{}", SqlFormatter.format(ps.getScript()));
                                
                                /* DQL 并且必须是最后一个 SQL 语句才执行查询 */
                                if (ps.getType() == SQLCommandType.DQL && ps.isLast()) {

                                        MutableDataGrid grid = executeQueryGrid(
                                                connection,
                                                statement,
                                                sql,
                                                ps
                                        );

                                        callback.doCallback(ps.getScript(), SQLExecutorStatus.OK);

                                        return grid;

                                }
                                
                                if (ps.getType() == SQLCommandType.DML) {
                                        statement.executeUpdate(ps.getScript());
                                        callback.doCallback(ps.getScript(), SQLExecutorStatus.OK);
                                        continue;
                                }

                                if (ps.getType() == SQLCommandType.DQL)
                                        skip = true;

                                statement.execute(ps.getScript());

                                callback.doCallback(ps.getScript(), skip ? SQLExecutorStatus.SKIP : SQLExecutorStatus.OK);
                        }

                        queue.remove(sql.getTaskId());

                } catch (SQLException e) {

                        LOG.error("MySQLExecutor execute error", e);

                        if (callback instanceof DefaultExecutorCallback) {

                                callback.doCallback(e.getMessage(), SQLExecutorStatus.ERROR);
                                return null;

                        }

                        if (current != null)
                                callback.doCallback(current.getScript(), SQLExecutorStatus.ERROR);

                        throw new CatcherException(e);

                }

                return null;
        }

        public MutableDataGrid select(TableMetaData tbMeta, int start, int size)
                throws SQLException
        {
                MutableDataGrid grid;

                String text = strfmt("SELECT * FROM %s LIMIT %d OFFSET %d;", tbMeta.getName(), size, start);

                SQL sql = new SQL(0L, tbMeta.getDatabase() , text);

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, tbMeta.getDatabase())) {
                        grid = executeQueryGrid(connection, statement, sql, sql.iterator().next());
                }

                grid.setEditable(true);
                grid.setAddable(true);

                return grid;
        }

        @Override
        public void cancel(Long id)
        {
                Catcher.tryCall(() -> {
                        if (queue.containsKey(id))
                                queue.get(id).cancel();
                });
        }

        private MutableDataGrid executeQueryGrid(Connection connection,
                                                 Statement statement,
                                                 SQL sql,
                                                 SQLParsedStatement ps) throws SQLException
        {
                ResultSet rs = statement.executeQuery(ps.getScript());

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

                if (ps.isOnlyOneTable()) {

                        Set<String> pks = new HashSet<>();
                        String onlyTable = ps.getOnlyTable();

                        try (ResultSet pk = dbMeta.getPrimaryKeys(sql.getDb(), connection.getSchema(), onlyTable)) {
                                while (pk.next())
                                        pks.add(pk.getString("COLUMN_NAME"));
                        }

                        pks.forEach(c -> {

                                ColumnMetaData meta = colMetas.get(c);
                                if (meta != null)
                                        meta.setPrimary(true);

                        });

                        Map<String, Map<String, Object>> columnInfo = new HashMap<>();

                        try (ResultSet col = dbMeta.getColumns(sql.getDb(), null, onlyTable, null)) {

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

                        editable = colMetas
                                .values()
                                .stream()
                                .anyMatch(ColumnMetaData::isPrimary);

                }

                MutableDataGrid grid = new MutableDataGrid(sql, this);
                ResultSets.toMutableDataGird(List.copyOf(colMetas.values()), rs, grid);
                grid.setEditable(editable);

                return grid;
        }
}
