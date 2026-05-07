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
                        // 聚合函数（核心通用）
                        "AVG",
                        "SUM",
                        "COUNT",
                        "MAX",
                        "MIN",

                        // 数学函数
                        "ABS",
                        "CEIL",
                        "CEILING",
                        "FLOOR",
                        "ROUND",
                        "TRUNCATE",
                        "MOD",
                        "POWER",
                        "SQRT",
                        "EXP",
                        "LOG",
                        "LOG10",

                        // 字符串函数
                        "UPPER",
                        "LOWER",
                        "LENGTH",
                        "CHAR_LENGTH",
                        "SUBSTRING",
                        "SUBSTR",
                        "TRIM",
                        "LTRIM",
                        "RTRIM",
                        "CONCAT",
                        "REPLACE",
                        "INSTR",

                        // 日期时间函数（通用写法）
                        "NOW",
                        "CURRENT_DATE",
                        "CURRENT_TIME",
                        "CURRENT_TIMESTAMP",
                        "DATE",
                        "TIME",
                        "YEAR",
                        "MONTH",
                        "DAY",
                        "HOUR",
                        "MINUTE",
                        "SECOND",
                        "DATEDIFF",
                        "DATE_ADD",
                        "DATE_SUB",

                        // 类型转换
                        "CAST",
                        "CONVERT",

                        // NULL 处理
                        "COALESCE",
                        "NULLIF",
                        "IFNULL",

                        // 条件函数
                        "CASE",
                        "WHEN",
                        "THEN",
                        "ELSE",
                        "END",

                        // 窗口函数（现代 SQL）
                        "ROW_NUMBER",
                        "RANK",
                        "DENSE_RANK",
                        "NTILE",
                        "LAG",
                        "LEAD",

                        // JSON
                        "JSON_EXTRACT",
                        "JSON_ARRAY",
                        "JSON_OBJECT"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private SqlStandardKeywords()
        {
                /* DO NOTHING... */
        }
}
