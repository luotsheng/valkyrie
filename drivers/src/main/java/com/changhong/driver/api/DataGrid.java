package com.changhong.driver.api;

import com.changhong.collection.Maps;
import com.changhong.driver.api.sql.SQL;
import com.changhong.driver.api.sql.SQLExecutor;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.update.Update;

import java.util.*;

/**
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
@Getter
@Setter
@SuppressWarnings("DuplicatedCode")
public class DataGrid
{
        @Getter
        private List<Column> columns;

        private final Map<String, Integer> columnIndices = Maps.newHashMap();

        private List<Column> pks;

        @Setter
        @Getter
        private List<GridRow> rows;

        @Setter
        @Getter
        private boolean editable = false;

        @Setter
        @Getter
        private boolean addable = false;

        private final Session session;
        private final SQLExecutor executor;

        private final Map<Integer, GridRow> updateRowBuffer = new HashMap<>();

        private final SQL sql;

        public interface UpdateListener
        {
                void update(GridRow row);
        }

        @Setter
        private UpdateListener updateListener;

        /**
         * 构造器
         */
        public DataGrid(Session session, SQLExecutor executor, SQL sql)
        {
                this.session = session;
                this.executor = executor;
                this.sql = sql;
        }

        public int size()
        {
                return rows.size();
        }

        public void setColumns(List<Column> columns)
        {
                this.columns = columns;

                pks = columns.stream()
                        .filter(Column::isPrimary)
                        .toList();

                for (int i = 0; i < columns.size(); i++)
                        columnIndices.put(columns.get(i).getLabel(), i);
        }


        public void reload()
        {
                if (executor != null && session != null && sql != null) {

                        DataGrid dataGrid = executor.execute(session, sql);

                        columns = dataGrid.columns;
                        rows = dataGrid.rows;

                        clearUpdateBuffer();

                }
        }

        public void addEmptyRow()
        {
                rows.addLast(new GridRow(columns.size()));
        }

        public void remove(List<Integer> indices)
        {
                if (indices == null || indices.isEmpty())
                        return;

                SQL sql = toDeleteSQL(indices);
                executor.execute(session, sql);
        }

        public void addUpdateRow(int colIndex, int rowIndex, String newValue)
        {
                if (rowIndex < 0)
                        return;

                GridRow row = new GridRow();

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
                                executor.execute(session, sql);

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

                        var table = new Table(sql.getSingleTableName());
                        delete.setTable(table);

                        if (!pks.isEmpty()) {

                                pks.forEach(pk -> {

                                        var c = new net.sf.jsqlparser.schema.Column(pk.getName());
                                        var v = new StringValue(rows.get(index).get(pk.getIndex()));
                                        var w = new EqualsTo();

                                        w.setLeftExpression(c);
                                        w.setRightExpression(v);

                                        delete.setWhere(w);

                                });

                        } else {

                                List<EqualsTo> equals = new ArrayList<>();

                                columns.forEach(col -> {

                                        var c = new net.sf.jsqlparser.schema.Column(col.getName());
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

                return new SQL(builder.toString());
        }

        private SQL toUpdateSQL()
        {
                List<Update> updates = new ArrayList<>();

                for (Map.Entry<Integer, GridRow> entry : updateRowBuffer.entrySet()) {

                        var update = new Update();
                        var row = entry.getValue();

                        var table = new Table(sql.getSingleTableName());
                        update.setTable(table);

                        for (int i = 0; i < row.size(); i++) {

                                String v = row.get(i);

                                if (!Objects.equals(v, rows.get(entry.getKey()).get(i))) {

                                        var c = new net.sf.jsqlparser.schema.Column(columns.get(i).getName());

                                        Expression exp;

                                        if (v != null) {
                                                exp = new StringValue(v);
                                        } else {
                                                exp = new NullValue();
                                        }

                                        update.addUpdateSet(c, exp);

                                }

                        }

                        for (Column pk : pks) {

                                var r = rows.get(entry.getKey());
                                var c = new net.sf.jsqlparser.schema.Column(pk.getName());
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

                return new SQL(builder.toString());
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
