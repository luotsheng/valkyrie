package com.changhong.driver.utils;

import com.changhong.driver.api.DataGrid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
         * 结果集转 QueryResultSet 对象
         */
        public static void toDataGrid(ResultSet rs)
                throws SQLException
        {
                SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_PATTERN);
                DataGrid dataGrid = new DataGrid();

                

        }

        private static String stringify(Object val, SimpleDateFormat sdf)
        {
                if (val == null)
                        return null;

                return switch (val) {
                        case java.sql.Timestamp ts -> ts.toLocalDateTime().format(formatter);
                        case java.util.Date date -> sdf.format(date);
                        case LocalDateTime date -> date.format(formatter);
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
