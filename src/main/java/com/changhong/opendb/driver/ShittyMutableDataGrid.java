package com.changhong.opendb.driver;

import com.changhong.opendb.driver.executor.SQLExecutor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
public class ShittyMutableDataGrid
{
        @Setter
        @Getter
        private List<ColumnMetaData> columns;

        @Setter
        @Getter
        private List<Row> rows;

        @Setter
        @Getter
        private boolean editable = false;

        @Setter
        @Getter
        private boolean addable = false;

        private final Map<Integer, Row> updateRowBuffer = new HashMap<>();

        private final SQL origin;
        private final SQLExecutor executor;

        public interface UpdateListener {
                void update(Row row);
        }

        @Setter
        private UpdateListener updateListener;

        public ShittyMutableDataGrid(SQL origin, SQLExecutor executor)
        {
                this.origin = origin;
                this.executor = executor;
        }

        public void refresh()
        {
                if (executor != null && origin != null) {

                        ShittyMutableDataGrid grid = executor.execute(origin);

                        columns = grid.columns;
                        rows = grid.rows;

                        clearUpdateBuffer();

                }
        }

        public void addEmptyRow()
        {
                rows.addLast(new Row(columns.size()));
        }

        public void addUpdateRow(int colIndex, int rowIndex, String newValue)
        {
                Row row = new Row();
                row.addAll(rows.get(rowIndex));
                row.set(colIndex, newValue);

                updateRowBuffer.put(rowIndex, row);

                if (updateListener != null)
                        updateListener.update(row);

        }

        public boolean isEmptyUpdateBuffer()
        {
                return !updateRowBuffer.isEmpty();
        }

        public void clearUpdateBuffer()
        {
                updateRowBuffer.clear();
        }

        /**
         * 刷新行更新缓冲区
         */
        public void flushUpdateBuffer()
        {
                if (isEmptyUpdateBuffer()) {
                        updateRowBuffer.clear();
                }
        }
}
