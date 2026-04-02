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
        private boolean editable;

        public QueryResultSet(List<ColumnMetaData> columns,
                              List<List<String>> rows,
                              boolean editable)
        {
                this.columns = columns;
                this.rows = rows;
                this.editable = editable;
        }
}
