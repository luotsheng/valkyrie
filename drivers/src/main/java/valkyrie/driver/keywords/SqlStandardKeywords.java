package valkyrie.driver.keywords;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SQL 标准关键字
 *
 * @author Luo Tiansheng
 * @since 2026/5/7
 */
public class SqlStandardKeywords
{
        public static final Set<String> KEYWORDS;

        static {
                Set<String> keywords = new LinkedHashSet<>();

                Collections.addAll(keywords,
                        "ADD",
                        "ALL",
                        "ALTER",
                        "AND",
                        "ANY",
                        "AS",
                        "ASC",
                        "AUTHORIZATION",
                        "BACKUP",
                        "BEGIN",
                        "BETWEEN",
                        "BY",
                        "CASE",
                        "CHECK",
                        "COLUMN",
                        "COMMIT",
                        "CONSTRAINT",
                        "CREATE",
                        "DATABASE",
                        "DEFAULT",
                        "DELETE",
                        "DESC",
                        "DISTINCT",
                        "DROP",
                        "ELSE",
                        "END",
                        "EXEC",
                        "EXISTS",
                        "FOREIGN",
                        "FROM",
                        "FULL",
                        "GROUP",
                        "HAVING",
                        "IN",
                        "INDEX",
                        "INNER",
                        "INSERT",
                        "INTO",
                        "IS",
                        "JOIN",
                        "LEFT",
                        "LIKE",
                        "LIMIT",
                        "NOT",
                        "NULL",
                        "ON",
                        "OR",
                        "ORDER",
                        "OUTER",
                        "PRIMARY",
                        "PROCEDURE",
                        "RIGHT",
                        "ROLLBACK",
                        "ROWNUM",
                        "SELECT",
                        "SET",
                        "TABLE",
                        "TOP",
                        "TRUNCATE",
                        "UNION",
                        "UNIQUE",
                        "UPDATE",
                        "VALUES",
                        "VIEW",
                        "WHERE"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private SqlStandardKeywords()
        {
                /* DO NOTHING... */
        }
}
