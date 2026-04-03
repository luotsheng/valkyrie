package com.changhong.opendb.driver;

import com.changhong.opendb.driver.executor.SQLExecutor;
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

        private final SQL origin;
        private final SQLExecutor executor;

        public QueryResultSet(SQL origin, SQLExecutor executor)
        {
                this.origin = origin;
                this.executor = executor;
        }

        public void refresh()
        {
                if (executor != null && origin != null) {
                        QueryResultSet refreshQRS = executor.execute(origin);
                        this.columns = refreshQRS.columns;
                        this.rows = refreshQRS.rows;
                }
        }

        public void addEmptyRow()
        {
                List<String> emptyRow = new ArrayList<>();
                for (ColumnMetaData ignored : columns)
                        emptyRow.add(null);
                rows.addLast(emptyRow);
        }
}
