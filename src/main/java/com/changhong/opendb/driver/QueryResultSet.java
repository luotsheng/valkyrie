package com.changhong.opendb.driver;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@Data
public class QueryResultSet
{
        private List<ColumnMetaData> columns;
        private List<List<String>> rows;
        private boolean editable = false;
        private boolean addable = false;

        public QueryResultSet(List<ColumnMetaData> columns,
                              List<List<String>> rows)
        {
                this.columns = columns;
                this.rows = rows;
        }
}
