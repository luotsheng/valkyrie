package com.changhong.driver.api.sql;

import com.changhong.collection.Lists;
import com.changhong.utils.Captor;
import lombok.Getter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;

import java.util.Iterator;
import java.util.List;

/**
 * SQL 执行单元
 *
 * 表示一次用户提交的 SQL 内容，支持包含多条语句。
 * 每条语句可能属于不同类型（SELECT / DDL / DML / DCL / TCL / 扩展语句）。
 *
 * 执行特性：
 * - SQL 内容可能包含多条语句（按分隔符拆分后执行）
 * - 执行顺序严格按照语句顺序
 * - 每条语句独立产生执行结果
 *
 * 使用场景：
 * - 编辑器执行选中 SQL
 * - 脚本批量执行
 * - 控制台命令执行
 *
 * 该接口由各自驱动独立实现，其中 SQL 方言解析等内容，由子类自由实现。
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public class SQL implements Iterable<SQLParsedStatement>
{
        @Getter
        private final String raw;

        private final List<SQLParsedStatement> statements = Lists.newArrayList();

        public SQL(String raw)
        {
                this(null, raw);

        }
        public SQL(SQLCommandType type, String raw)
        {
                this.raw = raw;

                Statements statements =
                        Captor.call(() -> CCJSqlParserUtil.parseStatements(raw));

                for (Statement statement : statements) {
                        SQLParsedStatement sqlParsedStatement = new SQLParsedStatement(statement);
                        if (type != null)
                                sqlParsedStatement.setCommand(type);
                        this.statements.add(sqlParsedStatement);
                }
        }

        public SQLParsedStatement popupEnd()
        {
                return statements.remove(statements.size() - 1);
        }

        @Override
        public Iterator<SQLParsedStatement> iterator()
        {
                return statements.iterator();
        }
}
