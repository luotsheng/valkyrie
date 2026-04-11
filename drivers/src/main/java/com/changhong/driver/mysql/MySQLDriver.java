package com.changhong.driver.mysql;

import com.changhong.collection.Lists;
import com.changhong.driver.api.*;
import com.changhong.driver.api.Driver;
import com.changhong.driver.api.sql.SQLCommandType;
import com.changhong.driver.api.sql.SQLParsedStatement;
import com.changhong.driver.api.exception.DriverException;
import com.changhong.driver.api.sql.SQL;
import com.changhong.driver.api.sql.SQLExecutor;
import com.changhong.driver.utils.ResultSets;
import com.changhong.driver.utils.SQLUtils;
import com.changhong.utils.Captor;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.create.table.ColDataType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.changhong.collection.Lists.beg;
import static com.changhong.string.StringStaticize.*;
import static com.changhong.utils.TypeConverter.atos;

/**
 * MySQL 驱动层实现
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class MySQLDriver extends Driver implements SQLExecutor
{
        private final MySQLDialect dialect = new MySQLDialect();

        private final Map<Long, Statement> taskQueue = new ConcurrentHashMap<>();

        public MySQLDriver(DataSource dataSource)
        {
                super(dataSource);
        }

        @Override
        public String showCreateTable(Session session, String table)
        {
                DataGrid dataGrid = execute(session, new SQL(
                        strwfmt("SHOW CREATE TABLE %s;", dialect.quote(table))
                ));

                return beg(dataGrid.getRows()).get(1);
        }

        @Override
        public List<Table> getTables(Session session)
        {
                List<Table> tables = Lists.newArrayList();

                execute(session, (connection, statement) -> {
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
                            	TABLE_SCHEMA = '%s';
                        """, session.catalog());

                        try (var rs = statement.executeQuery(sql)) {
                                while (rs.next()) {
                                        tables.add(new Table(
                                                rs.getString("name"),
                                                rs.getString("createTime"),
                                                rs.getString("updateTime"),
                                                rs.getString("engine"),
                                                rs.getFloat("size"),
                                                rs.getInt("rows"),
                                                rs.getString("comment")
                                        ));
                                }
                        } catch (SQLException e) {
                                throw new DriverException(e);
                        }
                });

                return tables;
        }

        @Override
        public List<Column> getColumns(Session session, String table)
        {
                try {
                        String sql = strwfmt("SELECT * FROM %s", dialect.quote(table));
                        List<Column> columns = selectByPage(session, sql, 0, 0).getColumns();

                        Map<String, Column> columnMetaDataMap = new HashMap<>();

                        for (Column column : columns)
                                columnMetaDataMap.put(column.getName(), column);

                        String createTableDDL = showCreateTable(session, table);

                        SQLUtils.parseColumnDefSpec(createTableDDL, columnMetaDataMap);

                        /* 防篡改码生成 */
                        columns.forEach(Column::finalIntegrityCode);

                        return columns;
                } catch (Exception e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void dropTable(Session session, String table)
        {
                execute(session, (connection, statement) -> statement.execute(
                        strwfmt("DROP TABLE `%s`;", dialect.quote(table))));
        }

        @Override
        public void dropColumns(Session session, Table table, Collection<Column> columns)
        {
                StringBuilder script = new StringBuilder();

                script.append(strwfmt("ALTER TABLE `%s` ", table.name()));

                for (Column col : columns)
                        script.append(strwfmt("DROP COLUMN `%s`, ", col.getName()));

                script.delete(script.length() - 2, script.length());
                script.append(";");

                execute(session, new SQL(atos(script)));
        }

        @Override
        public void dropIndexKeys(Session session, Table table, Collection<Index> selectionItems)
        {
                StringBuilder scripts = new StringBuilder();

                for (Index index : selectionItems) {

                        String name = streq(index.getName(), index.getName())
                                ? index.getName()
                                : index.getOriginalName();

                        scripts.append(
                                strwfmt("ALTER TABLE `%s` DROP INDEX `%s`;\n", table.name(), name)
                        );
                }

                execute(session, new SQL(atos(scripts)));
        }

        @Override
        public void alterPrimaryKey(Session session, Table table, Collection<Column> primaryKeys)
        {
                if (primaryKeys.isEmpty())
                        return;

                String sql = strwfmt("ALTER TABLE %s DROP PRIMARY KEY;", dialect.quote(table.name()));
                execute(session, new SQL(sql));

                StringBuilder script = new StringBuilder();

                script.append("ALTER TABLE `")
                        .append(table.name())
                        .append("` ADD PRIMARY KEY (");

                for (Column primaryKey : primaryKeys) {
                        script.append("`")
                                .append(primaryKey.getName())
                                .append("`")
                                .append(",");
                }

                script.delete(script.length() - 1, script.length());
                script.append(");");

                execute(session, new SQL(atos(script)));
        }

        @Override
        public void alterIndexKeys(Session session, Table table, Collection<Index> indexes)
        {
                StringBuilder scripts = new StringBuilder();

                for (Index index : indexes) {

                        String type = index.getType();
                        String name = index.getName();
                        String columns = index.getColumnsText();

                        if (streq(type, "NORMAL")) {
                                scripts.append(
                                        strwfmt(
                                                "CREATE INDEX `%s` ON `%s`(%s);\n",
                                                name,
                                                table.name(),
                                                columns
                                        )
                                );
                                continue;
                        }

                        if (streq(type, "UNIQUE")) {
                                scripts.append(
                                        strwfmt(
                                                "CREATE UNIQUE INDEX `%s` ON `%s`(%s);\n",
                                                name,
                                                table,
                                                columns
                                        )
                                );
                                continue;
                        }

                        if (streq(type, "FULLTEXT")) {
                                scripts.append(
                                        strwfmt(
                                                "CREATE FULLTEXT INDEX `%s` ON `%s`(%s);\n",
                                                name,
                                                table,
                                                columns
                                        )
                                );
                                continue;
                        }

                        if (streq(type, "SPATIAL")) {
                                scripts.append(
                                        strwfmt(
                                                "CREATE SPATIAL INDEX `%s` ON `%s`(%s);\n",
                                                name,
                                                table,
                                                columns
                                        )
                                );
                                continue;
                        }

                        if (streq(type, "HASH")) {
                                scripts.append(
                                        strwfmt(
                                                "CREATE INDEX `%s` ON `%s`(%s) USING HASH;\n",
                                                name,
                                                table,
                                                columns
                                        )
                                );
                        }
                }

                execute(session, new SQL(SQLCommandType.EXECUTE, atos(scripts)));
        }

        @Override
        public void alterChange(Session session, Table table, Collection<Column> columns)
        {
                if (Lists.isEmpty(columns))
                        return;

                StringBuilder builder = new StringBuilder();

                for (Column col : columns) {
                        AlterExpression alterExpr = new AlterExpression();

                        if (col.getOriginalName() != null) {
                                alterExpr.setOperation(AlterOperation.CHANGE);
                                alterExpr.setColumnOldName("`" + col.getOriginalName() + "`");
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

                        if (col.isAutoIncrement())
                                alterColDataType.addColumnSpecs("AUTO_INCREMENT");

                        if (strnempty(col.getDefaultValue()))
                                alterColDataType.addColumnSpecs("DEFAULT", col.getDefaultValue());

                        if (col.getComment() != null)
                                alterColDataType.addColumnSpecs("COMMENT", "'" + col.getComment() + "'");

                        alterExpr.addColDataType(alterColDataType);

                        Alter alter = new Alter();
                        alter.setTable(new net.sf.jsqlparser.schema.Table(dialect.quote(table.name())));
                        alter.setAlterExpressions(List.of(alterExpr));

                        builder.append(alter).append(";");
                }

                execute(session, new SQL(atos(builder)));
        }

        @Override
        public DataGrid selectByPage(Session session, String sql, int off, int size)
        {
                return execute(session, new SQL(dialect.limit(sql, off, size)));
        }

        @Override
        public DataGrid execute(long jobId, Session session, SQL sql)
        {
                return executeQuery(session, (connection, statement) -> {

                        taskQueue.put(jobId, statement);

                        SQLParsedStatement endStatement = sql.popupEnd();

                        for (SQLParsedStatement ps : sql) {
                                switch (ps.getCommand()) {
                                        case EXECUTE -> statement.execute(ps.toString());
                                        case EXECUTE_UPDATE -> statement.executeUpdate(ps.toString());
                                        case EXECUTE_QUERY -> {}
                                }
                        }

                        if (endStatement.getCommand() == SQLCommandType.EXECUTE_QUERY) {
                                ResultSet rs = statement.executeQuery(endStatement.toString());
                                return ResultSets.toDataGrid(connection, endStatement, rs);
                        }

                        return null;
                });
        }

        @Override
        public void cancel(long jobId)
        {
                if (taskQueue.containsKey(jobId))
                        Captor.call(() -> taskQueue.remove(jobId).cancel());
        }
}
