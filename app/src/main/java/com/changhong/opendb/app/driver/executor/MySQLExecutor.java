package com.changhong.opendb.app.driver.executor;

import com.changhong.collection.Lists;
import com.changhong.exception.SystemRuntimeException;
import com.changhong.opendb.app.driver.*;
import com.changhong.opendb.app.driver.datasource.VirtualDataSource;
import com.changhong.opendb.app.driver.sql.SQL;
import com.changhong.opendb.app.driver.sql.SQLCommandType;
import com.changhong.opendb.app.driver.sql.SQLParsedStatement;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialog;
import com.changhong.opendb.app.utils.ResultSets;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

import static com.changhong.string.StringStaticize.*;
import static com.changhong.utils.TypeConverter.*;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({
        "SqlSourceToSinkFlow",
        "SqlDialectInspection",
        "SqlNoDataSourceInspection",
        "RedundantSuppression"
})
public class MySQLExecutor extends SQLExecutor
{
        private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);

        public MySQLExecutor(String name, VirtualDataSource ds)
        {
                super(name, ds);
        }

        @Override
        public List<String> getSchemas()
        {
                throw new UnsupportedOperationException("MySQL not supported yet.");
        }

        @Override
        public List<String> getDatabases()
        {
                String sql = "SHOW DATABASES;";

                try (Connection connection = ds.getConnection();
                     Statement statement = connection.createStatement()) {
                        List<String> ret = new ArrayList<>();
                        ResultSet resultSet = statement.executeQuery(sql);

                        while (resultSet.next())
                                ret.add(resultSet.getString(1));

                        return ret;
                } catch (SQLException e) {
                        VFXDialog.openError(e);
                }

                return List.of();
        }

        @Override
        public List<TableMetaData> getTables(String db)
        {
                String sql = strwfmt("""
                            SELECT
                            	`TABLE_NAME` AS `name`,
                            	`CREATE_TIME` AS `createTime`,
                            	`UPDATE_TIME` AS `updateTime`,
                            	`ENGINE` AS `engine`,
                            	ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024, 2) AS `size`,
                            	`TABLE_ROWS` AS `rows`,
                            	`TABLE_COMMENT` AS `comment`
                            FROM
                            	information_schema.TABLES
                            WHERE
                            	TABLE_SCHEMA = '%s'
                        """, db);

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, db)) {
                        ResultSet rs = statement.executeQuery(sql);
                        List<TableMetaData> metas = ResultSets.toJavaList(rs, TableMetaData.class);
                        metas.forEach(e -> e.setDatabase(db));
                        return metas;
                } catch (SQLException e) {
                        VFXDialog.openError(e);
                }

                return List.of();
        }

        @Override
        public List<ColumnMetaData> getColumns(TableMetaData table)
        {
                try {
                        List<ColumnMetaData> columns = selectByPage(table, 0, 0).getColumns();

                        Map<String, ColumnMetaData> columnMetaDataMap = new HashMap<>();

                        for (ColumnMetaData column : columns)
                                columnMetaDataMap.put(column.getName(), column);

                        String createTableDDL = showCreateTable(table.getDatabase(), table.getName());

                        SQLUtils.parseColumnDefSpec(createTableDDL, columnMetaDataMap);

                        return columns;
                } catch (Exception e) {
                        VFXDialog.openError(e);
                        return List.of();
                }
        }

        @Override
        public List<TableIndexMetaData> getIndexes(TableMetaData table)
        {
                String text = "SHOW INDEX FROM " + table.getName() + ";";
                SQL sql = new SQL(table.getDatabase(), text);

                MutableDataGrid dataGrid = execute(sql);

                Map<String, TableIndexMetaData> indexes = new LinkedHashMap<>();

                for (int i = 0; i < dataGrid.size(); i++) {

                        String keyName = dataGrid.getRowValue("Key_name", i);

                        if (streq(keyName, "PRIMARY"))
                                continue;

                        String columnName = dataGrid.getRowValue("Column_name", i);

                        TableIndexColumn indexColumn = new TableIndexColumn();
                        indexColumn.setName(columnName);
                        indexColumn.setOrder(atoi(dataGrid.getRowValue("Seq_in_index", i)));
                        indexColumn.setPrefixLength(atoi(dataGrid.getRowValue("Sub_part", i)));

                        if (indexes.containsKey(keyName)) {
                                TableIndexMetaData tableIndexMetaData = indexes.get(keyName);
                                tableIndexMetaData.getColumnMetaDatas().add(indexColumn);
                                continue;
                        }

                        TableIndexMetaData index = new TableIndexMetaData();
                        index.setName(keyName);
                        index.setType(dataGrid.getRowValue("Index_type", i));

                        if (productMetaData.getMajorVersion() >= MySQL.VERSION_8x)
                                index.setVisible(atobool(dataGrid.getRowValue("Visible", i)));

                        index.getColumnMetaDatas().add(indexColumn);

                        indexes.put(index.getName(), index);

                }

                List<TableIndexMetaData> ret = Lists.newArrayList(indexes.values());

                ret.forEach(TableIndexMetaData::generateColumnText);

                return ret;
        }

        @Override
        public String showCreateTable(String db, String table)
        {
                String sql = "SHOW CREATE TABLE " + table + ";";

                try (var connection = ds.getConnection();
                     var statement = connection.createStatement()) {
                        ResultSet rs = statement.executeQuery(sql);
                        if (rs.next())
                                return rs.getString(2);
                        return null;
                } catch (SQLException e) {
                        throw new SystemRuntimeException(e);
                }
        }

        @Override
        public void drop(String db, String table) throws SQLException
        {
                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, db)) {
                        statement.execute(strwfmt("DROP TABLE `%s`;", table));
                }
        }

        @Override
        @SuppressWarnings("ExtractMethodRecommender")
        public void alterChange(TableMetaData tableMetaData, Collection<ColumnMetaData> columnMetaDatas)
        {
                if (Lists.isEmpty(columnMetaDatas))
                        return;

                StringBuilder builder = new StringBuilder();

                for (ColumnMetaData col : columnMetaDatas) {
                        AlterExpression alterExpr = new AlterExpression();

                        if (col.getOriginName() != null) {
                                alterExpr.setOperation(AlterOperation.CHANGE);
                                alterExpr.setColumnOldName("`" + col.getOriginName() + "`");
                        } else {
                                alterExpr.setOperation(AlterOperation.ADD);
                        }

                        ColDataType colDataType = new ColDataType(col.getType());

                        var alterColDataType = new AlterExpression.ColumnDataType(false);

                        alterColDataType.setColumnName("`" + col.getName() + "`");
                        alterColDataType.setColDataType(colDataType);

                        alterColDataType.addColumnSpecs(
                                col.isNullable() ? "NULL" : "NOT NULL"
                        );

                        if (strnempty(col.getDefaultValue()))
                                alterColDataType.addColumnSpecs("DEFAULT", col.getDefaultValue());

                        if (col.getComment() != null)
                                alterColDataType.addColumnSpecs("COMMENT", "'" + col.getComment() + "'");

                        alterExpr.addColDataType(alterColDataType);

                        Alter alter = new Alter();
                        alter.setTable(new Table("`" + tableMetaData.getName() + "`"));
                        alter.setAlterExpressions(List.of(alterExpr));

                        builder.append(alter).append(";");
                }

                execute(new SQL(tableMetaData.getDatabase(), atos(builder)));
        }

        @Override
        @SuppressWarnings("resource")
        public MutableDataGrid execute(SQL sql, ExecuteCallback callback)
        {
                SQLParsedStatement current = null;

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, sql.getDb())) {

                        queue.put(sql.getTaskId(), statement);

                        for (SQLParsedStatement ps : sql) {

                                current = ps;
                                boolean skip = false;

                                LOG.info("Execute sql: \n{}", SqlFormatter.format(ps.getScript()));

                                /* DQL 并且必须是最后一个 SQL 语句才执行查询 */
                                if (ps.getType() == SQLCommandType.DQL && ps.isLast()) {

                                        MutableDataGrid grid = executeQueryGrid(
                                                connection,
                                                statement,
                                                sql,
                                                ps
                                        );

                                        callback.doCallback(ps.getScript(), SQLExecutorStatus.OK);

                                        return grid;

                                }

                                if (ps.getType() == SQLCommandType.DML) {
                                        statement.executeUpdate(ps.getScript());
                                        callback.doCallback(ps.getScript(), SQLExecutorStatus.OK);
                                        continue;
                                }

                                if (ps.getType() == SQLCommandType.DQL)
                                        skip = true;

                                statement.execute(ps.getScript());

                                callback.doCallback(ps.getScript(), skip ? SQLExecutorStatus.SKIP : SQLExecutorStatus.OK);
                        }

                        queue.remove(sql.getTaskId());

                } catch (SQLException e) {

                        LOG.error("MySQLExecutor execute error", e);

                        if (callback instanceof DefaultExecutorCallback) {

                                callback.doCallback(e.getMessage(), SQLExecutorStatus.ERROR);

                        } else if (current != null) {

                                callback.doCallback(current.getScript(), SQLExecutorStatus.ERROR);

                        }

                        throw new RuntimeException(e);

                }

                return null;
        }

        public MutableDataGrid selectByPage(TableMetaData tbMeta, int start, int size)
                throws SQLException
        {
                MutableDataGrid grid;

                String text = strwfmt("SELECT * FROM %s LIMIT %d OFFSET %d;", tbMeta.getName(), size, start);

                SQL sql = new SQL(0L, tbMeta.getDatabase(), text);

                try (Connection connection = ds.getConnection();
                     Statement statement = ds.use(connection, tbMeta.getDatabase())) {
                        grid = executeQueryGrid(connection, statement, sql, sql.iterator().next());
                }

                grid.setEditable(true);
                grid.setAddable(true);

                return grid;
        }

        @Override
        public void cancel(Long id)
        {
                VFXDialog.tryCall(() -> {
                        if (queue.containsKey(id))
                                queue.get(id).cancel();
                });
        }

        @SuppressWarnings("SimplifiableConditionalExpression")
        private MutableDataGrid executeQueryGrid(Connection connection,
                                                 Statement statement,
                                                 SQL sql,
                                                 SQLParsedStatement ps) throws SQLException
        {
                ResultSet rs = statement.executeQuery(ps.getScript());

                ResultSetMetaData rsMeta = rs.getMetaData();
                DatabaseMetaData dbMeta = connection.getMetaData();

                Map<String, ColumnMetaData> colMetas = new LinkedHashMap<>();

                for (int i = 1; i <= rsMeta.getColumnCount(); i++) {

                        ColumnMetaData c = new ColumnMetaData();

                        c.setIndex(i - 1);

                        c.setLabel(rsMeta.getColumnLabel(i));

                        c.setName(rsMeta.getColumnName(i));

                        c.setType(rsMeta.getColumnTypeName(i));

                        c.setNullable(
                                rsMeta.isNullable(i) == ResultSetMetaData.columnNullable
                        );

                        c.setOriginName(c.getName());

                        c.setTable(rsMeta.getTableName(i));

                        c.setSchema(rsMeta.getSchemaName(i));

                        colMetas.put(c.getName(), c);

                }

                boolean editable = false;

                if (ps.isSingleTable()) {

                        Set<String> pks = new HashSet<>();
                        String singleTable = ps.getSingleTable();

                        try (ResultSet pk = dbMeta.getPrimaryKeys(sql.getDb(), connection.getSchema(), singleTable)) {
                                while (pk.next())
                                        pks.add(pk.getString("COLUMN_NAME"));
                        }

                        pks.forEach(c -> {

                                ColumnMetaData meta = colMetas.get(c);
                                if (meta != null)
                                        meta.setPrimary(true);

                        });

                        Map<String, Map<String, Object>> columnInfo = new HashMap<>();

                        try (ResultSet col = dbMeta.getColumns(sql.getDb(), null, singleTable, null)) {

                                while (col.next()) {

                                        Map<String, Object> m = new HashMap<>();

                                        m.put("autoIncrement",
                                                "YES".equals(col.getString("IS_AUTOINCREMENT")));

                                        m.put("default",
                                                col.getString("COLUMN_DEF"));

                                        m.put("comment",
                                                col.getString("REMARKS"));

                                        columnInfo.put(col.getString("COLUMN_NAME"), m);

                                }

                        }

                        for (ColumnMetaData c : colMetas.values()) {
                                Map<String, Object> m = columnInfo.get(c.getName());

                                if (m == null)
                                        continue;

                                c.setAutoIncrement((Boolean) m.get("autoIncrement"));

                                c.setDefaultValue((String) m.get("default"));

                                c.setComment((String) m.get("comment"));
                        }

                        editable = colMetas
                                .values()
                                .stream()
                                .anyMatch(ColumnMetaData::isPrimary);

                }

                MutableDataGrid grid = new MutableDataGrid(sql, this);
                ResultSets.toMutableDataGird(List.copyOf(colMetas.values()), rs, grid);
                grid.setEditable(editable);

                return grid;
        }
}
