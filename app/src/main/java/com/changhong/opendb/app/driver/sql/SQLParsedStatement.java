package com.changhong.opendb.app.driver.sql;

import com.changhong.opendb.app.core.exception.CatcherException;
import com.changhong.opendb.app.utils.Catcher;
import lombok.Getter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
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
import net.sf.jsqlparser.statement.show.ShowIndexStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashSet;
import java.util.Set;

import static com.changhong.collection.Lists.beg;

/**
 * @author Luo Tiansheng
 * @since 2026/4/02
 */
public class SQLParsedStatement
{
        @Getter
        private String script;
        @Getter
        private SQLCommandType type;
        @Getter
        private boolean last;
        @Getter
        private boolean star;

        private final Set<String> tables = new HashSet<>();

        public SQLParsedStatement(String text)
        {
                try {
                        Statements statements = CCJSqlParserUtil.parseStatements(text);

                        if (statements.size() > 1)
                                throw new CatcherException("jsqlparser: parse statements size > 1");

                        initialize(beg(statements), true);
                } catch (Exception e) {
                        Catcher.ithrow(e);
                }
        }

        public SQLParsedStatement(Statement statement, boolean last)
        {
                initialize(statement, last);
        }

        private void initialize(Statement statement, boolean last)
        {
                this.script = statement.toString();
                this.type = toType(statement);
                this.last = last;
                this.star = false;

                if (type == SQLCommandType.UNSUPPORTED)
                        Catcher.ithrow("Unsupported " + script);

                if (type == SQLCommandType.DQL && !(statement instanceof ShowIndexStatement)) {
                        TablesNamesFinder<Void> finder = new TablesNamesFinder<>();
                        this.tables.addAll(finder.getTables(statement));
                }
        }

        public boolean isSingleTable()
        {
                return tables.size() == 1;
        }

        public String getSingleTable()
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

                        /* 不支持也得硬要支持 */
                        default -> SQLCommandType.DQL;
                };
        }
}
