package valkyrie.driver.mysql;

import valkyrie.driver.keywords.SqlStandardKeywords;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * MySQL 关键字
 *
 * @author Luo Tiansheng
 * @since 2026/5/7
 */
public class MySQLKeywords
{
        public static final Set<String> KEYWORDS;

        static {
                Set<String> keywords = new LinkedHashSet<>();

                Collections.addAll(keywords,
                        "ACCESSIBLE",
                        "AGAINST",
                        "ALWAYS",
                        "ANALYZE",
                        "ASCII",
                        "ASENSITIVE",
                        "AT",
                        "AUTO_INCREMENT",
                        "BINARY",
                        "BLOB",
                        "BOTH",
                        "CHANGE",
                        "CHARACTER",
                        "COLLATE",
                        "CONDITION",
                        "CONTINUE",
                        "CONVERT",
                        "CURSOR",
                        "DATABASES",
                        "DAY_HOUR",
                        "DAY_MICROSECOND",
                        "DAY_MINUTE",
                        "DAY_SECOND",
                        "DELAYED",
                        "DESCRIBE",
                        "DETERMINISTIC",
                        "DISTINCTROW",
                        "DIV",
                        "DUAL",
                        "EACH",
                        "ELSEIF",
                        "ENCLOSED",
                        "ESCAPED",
                        "EXIT",
                        "EXPLAIN",
                        "FETCH",
                        "FORCE",
                        "FULLTEXT",
                        "GENERATED",
                        "HIGH_PRIORITY",
                        "IGNORE",
                        "INFILE",
                        "INOUT",
                        "INT",
                        "INTEGER",
                        "INTERVAL",
                        "ITERATE",
                        "KEY",
                        "KEYS",
                        "KILL",
                        "LEADING",
                        "LEAVE",
                        "LINEAR",
                        "LINES",
                        "LOAD",
                        "LOCK",
                        "LONG",
                        "LOOP",
                        "LOW_PRIORITY",
                        "MATCH",
                        "MAXVALUE",
                        "MEDIUMINT",
                        "MINUTE_MICROSECOND",
                        "MOD",
                        "MODIFIES",
                        "NATURAL",
                        "NO_WRITE_TO_BINLOG",
                        "NUMERIC",
                        "OPTIMIZE",
                        "OPTIONALLY",
                        "OUTFILE",
                        "PARTITION",
                        "PRECISION",
                        "PURGE",
                        "RANGE",
                        "READS",
                        "REAL",
                        "REGEXP",
                        "RELEASE",
                        "RENAME",
                        "REPEAT",
                        "REPLACE",
                        "REQUIRE",
                        "RETURN",
                        "REVOKE",
                        "RLIKE",
                        "ROW",
                        "ROWS",
                        "SCHEMAS",
                        "SECOND_MICROSECOND",
                        "SENSITIVE",
                        "SEPARATOR",
                        "SHOW",
                        "SMALLINT",
                        "SPATIAL",
                        "SPECIFIC",
                        "SQLEXCEPTION",
                        "SQLSTATE",
                        "SQLWARNING",
                        "SQL_BIG_RESULT",
                        "SQL_CALC_FOUND_ROWS",
                        "SQL_SMALL_RESULT",
                        "SSL",
                        "STARTING",
                        "STRAIGHT_JOIN",
                        "TERMINATED",
                        "TINYINT",
                        "TRAILING",
                        "TRIGGER",
                        "UNDO",
                        "UNLOCK",
                        "UNSIGNED",
                        "USAGE",
                        "USING",
                        "VARCHAR",
                        "VARYING",
                        "WHILE",
                        "WRITE",
                        "XOR",
                        "ZEROFILL"
                );

                keywords.addAll(SqlStandardKeywords.KEYWORDS);
                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private MySQLKeywords()
        {
                /* DO NOTHING... */
        }
}
