package com.changhong.driver.mysql;

import com.changhong.collection.Lists;
import com.changhong.driver.api.*;
import com.changhong.driver.api.Driver;
import com.changhong.driver.api.sql.SQLCommandType;
import com.changhong.driver.api.sql.SQLParsedStatement;
import com.changhong.driver.api.exception.DriverException;
import com.changhong.driver.api.sql.SQL;
import com.changhong.driver.api.sql.SQLExecutor;
import com.changhong.driver.utils.ResultSets;
import com.changhong.driver.utils.SQLUtils;
import com.changhong.utils.Captor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.changhong.collection.Lists.beg;
import static com.changhong.string.StringStaticize.strwfmt;

/**
 * MySQL 驱动层实现
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class MySQLDriver extends Driver implements SQLExecutor
{
        private final MySQLDialect dialect = new MySQLDialect();

        private final Map<Long, Statement> taskQueue = new ConcurrentHashMap<>();

        public MySQLDriver(DataSource dataSource)
        {
                super(dataSource);
        }

        @Override
        public String showCreateTable(Session session, String table)
        {
                DataGrid dataGrid = execute(-1, session, new SQL(
                        strwfmt("SHOW CREATE TABLE %s;", dialect.quote(table))
                ));

                return beg(dataGrid.getRows()).get(1);
        }

        @Override
        public List<Table> getTables(Session session)
        {
                List<Table> tables = Lists.newArrayList();

                execute(session, (connection, statement) -> {
                        String sql = strwfmt("""
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
                            	TABLE_SCHEMA = '%s';
                        """, session.catalog());

                        try (var rs = statement.executeQuery(sql)) {
                                while (rs.next()) {
                                        tables.add(new Table(
                                                rs.getString("name"),
                                                rs.getString("createTime"),
                                                rs.getString("updateTime"),
                                                rs.getString("engine"),
                                                rs.getFloat("size"),
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
                try {
                        String sql = strwfmt("SELECT * FROM %s", dialect.quote(table));
                        List<Column> columns = selectByPage(session, sql, 0, 0).getColumns();

                        Map<String, Column> columnMetaDataMap = new HashMap<>();

                        for (Column column : columns)
                                columnMetaDataMap.put(column.getName(), column);

                        String createTableDDL = showCreateTable(session, table);

                        SQLUtils.parseColumnDefSpec(createTableDDL, columnMetaDataMap);

                        /* 防篡改码生成 */
                        columns.forEach(Column::finalIntegrityCode);

                        return columns;
                } catch (Exception e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public DataGrid selectByPage(Session session, String sql, int off, int size)
        {
                return execute(-1, session, new SQL(dialect.limit(sql, off, size)));
        }

        @Override
        public DataGrid execute(long jobId, Session session, SQL sql)
        {
                return executeQuery(session, (connection, statement) -> {

                        taskQueue.put(jobId, statement);

                        SQLParsedStatement endStatement = sql.popupEnd();

                        for (SQLParsedStatement ps : sql) {
                                switch (ps.getCommand()) {
                                        case EXECUTE -> statement.execute(ps.toString());
                                        case EXECUTE_UPDATE -> statement.executeUpdate(ps.toString());
                                        case EXECUTE_QUERY -> {}
                                }
                        }

                        if (endStatement.getCommand() == SQLCommandType.EXECUTE_QUERY) {
                                ResultSet rs = statement.executeQuery(endStatement.toString());
                                return ResultSets.toDataGrid(connection, endStatement, rs);
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
