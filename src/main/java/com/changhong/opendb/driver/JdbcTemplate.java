package com.changhong.opendb.driver;

import com.changhong.opendb.driver.datasource.DataSourceProxy;
import com.changhong.opendb.utils.Catcher;
import org.w3c.dom.CDATASection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
public class JdbcTemplate
{
        private final DataSourceProxy ds;

        public JdbcTemplate(DataSourceProxy ds)
        {
                this.ds = ds;
        }

        /**
         * 获取数据库列表
         */
        public List<String> getDatabases()
        {
                return ds.getDatabases();
        }

        /**
         * 获取表
         */
        public List<TableInfo> getTables(String database)
        {
                return ds.getTables(database);
        }

        @SuppressWarnings("SqlSourceToSinkFlow")
        public QueryResultSet selectByPage(String database, String table, int start, int size)
        {
                List<String> columns = new ArrayList<>();
                List<List<String>> rows = new ArrayList<>();

                String sql = strfmt("SELECT * FROM %s LIMIT %d OFFSET %d;", table, size, start);

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, database);
                     ResultSet rs = statement.executeQuery(sql)) {

                        ResultSetMetaData meta = rs.getMetaData();
                        int colCount = meta.getColumnCount();

                        for (int i = 1; i <= colCount; i++)
                                columns.add(meta.getColumnLabel(i));

                        while (rs.next()) {
                                List<String> row = new ArrayList<>();
                                for (int i = 1; i <= colCount; i++) {
                                        Object val = rs.getObject(i);
                                        row.add(val != null ? val.toString() : null);
                                }
                                rows.add(row);
                        }

                        return new QueryResultSet(columns, rows);
                } catch (Throwable e) {
                        Catcher.ithrow(e);
                        return null;
                }
        }

}
