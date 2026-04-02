package com.changhong.opendb.driver;

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
public class SQL implements Iterable<SQLStatement>
{
        @Getter
        private final long taskId;
        @Getter
        private final String db;
        private final List<SQLStatement> sqlStatements = new ArrayList<>();

        public SQL(Long taskId, String db, String sqlText)
                throws JSQLParserException
        {
                this.taskId = taskId;
                this.db = db;

                Statements statements = CCJSqlParserUtil.parseStatements(sqlText);
                int size = statements.size();

                for (int i = 0; i < size; i++) {
                        sqlStatements.add(new SQLStatement(
                                statements.get(i),
                                i == size - 1
                        ));
                }
        }

        @Override
        public Iterator<SQLStatement> iterator()
        {
                return sqlStatements.iterator();
        }
}
