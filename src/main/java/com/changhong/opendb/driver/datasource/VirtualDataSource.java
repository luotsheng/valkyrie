package com.changhong.opendb.driver.datasource;

import com.changhong.opendb.driver.executor.SQLExecutor;
import com.changhong.opendb.model.ConnectionInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * 代理数据源
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public abstract class VirtualDataSource
        implements DataSource, AutoCloseable
{
        private final HikariDataSource ds;

        public VirtualDataSource(ConnectionInfo info)
        {
                HikariConfig conf = new HikariConfig();

                conf.setJdbcUrl(info.getJdbcUrl());
                conf.setUsername(info.getUsername());
                conf.setPassword(info.getPassword());

                conf.setMaximumPoolSize(16);
                conf.setMinimumIdle(1);
                conf.setConnectionTimeout(30000);

                ds = new HikariDataSource(conf);
        }

        public abstract Statement use(Connection connection, String database)
                throws SQLException;

        /**
         * 创建 Jdbc 模板
         */
        public abstract SQLExecutor newSQLExecutor(String name);

        @Override
        public Connection getConnection() throws SQLException
        {
                return ds.getConnection();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException
        {
                return ds.getConnection();
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException
        {
                return ds.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException
        {
                ds.setLogWriter(out);
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException
        {
                ds.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException
        {
                return ds.getLoginTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException
        {
                return ds.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException
        {
                return ds.isWrapperFor(iface);
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException
        {
                return ds.getParentLogger();
        }

        @Override
        public void close() throws Exception
        {
                ds.close();
        }
}
