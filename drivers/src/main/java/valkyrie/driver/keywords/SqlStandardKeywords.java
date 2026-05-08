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
                        // A - B
                        "ABS", "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS",
                        "ASC", "ASENSITIVE", "ASSERTION", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION",
                        "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BIT", "BIT_LENGTH", "BLOB",
                        "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADE", "CASCADED",
                        "CASE", "CAST", "CATALOG", "CHAR", "CHAR_LENGTH", "CHARACTER", "CHARACTER_LENGTH",
                        "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLECT", "COLUMN", "COMMIT",
                        "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING",
                        "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT",
                        "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH",
                        "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
                        "CURSOR", "CYCLE", "DATA", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE",
                        "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE", "DESC", "DETERMINISTIC",
                        "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE",
                        "END", "END-EXEC", "ESCAPE", "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS",
                        "EXIT", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FIRST", "FLOAT",
                        "FLOOR", "FOR", "FOREIGN", "FORTRAN", "FOUND", "FREE", "FROM", "FULL", "FUNCTION",
                        "FUSION", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "GROUPING", "HAVING",
                        "HOLD", "HOUR", "IDENTITY", "IF", "IN", "INDICATOR", "INNER", "INOUT", "INSENSITIVE",
                        "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS",
                        "ISOLATION", "JOIN", "KEY", "LANGUAGE", "LARGE", "LAST", "LATERAL", "LEADING",
                        "LEFT", "LEVEL", "LIKE", "LIMIT", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP",
                        "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD",
                        "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB",
                        "NEW", "NEXT", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC",
                        "OCTET_LENGTH", "OF", "OFFSET", "OLD", "ON", "ONLY", "OPEN", "OPTION", "OR",
                        "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", "OVERLAY", "PARAMETER", "PARTITION",
                        "PERCENT", "PERCENT_RANK", "PERIOD", "PORTION", "POSITION", "POWER", "PRECEDING",
                        "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL",
                        "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT",
                        "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY",
                        "RELATIVE", "RELEASE", "REPEAT", "RESIGNAL", "RESTRICT", "RESULT", "RETURN",
                        "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROW_NUMBER", "ROWS",
                        "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SELECT", "SENSITIVE",
                        "SESSION_USER", "SET", "SIGNAL", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC",
                        "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "START", "STATIC",
                        "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC",
                        "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP",
                        "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE",
                        "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE", "UESCAPE", "UNBOUNDED", "UNION",
                        "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPPER", "USAGE", "USER", "USING",
                        "VALUE", "VALUES", "VAR_POP", "VAR_SAMP", "VARCHAR", "VARYING", "WHEN", "WHENEVER",
                        "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR", "SUBSTR"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private SqlStandardKeywords()
        {
                /* DO NOTHING... */
        }
}
