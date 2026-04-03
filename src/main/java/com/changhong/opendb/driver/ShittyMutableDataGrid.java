package com.changhong.opendb.driver;

import com.changhong.opendb.driver.executor.SQLExecutor;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

import java.util.*;
import java.util.stream.Collectors;

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

        public interface UpdateListener
        {
                void update(Row row);
        }

        @Setter
        private UpdateListener updateListener;

        public ShittyMutableDataGrid(SQL origin, SQLExecutor executor)
        {
                this.origin = origin;
                this.executor = executor;
        }

        public void reload()
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
                if (colIndex >= rows.size() || colIndex < 0)
                        return;

                Row row = new Row();

                if (updateRowBuffer.containsKey(rowIndex)) {
                        row.addAll(updateRowBuffer.get(rowIndex));
                } else {
                        row.addAll(rows.get(rowIndex));
                }

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

                        try {

                                SQL sql = toUpdateSQL();
                                executor.execute(sql);

                        } finally {

                                reload();
                                updateRowBuffer.clear();

                        }
                }
        }

        private SQL toUpdateSQL()
        {
                List<Update> updates = new ArrayList<>();

                for (Map.Entry<Integer, Row> entry : updateRowBuffer.entrySet()) {

                        var update = new Update();
                        var row = entry.getValue();

                        var table = new Table(origin.getOnlyTable());
                        update.setTable(table);

                        for (int i = 0; i < row.size(); i++) {

                                String v = row.get(i);

                                if (!Objects.equals(v, rows.get(entry.getKey()).get(i))) {

                                        var c = new Column(columns.get(i).getName());

                                        Expression exp;

                                        if (v != null) {
                                                exp = new StringValue(v);
                                        } else {
                                                exp = new NullValue();
                                        }

                                        update.addUpdateSet(c, exp);

                                }

                        }

                        List<ColumnMetaData> pks = columns.stream()
                                .filter(ColumnMetaData::isPrimary)
                                .toList();

                        for (ColumnMetaData pk : pks) {

                                var r = rows.get(entry.getKey());
                                var c = new Column(pk.getName());
                                var v = new StringValue(r.get(pk.getIndex()));
                                var w = new EqualsTo();

                                w.setLeftExpression(c);
                                w.setRightExpression(v);

                                update.setWhere(w);

                        }

                        updates.add(update);
                }

                StringBuilder builder = new StringBuilder();

                for (Update update : updates)
                        builder.append(update.toString()).append(";");

                return new SQL(-1L, origin.getDb(), builder.toString());
        }

}
