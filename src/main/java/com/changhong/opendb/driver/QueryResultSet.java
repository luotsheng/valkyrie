package com.changhong.opendb.driver;

import lombok.Data;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@Data
public class QueryResultSet
{
        private List<String> columns;
        private List<List<String>> rows;

        public QueryResultSet(List<String> columns, List<List<String>> rows)
        {
                this.columns = columns;
                this.rows = rows;
        }
}
