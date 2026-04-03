package com.changhong.opendb.app.driver.datasource;

import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.driver.executor.MySQLExecutor;
import com.changhong.opendb.app.model.ConnectionInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings({
        "SqlSourceToSinkFlow",
        "SqlDialectInspection",
        "SqlNoDataSourceInspection"
})
public class MySQLDataSource extends VirtualDataSource
{
        public MySQLDataSource(ConnectionInfo info)
        {
                super(info);
        }

        @Override
        public Statement use(Connection connection, String database) throws SQLException
        {
                Statement statement = connection.createStatement();
                statement.execute("USE " + database + ";");
                return statement;
        }

        @Override
        public SQLExecutor newSQLExecutor(String name)
        {
                return new MySQLExecutor(name, this);
        }
}
