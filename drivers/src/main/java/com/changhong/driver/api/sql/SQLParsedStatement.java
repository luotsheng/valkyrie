package com.changhong.driver.api.sql;

import lombok.Getter;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.refresh.RefreshMaterializedViewStatement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;

/**
 * @author Luo Tiansheng
 * @since 2026/4/02
 */
@Getter
public class SQLParsedStatement
{
        private final Statement statement;

        private final SQLCommandType command;

        public SQLParsedStatement(Statement statement)
        {
                this.statement = statement;
                this.command = toType(statement);
        }

        private static SQLCommandType toType(Statement statement)
        {
                return switch (statement) {
                        /* DDL */
                        case Alter ignored -> SQLCommandType.EXECUTE;
                        case CreateTable ignored -> SQLCommandType.EXECUTE;
                        case CreateView ignored -> SQLCommandType.EXECUTE;
                        case CreateIndex ignored -> SQLCommandType.EXECUTE;
                        case Drop ignored -> SQLCommandType.EXECUTE;
                        case Truncate ignored -> SQLCommandType.EXECUTE;
                        case RefreshMaterializedViewStatement ignored -> SQLCommandType.EXECUTE;

                        /* DML */
                        case Insert ignored -> SQLCommandType.EXECUTE_UPDATE;
                        case Update ignored -> SQLCommandType.EXECUTE_UPDATE;
                        case Delete ignored -> SQLCommandType.EXECUTE_UPDATE;
                        case Merge ignored -> SQLCommandType.EXECUTE_UPDATE;
                        case Upsert ignored -> SQLCommandType.EXECUTE_UPDATE;

                        /* DQL */
                        case Select ignored -> SQLCommandType.EXECUTE_QUERY;
                        case ShowStatement ignored -> SQLCommandType.EXECUTE_QUERY;

                        /* DCL */
                        case Grant ignored -> SQLCommandType.EXECUTE;

                        /* TCL */
                        case Commit ignored -> SQLCommandType.EXECUTE_UPDATE;
                        case RollbackStatement ignored -> SQLCommandType.EXECUTE_UPDATE;

                        /* 其他直接执行 */
                        case SetStatement ignored -> SQLCommandType.EXECUTE;
                        case Execute ignored -> SQLCommandType.EXECUTE;

                        /* 默认查询 */
                        default -> SQLCommandType.EXECUTE_QUERY;
                };
        }

        @Override
        public String toString()
        {
                return statement.toString();
        }
}
