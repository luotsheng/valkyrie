package valkyrie.driver.api;

import valkyrie.driver.api.sql.SQL;
import valkyrie.utils.Optional;
import valkyrie.utils.collection.Lists;
import valkyrie.utils.collection.Maps;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.update.Update;

import java.util.*;

/**
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
@Getter
@Setter
public class DataGrid
{
        @Getter
        private List<Column> columns;

        private final Map<String, Integer> columnIndices = Maps.newHashMap();

        private List<Column> pks;

        @Setter
        @Getter
        private List<GridRow> rows = Lists.newArrayList();

        @Setter
        @Getter
        private boolean editable = false;

        @Setter
        @Getter
        private boolean addable = false;

        private final Session session;
        private final Driver driver;

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
        public DataGrid(Session session, Driver driver, SQL sql)
        {
                this.session = session;
                this.driver = driver;
                this.sql = sql;
        }
        
        public static DataGrid ofValue(Session session, String value)
        {
                DataGrid dataGrid = new DataGrid(session, null, null);
                
                Column col = new Column();
                col.setLabel("Value");
                col.setName("Value");
                col.setType("Object");

                dataGrid.setColumns(Lists.of(col));
                dataGrid.addEmptyRow();
                dataGrid.getRows().getFirst().set(0, value);

                return dataGrid;
        }

        public static DataGrid ofList(Session session, List<String> list)
        {
                DataGrid dataGrid = new DataGrid(session, null, null);

                Column col = new Column();
                col.setLabel("Value");
                col.setName("Value");
                col.setType("ANY");

                dataGrid.setColumns(Lists.of(col));

                list.forEach(e -> {
                        GridRow row = new GridRow();
                        row.add(e);
                        dataGrid.rows.add(row);
                });

                return dataGrid;
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
                if (driver != null && session != null && sql != null) {

                        DataGrid dataGrid = driver.execute(session, sql);

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
                driver.execute(session, sql);
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
                                driver.execute(session, sql);

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
                        List<EqualsTo> equals = new ArrayList<>();

                        var table = new Table(driver.getDialect().removeQuote(sql.getSingleTableName()));
                        delete.setTable(table);

                        List<Column> whereColumns = columns;

                        if (!pks.isEmpty())
                                whereColumns = pks;

                        whereColumns.forEach(col -> {

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

                        if (pks.isEmpty()) {
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

                        var table = new Table(driver.getDialect().removeQuote(sql.getSingleTableName()));
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

                        List<Column> whereColumns = columns;

                        if (!pks.isEmpty())
                                whereColumns = pks;

                        for (Column col : whereColumns) {

                                var r = rows.get(entry.getKey());
                                var c = new net.sf.jsqlparser.schema.Column(col.getName());
                                var v = new StringValue(r.get(col.getIndex()));
                                var w = new EqualsTo();

                                w.setLeftExpression(c);
                                w.setRightExpression(v);

                                update.setWhere(w);

                        }

                        /* 如果没有主键只修改一条 */
                        if (pks.isEmpty()) {
                                Limit limit = new Limit();
                                limit.setRowCount(new LongValue(1));
                                update.setLimit(limit);
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
         * @param index 行索引
         * @param columnName 字段名
         * @return 对应行列值
         */
        public String getRowValue(int index, String columnName)
        {
                return getRowValue(index, columnIndices.get(columnName));
        }

        /**
         * 根据列名获取指定行数据
         *
         * @param index 行索引
         * @param col 列索引
         * @return 对应行列值
         */
        public String getRowValue(int index, int col)
        {
                return Optional.ifError(() -> rows.get(index).get(col), null);
        }
}
