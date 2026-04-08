package com.changhong.opendb.app.driver;

import com.changhong.collection.Maps;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.driver.sql.SQL;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.update.Update;

import java.util.*;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
public class MutableDataGrid
{
        @Getter
        private List<ColumnMetaData> columns;

        private final Map<String, Integer> columnIndices = Maps.newHashMap();

        private List<ColumnMetaData> pks;

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

        /**
         * 构造器
         */
        public MutableDataGrid(SQL origin, SQLExecutor executor)
        {
                this.origin = origin;
                this.executor = executor;
        }

        public int size()
        {
                return rows.size();
        }

        public void setColumns(List<ColumnMetaData> columns)
        {
                this.columns = columns;

                pks = columns.stream()
                        .filter(ColumnMetaData::isPrimary)
                        .toList();

                for (int i = 0; i < columns.size(); i++)
                        columnIndices.put(columns.get(i).getLabel(), i);
        }

        public void reload()
        {
                if (executor != null && origin != null) {

                        MutableDataGrid grid = executor.execute(origin);

                        columns = grid.columns;
                        rows = grid.rows;

                        clearUpdateBuffer();

                }
        }

        public void addEmptyRow()
        {
                rows.addLast(new Row(columns.size()));
        }

        public void remove(List<Integer> indices)
        {
                if (indices == null || indices.isEmpty())
                        return;

                SQL sql = toDeleteSQL(indices);
                executor.execute(sql);
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

        public boolean isUpdatable()
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
        public void update()
        {
                if (isUpdatable()) {

                        try {

                                SQL sql = toUpdateSQL();
                                executor.execute(sql);

                        } finally {

                                reload();
                                updateRowBuffer.clear();

                        }
                }
        }

        private SQL toDeleteSQL(List<Integer> indices)
        {
                List<Delete> deletes = new ArrayList<>();

                indices.forEach(index -> {

                        var delete = new Delete();

                        var table = new Table(origin.getSingleTable());
                        delete.setTable(table);

                        if (!pks.isEmpty()) {

                                pks.forEach(pk -> {

                                        var c = new Column(pk.getName());
                                        var v = new StringValue(rows.get(index).get(pk.getIndex()));
                                        var w = new EqualsTo();

                                        w.setLeftExpression(c);
                                        w.setRightExpression(v);

                                        delete.setWhere(w);

                                });

                        } else {

                                List<EqualsTo> equals = new ArrayList<>();

                                columns.forEach(col -> {

                                        var c = new Column(col.getName());
                                        var v = new StringValue(rows.get(index).get(col.getIndex()));
                                        var w = new EqualsTo();

                                        w.setLeftExpression(c);
                                        w.setRightExpression(v);

                                        equals.add(w);

                                });

                                Expression exp = equals.getFirst();

                                for (int i = 1; i < equals.size(); i++)
                                        exp = new AndExpression(exp, equals.get(i));

                                delete.setWhere(exp);

                                Limit limit = new Limit();
                                limit.setRowCount(new LongValue(1));

                                delete.setLimit(limit);

                        }

                        deletes.add(delete);
                });

                StringBuilder builder = new StringBuilder();

                for (Delete delete : deletes)
                        builder.append(delete.toString()).append(";");

                return new SQL(-1L, origin.getDb(), builder.toString());
        }

        private SQL toUpdateSQL()
        {
                List<Update> updates = new ArrayList<>();

                for (Map.Entry<Integer, Row> entry : updateRowBuffer.entrySet()) {

                        var update = new Update();
                        var row = entry.getValue();

                        var table = new Table(origin.getSingleTable());
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

        /**
         * 根据列名获取指定行数据
         *
         * @param columnName 字段名
         * @param index 行索引
         * @return 对应行列值
         */
        public String  getRowValue(String columnName, int index)
        {
                return rows.get(index).get(columnIndices.get(columnName));
        }

}
