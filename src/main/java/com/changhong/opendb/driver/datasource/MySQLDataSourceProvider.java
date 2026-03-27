package com.changhong.opendb.driver.datasource;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.driver.Table;
import com.changhong.opendb.model.ConnectionInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings({
        "SqlNoDataSourceInspection",
        "SqlSourceToSinkFlow"
})
public class MySQLDataSourceProvider extends DataSourceProvider
{
        public MySQLDataSourceProvider(ConnectionInfo info)
        {
                super(info);
        }

        @Override
        protected Statement use(Connection connection, String database) throws SQLException
        {
                Statement statement = connection.createStatement();
                statement.execute("USE " + database + ";");
                return statement;
        }

        @Override
        public List<String> getDatabases()
        {
                String sql = "SHOW DATABASES;";

                try (Connection connection = getConnection();
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
        public List<Table> getTables(String database)
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
                """, database);

                try (Connection connection = getConnection();
                     Statement statement = use(connection, database);) {
                        List<String> ret = new ArrayList<>();

                        ResultSet rs = statement.executeQuery(sql);

                        return toJavaList(rs, Table.class);
                } catch (SQLException e) {
                        EventBus.publish(e);
                }

                return List.of();
        }
}
