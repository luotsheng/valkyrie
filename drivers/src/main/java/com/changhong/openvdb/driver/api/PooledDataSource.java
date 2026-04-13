package com.changhong.openvdb.driver.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 带池化功能的连接池数据源对象
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public class PooledDataSource
        implements DataSource, AutoCloseable
{
        private final HikariDataSource ds;

        public PooledDataSource(ConnectionConfig connectionConfig)
        {
                HikariConfig conf = new HikariConfig();

                conf.setJdbcUrl(connectionConfig.getJdbcUrl());
                conf.setUsername(connectionConfig.getUsername());
                conf.setPassword(connectionConfig.getPassword());

                conf.setMaximumPoolSize(16);
                conf.setMinimumIdle(1);
                conf.setConnectionTimeout(30000);

                ds = new HikariDataSource(conf);
        }

        /* ******************************************************************************** */
        /*                            DATASOURCE PROXY IMPLEMENTS                           */
        /* ******************************************************************************** */

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
