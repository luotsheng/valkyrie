package com.changhong.driver.mysql;

import com.changhong.driver.api.Dialect;

/**
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public class MySQLDialect implements Dialect
{
        @Override
        public String limit(String sql, int off, int size)
        {
                return sql + " LIMIT " + size + " OFFSET " + off;
        }

        @Override
        public String quote(String identifier)
        {
                if (identifier.startsWith("`") && identifier.endsWith("`"))
                        return identifier;

                return "`" + identifier + "`";
        }
}
