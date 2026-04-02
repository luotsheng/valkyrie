package com.changhong.opendb.driver;

import com.changhong.opendb.utils.Catcher;
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
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Luo Tiansheng
 * @since 2026/4/02
 */
public class SQLParsedStatement
{
        @Getter
        private final String script;
        @Getter
        private final SQLCommandType type;
        @Getter
        private final boolean last;

        private final Set<String> tables = new HashSet<>();

        public SQLParsedStatement(Statement statement, boolean last)
        {
                this.script = statement.toString();
                this.type = toType(statement);
                this.last = last;

                if (type == SQLCommandType.UNSUPPORTED)
                        Catcher.ithrow("不支持的查询语言！");

                if (type == SQLCommandType.DQL) {
                        TablesNamesFinder<Void> finder = new TablesNamesFinder<>();
                        this.tables.addAll(finder.getTables(statement));
                }
        }

        public boolean isOnlyOneTable()
        {
                return tables.size() == 1;
        }

        public String getOnlyTable()
        {
                return tables.iterator().next();
        }

        private static SQLCommandType toType(Statement statement)
        {
                return switch (statement) {
                        /* DDL */
                        case Alter ignored -> SQLCommandType.DDL;
                        case CreateTable ignored -> SQLCommandType.DDL;
                        case CreateView ignored -> SQLCommandType.DDL;
                        case CreateIndex ignored -> SQLCommandType.DDL;
                        case Drop ignored -> SQLCommandType.DDL;
                        case Truncate ignored -> SQLCommandType.DDL;
                        case RefreshMaterializedViewStatement ignored -> SQLCommandType.DDL;

                        /* DML */
                        case Insert ignored -> SQLCommandType.DML;
                        case Update ignored -> SQLCommandType.DML;
                        case Delete ignored -> SQLCommandType.DML;
                        case Merge ignored -> SQLCommandType.DML;
                        case Upsert ignored -> SQLCommandType.DML;

                        /* DQL */
                        case Select ignored -> SQLCommandType.DQL;
                        case ShowStatement ignored -> SQLCommandType.DQL;

                        /* DCL */
                        case Grant ignored -> SQLCommandType.DCL;

                        /* TCL */
                        case Commit ignored -> SQLCommandType.TCL;
                        case RollbackStatement ignored -> SQLCommandType.TCL;

                        /* 其他直接执行 */
                        case SetStatement ignored -> SQLCommandType.EXECUTE;
                        case Execute ignored -> SQLCommandType.EXECUTE;

                        /* 不支持 */
                        default -> SQLCommandType.UNSUPPORTED;
                };
        }
}
