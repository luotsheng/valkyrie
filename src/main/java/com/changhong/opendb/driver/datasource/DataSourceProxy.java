package com.changhong.opendb.driver.datasource;

import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.TableInfo;
import com.changhong.opendb.model.ConnectionInfo;
import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.JSONUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public abstract class DataSourceProxy
        implements DataSource, AutoCloseable
{
        private final HikariDataSource ds;

        public DataSourceProxy(ConnectionInfo info)
        {
                HikariConfig conf = new HikariConfig();

                conf.setJdbcUrl(info.getJdbcUrl());
                conf.setUsername(info.getUsername());
                conf.setPassword(info.getPassword());

                conf.setMaximumPoolSize(16);
                conf.setMinimumIdle(1);
                conf.setConnectionTimeout(5000);

                ds = new HikariDataSource(conf);
        }

        /**
         * 选择数据库
         */
        public abstract Statement use(Connection connection, String database)
                throws SQLException;

        /**
         * 获取数据库列表
         */
        public abstract List<String> getDatabases();

        /**
         * 获取表
         */
        public abstract List<TableInfo> getTables(String database);

        public JdbcTemplate newJdbcTemplate()
        {
                return new JdbcTemplate(this);
        }

        /**
         * 结果集转Java对象
         */
        public static <T> List<T> toJavaList(ResultSet rs, Class<T> aClass)
        {
                try {
                        List<Map<String, Object>> rows = new ArrayList<>();

                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        while (rs.next()) {
                                Map<String, Object> row = new HashMap<>();

                                for (int i = 1; i < columnCount + 1; i++) {
                                        Object object = rs.getObject(i);

                                        if (object instanceof LocalDateTime localDateTime) {
                                                java.util.Date date =
                                                        Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                                                row.put(metaData.getColumnLabel(i), date);
                                        } else {
                                                row.put(metaData.getColumnLabel(i), object);
                                        }

                                }

                                rows.add(row);
                        }

                        String jsonArray = JSONUtils.toJSONString(rows);

                        return JSONUtils.toJavaList(jsonArray, aClass);
                } catch (Exception e) {
                        Catcher.ithrow(e);
                        return null;
                }
        }

        /* ============================== DataSource proxy ============================= */

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
