package com.changhong.openvdb.driver.mysql;

import com.changhong.openvdb.driver.api.*;
import com.changhong.openvdb.driver.api.exception.DriverException;
import com.changhong.openvdb.driver.api.sql.SQL;
import com.changhong.openvdb.driver.api.sql.SQLCommandType;
import com.changhong.openvdb.driver.utils.SQLUtils;
import com.changhong.utils.collection.Lists;
import com.changhong.utils.collection.Sets;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static com.changhong.utils.TypeConverter.atobool;
import static com.changhong.utils.TypeConverter.atos;
import static com.changhong.utils.collection.Lists.beg;
import static com.changhong.utils.string.StaticLibrary.*;

/**
 * MySQL 驱动层实现
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class MySQLDriver extends Driver
{
        private static final Logger LOG = LoggerFactory.getLogger(MySQLDriver.class);

        public MySQLDriver(DataSource dataSource)
        {
                super(dataSource);
        }

        @Override
        protected Dialect createDialect()
        {
                return new MySQLDialect();
        }

        @Override
        public String showCreateTable(Session session, String table)
        {
                DataGrid dataGrid = execute(session, new SQL(
                        strfmt("SHOW CREATE TABLE %s;", dialect.quote(table))
                ));

                return beg(dataGrid.getRows()).get(1);
        }

        @Override
        public List<Table> getTables(Session session)
        {
                List<Table> tables = Lists.newArrayList();

                execute(session, (connection, statement) -> {
                        String sql = strfmt("""
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
                                                rs.getDate("createTime"),
                                                rs.getDate("updateTime"),
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
                        List<Column> columns = selectByPage(session, table, 0, 0).getColumns();

                        Map<String, Column> columnMap = new HashMap<>();

                        for (Column column : columns) {
                                column.setOriginalName(column.getName());
                                columnMap.put(column.getName(), column);
                        }

                        String createTableDDL = showCreateTable(session, table);

                        SQLUtils.parseColumnDefSpec(createTableDDL, columnMap);

                        /* 防篡改码生成 */
                        columns.forEach(Column::finalIntegrityCode);

                        return columns;
                } catch (Exception e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public List<Index> getIndexes(Session session, Table table)
        {
                SQL sql = new SQL("SHOW INDEX FROM " + dialect.quote(table.getName()) + ";");

                DataGrid dataGrid = execute(session, sql);

                List<String> indexColumns = new ArrayList<>();
                Map<String, Index> indexes = new LinkedHashMap<>();

                for (int i = 0; i < dataGrid.size(); i++) {
                        String keyName = dataGrid.getRowValue("Key_name", i);

                        /* 主键忽略 */
                        if (streq(keyName, "PRIMARY"))
                                continue;

                        indexColumns.add(dataGrid.getRowValue("Column_name", i));

                        Index index = new Index();

                        index.setName(keyName);
                        index.setOriginalName(keyName);

                        String Non_unique = dataGrid.getRowValue("Non_unique", i);
                        String Index_type = dataGrid.getRowValue("Index_type", i);

                        if (streq(Non_unique, "1") && streq(Index_type, "BTREE")) {
                                index.setType("NORMAL");
                        } else if (streq(Non_unique, "0") && streq(Index_type, "BTREE")) {
                                index.setType("UNIQUE");
                        } else if (streq(Index_type, "FULLTEXT")) {
                                index.setType("FULLTEXT");
                        } else if (streq(Index_type, "SPATIAL")) {
                                index.setType("SPATIAL");
                        } else if (streq(Index_type, "HASH")) {
                                index.setType("HASH");
                        } else {
                                index.setType(Index_type);
                        }

                        if (productMetaData.getMajorVersion() >= MySQL.VERSION_8x) {
                                index.setVisible(atobool(dataGrid.getRowValue("Visible", i)));
                                index.setOriginalVisible(index.isVisible());
                        }

                        indexes.put(index.getName(), index);
                }

                List<Index> ret = Lists.newArrayList(indexes.values());

                ret.forEach(idx -> {
                        /* 生成索引列 */
                        idx.generateColumnText(indexColumns);
                        /* 生成完整性校验码 */
                        idx.finalIntegrityCode();
                });

                return ret;
        }

        @Override
        public Set<String> getIndexTypes()
        {
                return Sets.newLinkedHashSet(
                        "NORMAL",
                        "UNIQUE",
                        "FULLTEXT",
                        "SPATIAL",
                        "HASH"
                );
        }

        @Override
        public void dropTable(Session session, String table)
        {
                execute(session, (connection, statement) -> statement.execute(
                        strfmt("DROP TABLE `%s`;", dialect.quote(table))));
        }

        @Override
        public void dropColumns(Session session, String table, Collection<Column> columns)
        {
                StringBuilder script = new StringBuilder();

                script.append(strfmt("ALTER TABLE `%s` ", table));

                for (Column col : columns)
                        script.append(strfmt("DROP COLUMN `%s`, ", col.getName()));

                script.delete(script.length() - 2, script.length());
                script.append(";");

                execute(session, new SQL(atos(script)));
        }

        @Override
        public void dropIndexKeys(Session session, String table, Collection<Index> selectionItems)
        {
                StringBuilder scripts = new StringBuilder();

                for (Index index : selectionItems) {

                        String name = streq(index.getName(), index.getName())
                                ? index.getName()
                                : index.getOriginalName();

                        scripts.append(
                                strfmt("ALTER TABLE `%s` DROP INDEX `%s`;\n", table, name)
                        );
                }

                execute(session, new SQL(atos(scripts)));
        }

        @Override
        public void dropPrimaryKey(Session session, String table)
        {
                try {
                        String sql = strfmt("ALTER TABLE %s DROP PRIMARY KEY;", dialect.quote(table));
                        execute(session, new SQL(sql));
                } catch (Exception e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void alterPrimaryKey(Session session, String table, Collection<Column> primaryKeys)
        {
                Column autoColumn = null;

                if (!primaryKeys.isEmpty()) {
                        for (Column primaryKey : primaryKeys) {
                                if (primaryKey.isAutoIncrement()) {
                                        autoColumn = primaryKey;
                                        break;
                                }
                        }

                        /* 先删除自增列 */
                        if (autoColumn != null) {
                                autoColumn.setAutoIncrement(false);
                                alterChange(session, table, List.of(autoColumn));
                        }
                }

                /* 尝试删除主键 */
                try {
                        dropPrimaryKey(session, table);
                } catch (DriverException e) {
                        if (e.getErrorCode() != 1091)
                                throw e;
                }

                if (primaryKeys.isEmpty())
                        return;

                /* 重建主键 */
                StringBuilder script = new StringBuilder();

                script.append("ALTER TABLE ")
                        .append(dialect.quote(table))
                        .append(" ADD PRIMARY KEY (");

                for (Column primaryKey : primaryKeys) {
                        script.append(dialect.quote(primaryKey.getName())).append(",");
                }

                script.delete(script.length() - 1, script.length());
                script.append(");");

                execute(session, new SQL(atos(script)));

                /* 恢复自增 */
                if (autoColumn != null) {
                        autoColumn.setAutoIncrement(true);
                        alterChange(session, table, List.of(autoColumn));
                }
        }

        @Override
        public void alterIndexKeys(Session session, String table, Collection<Index> indexes)
        {
                StringBuilder scripts = new StringBuilder();

                for (Index index : indexes) {

                        String type = index.getType();
                        String name = index.getName();
                        String columns = index.getColumnsText();

                        if (streq(type, "NORMAL")) {
                                scripts.append(
                                        strfmt(
                                                "CREATE INDEX `%s` ON `%s`(%s);\n",
                                                name,
                                                table,
                                                columns
                                        )
                                );
                                continue;
                        }

                        if (streq(type, "UNIQUE")) {
                                scripts.append(
                                        strfmt(
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
                                        strfmt(
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
                                        strfmt(
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
                                        strfmt(
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
        public void alterChange(Session session, String table, Collection<Column> columns)
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

                        alterColDataType.setColumnName(dialect.quote(col.getName()));
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
                        alter.setTable(new net.sf.jsqlparser.schema.Table(dialect.quote(table)));
                        alter.setAlterExpressions(List.of(alterExpr));

                        builder.append(alter).append(";");
                }

                execute(session, new SQL(atos(builder)));
        }

        @Override
        public void alterVisible(Session session, String table, Collection<Index> indexes)
        {
                StringBuilder scripts = new StringBuilder();

                for (Index index : indexes) {
                        String isVisible = index.isVisible() ? "VISIBLE" : "INVISIBLE";
                        scripts.append(
                                strfmt("ALTER TABLE %s ALTER INDEX %s %s;",
                                        dialect.quote(table),
                                        dialect.quote(index.getName()),
                                        isVisible)
                        );
                }

                execute(session, new SQL(atos(scripts)));
        }
}
