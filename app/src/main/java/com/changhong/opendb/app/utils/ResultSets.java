package com.changhong.opendb.app.utils;

import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.Row;
import com.changhong.opendb.app.driver.MutableDataGrid;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结果集工具
 *
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
public class ResultSets
{
        private static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN);

        /**
         * 结果集转 Java 集合
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
                        return List.of();
                }
        }

        /**
         * 结果集转 QueryResultSet 对象
         */
        public static void toMutableDataGird(List<ColumnMetaData> columns,
                                            ResultSet rs, MutableDataGrid dst)
                throws SQLException
        {
                SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_PATTERN);

                List<Row> rows = new ArrayList<>();

                while (rs.next()) {

                        Row row = new Row();

                        for (int i = 1; i <= columns.size(); i++)
                                row.add(stringify(rs.getObject(i), sdf));

                        rows.add(row);
                }

                dst.setColumns(columns);
                dst.setRows(rows);
        }

        private static String stringify(Object val, SimpleDateFormat sdf)
        {
                if (val == null)
                        return null;

                return switch (val) {
                        case java.sql.Timestamp ts -> ts.toLocalDateTime().format(formatter);
                        case java.util.Date date -> sdf.format(date);
                        case java.time.LocalDateTime date -> date.format(formatter);
                        case byte[] bits -> toBInary(bits);
                        default -> val.toString();
                };
        }

        private static String toBInary(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                        sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF))
                                .replace(' ', '0'));
                }
                return sb.toString();
        }
}
