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
                        // 聚合函数
                        "AVG", "SUM", "COUNT", "MAX", "MIN", "VAR", "VARIANCE", "STDDEV", "STDDEV_POP", "STDDEV_SAMP",
                        "BIT_AND", "BIT_OR", "BIT_XOR", "BOOL_AND", "BOOL_OR", "BOOL_XOR",
                        // 数学函数
                        "ABS", "CEIL", "CEILING", "FLOOR", "ROUND", "TRUNCATE", "TRUNC", "MOD", "POWER", "POW",
                        "SQRT", "CBRT", "EXP", "LOG", "LOG10", "LOG2", "LN", "LNP1", "PI", "RAND", "RANDOM", "SIGN",
                        "ACOS", "ASIN", "ATAN", "ATAN2", "COS", "COT", "SIN", "TAN",
                        // 字符串函数
                        "UPPER", "LOWER", "LENGTH", "CHAR_LENGTH", "CHARACTER_LENGTH", "OCTET_LENGTH", "BIT_LENGTH",
                        "SUBSTRING", "SUBSTR", "SUBSTRING_INDEX", "LEFT", "RIGHT", "LPAD", "RPAD", "TRIM", "LTRIM", "RTRIM",
                        "CONCAT", "CONCAT_WS", "REPLACE", "INSTR", "LOCATE", "POSITION", "ASCII", "CHAR", "SOUNDEX",
                        "SPACE", "STRCMP", "TRANSLATE", "REVERSE",
                        // 日期时间函数
                        "NOW", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "LOCALTIME", "LOCALTIMESTAMP",
                        "DATE", "TIME", "DATETIME", "TIMESTAMP", "YEAR", "MONTH", "MONTHNAME", "DAY", "DAYOFMONTH",
                        "DAYOFYEAR", "DAYOFWEEK", "WEEK", "WEEKDAY", "QUARTER", "HOUR", "MINUTE", "SECOND", "MICROSECOND",
                        "DATEDIFF", "DATE_ADD", "DATE_SUB", "ADDDATE", "SUBDATE", "TIMEDIFF", "TIMESTAMPADD",
                        "TIMESTAMPDIFF", "EXTRACT", "DATE_FORMAT", "TIME_FORMAT", "GETDATE", "SYSDATE",
                        // 类型转换函数
                        "CAST", "CONVERT", "TRY_CAST", "TRY_CONVERT",
                        // 空值处理函数
                        "COALESCE", "NULLIF", "IFNULL", "NVL", "NVL2",
                        // 条件表达式
                        "CASE", "WHEN", "THEN", "ELSE", "END", "DECODE",
                        // 窗口函数
                        "ROW_NUMBER", "RANK", "DENSE_RANK", "NTILE", "LAG", "LEAD", "FIRST_VALUE", "LAST_VALUE",
                        "CUME_DIST", "PERCENT_RANK",
                        // JSON 函数
                        "JSON_EXTRACT", "JSON_ARRAY", "JSON_OBJECT", "JSON_ARRAYAGG", "JSON_OBJECTAGG",
                        "JSON_VALUE", "JSON_QUERY", "JSON_EXISTS", "JSON_KEYS", "JSON_LENGTH", "JSON_SET",
                        "JSON_INSERT", "JSON_REPLACE", "JSON_REMOVE",
                        // 行值表达式
                        "ROW", "VALUES",
                        // 集合操作
                        "UNION", "UNION_ALL", "INTERSECT", "EXCEPT",
                        // 分页
                        "LIMIT", "OFFSET", "FETCH", "FIRST", "NEXT", "ROWS", "ONLY",
                        // 子查询
                        "IN", "EXISTS", "ANY", "SOME", "ALL",
                        // 别名与去重
                        "AS", "ALIAS", "DISTINCT",
                        // 表连接
                        "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "CROSS", "OUTER", "ON", "USING", "NATURAL",
                        // 谓词
                        "AND", "OR", "NOT", "BETWEEN", "LIKE", "ILIKE", "SIMILAR", "IS", "NULL", "TRUE", "FALSE", "UNKNOWN",
                        // 排序
                        "ORDER", "ASC", "DESC", "NULLS", "FIRST", "LAST",
                        // 分组
                        "GROUP", "HAVING", "GROUPING", "ROLLUP", "CUBE",
                        // 插入/更新/删除
                        "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE", "TRUNCATE",
                        // DDL
                        "SELECT", "FROM", "CREATE", "ALTER", "DROP", "RENAME", "TABLE", "INDEX", "VIEW", "DATABASE",
                        "SCHEMA", "COLUMN", "CONSTRAINT", "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "UNIQUE",
                        "CHECK", "DEFAULT", "AUTO_INCREMENT", "IDENTITY", "SEQUENCE", "TRIGGER", "PROCEDURE",
                        "FUNCTION", "PACKAGE", "BODY", "GRANT", "REVOKE", "COMMIT", "ROLLBACK", "SAVEPOINT",
                        "TRANSACTION", "BEGIN", "END", "CURSOR", "LOOP", "WHILE", "REPEAT", "LEAVE", "ITERATE",
                        "RETURN", "RETURNS", "OUT", "INOUT", "LANGUAGE", "PLPGSQL",
                        // 类型
                        "INTEGER", "INT", "SMALLINT", "BIGINT", "TINYINT", "FLOAT", "DOUBLE", "REAL", "DECIMAL",
                        "NUMERIC", "VARCHAR", "VARCHAR2", "CHAR", "CHARACTER", "TEXT", "STRING", "BOOLEAN", "BOOL",
                        "DATE", "TIME", "TIMESTAMP", "INTERVAL", "BLOB", "CLOB", "UUID", "JSON", "JSONB", "XML",
                        "ARRAY", "ENUM",
                        // 通用函数
                        "GREATEST", "LEAST", "IF", "IIF"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private SqlStandardKeywords()
        {
                /* DO NOTHING... */
        }
}
