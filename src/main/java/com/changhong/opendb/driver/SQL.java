package com.changhong.opendb.driver;

import com.changhong.opendb.utils.Catcher;
import lombok.Getter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.*;

import java.util.*;

/**
 * SQL 脚本
 *
 * @author Luo Tiansheng
 * @since 2026/4/02
 */
public class SQL implements Iterable<SQLParsedStatement>
{
        @Getter
        private long taskId;
        @Getter
        private String db;

        private final List<SQLParsedStatement> sqlStatements = new ArrayList<>();

        public SQL(Long taskId, String db, String sqlText)
        {
                try {
                        this.taskId = taskId;
                        this.db = db;

                        Statements statements = CCJSqlParserUtil.parseStatements(sqlText);
                        int size = statements.size();

                        for (int i = 0; i < size; i++) {
                                sqlStatements.add(new SQLParsedStatement(
                                        statements.get(i),
                                        i == size - 1
                                ));
                        }
                } catch (JSQLParserException e) {
                        Catcher.ithrow(e);
                }
        }

        @Override
        public Iterator<SQLParsedStatement> iterator()
        {
                return sqlStatements.iterator();
        }
}
