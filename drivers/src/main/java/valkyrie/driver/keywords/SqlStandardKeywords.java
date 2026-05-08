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
                        "ABS:Function", "ADD:Keyword", "ALL:Keyword", "ALLOCATE:Keyword", "ALTER:Keyword", "AND:Operator", "ANY:Keyword", "ARE:Keyword", "ARRAY:Keyword", "AS:Keyword",
                        "ASC:Keyword", "ASENSITIVE:Keyword", "ASSERTION:Keyword", "ASYMMETRIC:Keyword", "AT:Keyword", "ATOMIC:Keyword", "AUTHORIZATION:Keyword",
                        "AVG:Function", "BEGIN:Keyword", "BETWEEN:Keyword", "BIGINT:Keyword", "BINARY:Keyword", "BIT:Keyword", "BIT_LENGTH:Function", "BLOB:Keyword",
                        "BOOLEAN:Keyword", "BOTH:Keyword", "BY:Keyword", "CALL:Keyword", "CALLED:Keyword", "CARDINALITY:Function", "CASCADE:Keyword", "CASCADED:Keyword",
                        "CASE:Keyword", "CAST:Keyword", "CATALOG:Keyword", "CHAR:Keyword", "CHAR_LENGTH:Function", "CHARACTER:Keyword", "CHARACTER_LENGTH:Function",
                        "CHECK:Keyword", "CLOB:Keyword", "CLOSE:Keyword", "COALESCE:Function", "COLLATE:Keyword", "COLLECT:Keyword", "COLUMN:Keyword", "COMMIT:Keyword",
                        "CONDITION:Keyword", "CONNECT:Keyword", "CONNECTION:Keyword", "CONSTRAINT:Keyword", "CONVERT:Function", "CORR:Function", "CORRESPONDING:Keyword",
                        "COUNT:Function", "COVAR_POP:Function", "COVAR_SAMP:Function", "CREATE:Keyword", "CROSS:Keyword", "CUBE:Keyword", "CUME_DIST:Function", "CURRENT:Keyword",
                        "CURRENT_CATALOG:Keyword", "CURRENT_DATE:Keyword", "CURRENT_DEFAULT_TRANSFORM_GROUP:Keyword", "CURRENT_PATH:Keyword",
                        "CURRENT_ROLE:Keyword", "CURRENT_SCHEMA:Keyword", "CURRENT_TIME:Keyword", "CURRENT_TIMESTAMP:Keyword", "CURRENT_USER:Keyword",
                        "CURSOR:Keyword", "CYCLE:Keyword", "DATA:Keyword", "DATE:Keyword", "DAY:Keyword", "DEALLOCATE:Keyword", "DEC:Keyword", "DECIMAL:Keyword", "DECLARE:Keyword",
                        "DEFAULT:Keyword", "DELETE:Keyword", "DENSE_RANK:Function", "DEREF:Keyword", "DESCRIBE:Keyword", "DESC:Keyword", "DETERMINISTIC:Keyword",
                        "DISCONNECT:Keyword", "DISTINCT:Keyword", "DOUBLE:Keyword", "DROP:Keyword", "DYNAMIC:Keyword", "EXPLAIN:Keyword", "EACH:Keyword", "ELEMENT:Keyword", "ELSE:Keyword",
                        "END:Keyword", "END-EXEC:Keyword", "ESCAPE:Keyword", "EVERY:Keyword", "EXCEPT:Keyword", "EXEC:Keyword", "EXECUTE:Keyword", "EXISTS:Keyword",
                        "EXIT:Keyword", "EXTERNAL:Keyword", "EXTRACT:Function", "FALSE:Keyword", "FETCH:Keyword", "FILTER:Keyword", "FIRST:Keyword", "FLOAT:Keyword",
                        "FLOOR:Function", "FOR:Keyword", "FOREIGN:Keyword", "FORTRAN:Keyword", "FOUND:Keyword", "FREE:Keyword", "FROM:Keyword", "FULL:Keyword", "FUNCTION:Keyword",
                        "FUSION:Keyword", "GET:Keyword", "GLOBAL:Keyword", "GO:Keyword", "GOTO:Keyword", "GRANT:Keyword", "GROUP:Keyword", "GROUP BY:Operator", "GROUPING:Keyword", "HAVING:Keyword",
                        "HOLD:Keyword", "HOUR:Keyword", "IDENTITY:Keyword", "IF:Keyword", "IN:Keyword", "INDICATOR:Keyword", "INNER:Keyword", "INOUT:Keyword", "INSENSITIVE:Keyword",
                        "INSERT:Keyword", "INT:Keyword", "INTEGER:Keyword", "INTERSECT:Keyword", "INTERSECTION:Keyword", "INTERVAL:Keyword", "INTO:Keyword", "IS:Keyword",
                        "ISOLATION:Keyword", "JOIN:Operator", "INNER JOIN:Operator", "RIGHT JOIN:Operator", "LEFT JOIN:Operator", "KEY:Keyword", "LANGUAGE:Keyword", "LARGE:Keyword", "LAST:Keyword", "LATERAL:Keyword", "LEADING:Keyword",
                        "LEFT:Keyword", "LEVEL:Keyword", "LIKE:Keyword", "LIMIT:Keyword", "LN:Function", "LOCAL:Keyword", "LOCALTIME:Keyword", "LOCALTIMESTAMP:Keyword",
                        "LOWER:Function", "MATCH:Keyword", "MAX:Function", "MEMBER:Keyword", "MERGE:Keyword", "METHOD:Keyword", "MIN:Function", "MINUTE:Keyword", "MOD:Function",
                        "MODIFIES:Keyword", "MODULE:Keyword", "MONTH:Keyword", "MULTISET:Keyword", "NATIONAL:Keyword", "NATURAL:Keyword", "NCHAR:Keyword", "NCLOB:Keyword",
                        "NEW:Keyword", "NEXT:Keyword", "NO:Keyword", "NONE:Keyword", "NORMALIZE:Function", "NOT:Operator", "NULL:Keyword", "NULLIF:Function", "NUMERIC:Keyword",
                        "OCTET_LENGTH:Function", "OF:Keyword", "OFFSET:Keyword", "OLD:Keyword", "ON:Keyword", "ONLY:Keyword", "OPEN:Keyword", "OPTION:Keyword", "OR:Operator",
                        "ORDER:Keyword", "OUT:Keyword", "OUTER:Keyword", "OVER:Keyword", "OVERLAPS:Keyword", "OVERLAY:Function", "PARAMETER:Keyword", "PARTITION:Keyword",
                        "PERCENT:Keyword", "PERCENT_RANK:Function", "PERIOD:Keyword", "PORTION:Keyword", "POSITION:Function", "POWER:Function", "PRECEDING:Keyword",
                        "PRECISION:Keyword", "PREPARE:Keyword", "PRIMARY:Keyword", "PROCEDURE:Keyword", "RANGE:Keyword", "RANK:Function", "READS:Keyword", "REAL:Keyword",
                        "RECURSIVE:Keyword", "REF:Keyword", "REFERENCES:Keyword", "REFERENCING:Keyword", "REGR_AVGX:Function", "REGR_AVGY:Function", "REGR_COUNT:Function",
                        "REGR_INTERCEPT:Function", "REGR_R2:Function", "REGR_SLOPE:Function", "REGR_SXX:Function", "REGR_SXY:Function", "REGR_SYY:Function",
                        "RELATIVE:Keyword", "RELEASE:Keyword", "REPEAT:Keyword", "RESIGNAL:Keyword", "RESTRICT:Keyword", "RESULT:Keyword", "RETURN:Keyword",
                        "RETURNS:Keyword", "REVOKE:Keyword", "RIGHT:Keyword", "ROLLBACK:Keyword", "ROLLUP:Keyword", "ROW:Keyword", "ROW_NUMBER:Function", "ROWS:Keyword",
                        "SAVEPOINT:Keyword", "SCHEMA:Keyword", "SCOPE:Keyword", "SCROLL:Keyword", "SEARCH:Keyword", "SECOND:Keyword", "SELECT:Keyword", "SENSITIVE:Keyword",
                        "SESSION_USER:Keyword", "SET:Keyword", "SIGNAL:Keyword", "SIMILAR:Keyword", "SMALLINT:Keyword", "SOME:Keyword", "SPECIFIC:Keyword",
                        "SPECIFICTYPE:Keyword", "SQL:Keyword", "SQLEXCEPTION:Keyword", "SQLSTATE:Keyword", "SQLWARNING:Keyword", "START:Keyword", "STATIC:Keyword",
                        "STDDEV_POP:Function", "STDDEV_SAMP:Function", "SUBMULTISET:Keyword", "SUBSTRING:Function", "SUM:Function", "SYMMETRIC:Keyword",
                        "SYSTEM:Keyword", "SYSTEM_USER:Keyword", "TABLE:Keyword", "TABLESAMPLE:Keyword", "THEN:Keyword", "TIME:Keyword", "TIMESTAMP:Keyword",
                        "TIMEZONE_HOUR:Keyword", "TIMEZONE_MINUTE:Keyword", "TO:Keyword", "TRAILING:Keyword", "TRANSACTION:Keyword", "TRANSLATE:Function",
                        "TRANSLATION:Keyword", "TREAT:Keyword", "TRIGGER:Keyword", "TRIM:Function", "TRUE:Keyword", "UESCAPE:Keyword", "UNBOUNDED:Keyword", "UNION:Keyword",
                        "UNIQUE:Keyword", "UNKNOWN:Keyword", "UNNEST:Keyword", "UPDATE:Keyword", "UPPER:Function", "USAGE:Keyword", "USER:Keyword", "USING:Keyword",
                        "VALUE:Keyword", "VALUES:Keyword", "VAR_POP:Function", "VAR_SAMP:Function", "VARCHAR:Keyword", "VARYING:Keyword", "WHEN:Keyword", "WHENEVER:Keyword",
                        "WHERE:Keyword", "WIDTH_BUCKET:Function", "WINDOW:Keyword", "WITH:Keyword", "WITHIN:Keyword", "WITHOUT:Keyword", "YEAR:Keyword", "SUBSTR:Function"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private SqlStandardKeywords()
        {
                /* DO NOTHING... */
        }
}