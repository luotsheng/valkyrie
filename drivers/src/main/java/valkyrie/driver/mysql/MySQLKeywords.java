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
                        // 存储过程与函数
                        "ACCESSIBLE", "AGAINST", "ASENSITIVE", "CONDITION", "CONTINUE", "CURSOR", "DETERMINISTIC",
                        "EACH", "ELSEIF", "EXIT", "FETCH", "GOTO", "INOUT", "ITERATE", "LEAVE", "LOOP", "MODIFIES",
                        "PREPARE", "READS", "REPEAT", "REQUIRE", "RETURN", "SCHEMAS", "SENSITIVE", "SPECIFIC",
                        "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "UNDO", "WHILE", "FROM_BASE64",
                        // 全文搜索
                        "FULLTEXT", "MATCH", "AGAINST",
                        // 索引
                        "KEY", "KEYS", "SPATIAL", "USING", "BTREE", "HASH",
                        // 字符串类型与函数
                        "ASCII", "BINARY", "BLOB", "CHARACTER", "COLLATE", "VARCHAR", "VARYING", "LINEAR",
                        // 数值类型
                        "BIT", "INT", "INTEGER", "MEDIUMINT", "SMALLINT", "TINYINT", "FLOAT", "DOUBLE", "REAL",
                        "NUMERIC", "DECIMAL", "PRECISION", "UNSIGNED", "ZEROFILL", "MAXVALUE", "MINVALUE",
                        // 存储与加载
                        "LINES", "TERMINATED", "ENCLOSED", "ESCAPED", "LOAD", "INFILE", "OUTFILE", "DELIMITER",
                        // 时间类型
                        "TIME", "TIMESTAMP", "INTERVAL", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE",
                        "DAY_SECOND", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "MINUTE_MICROSECOND",
                        "SECOND_MICROSECOND", "YEAR_MONTH",
                        // 排序与分区
                        "PARTITION", "PARTITIONS", "SUBPARTITION", "SUBPARTITIONS", "RANGE", "LIST",
                        "HASH", "KEY", "COLUMNS", "LINEAR", "ALGORITHM",
                        // 事务与锁
                        "LOCK", "UNLOCK", "READ", "WRITE", "LOCAL", "LOW_PRIORITY", "HIGH_PRIORITY",
                        "DELAYED", "NO_WRITE_TO_BINLOG", "BINLOG",
                        // 用户与权限
                        "USER", "ROLE", "PASSWORD", "GRANT", "REVOKE", "ssl", "cipher", "issuer", "subject",
                        // 服务器选项
                        "AUTO", "AUTO_INCREMENT", "AVG_ROW_LENGTH", "BACKUP", "CACHE", "CHECKSUM", "COMMENT",
                        "COMPRESS", "CONNECTION", "DATA", "DIRECTORY", "DISK", "DO", "DUMPFILE", "ENCRYPTION",
                        "ENGINE", "FIRST", "FIXED", "FLUSH", "FULL", "GLOBAL", "IGNORE", "INDEX", "INSERT_METHOD",
                        "KEY_BLOCK_SIZE", "LAST", "MAX_ROWS", "MEMORY", "MIN_ROWS", "MODIFY", "MRG_MYISAM",
                        "PACK_KEYS", "PERSISTENT", "PROCEDURE", "QUICK", "RAID_0", "RAID_1", "RAID_5",
                        "REBUILD", "REPAIR", "REPLICATE", "RESTORE", "ROLLUP", "ROW_FORMAT", "SLOW",
                        "STORAGE", "UNIQUE", "USE_FRM", "VIRTUAL",
                        // 字符集与校验规则
                        "CHARSET", "COLLATION", "UPPERCASE", "UNICODE", "UTF8", "UTF8MB4", "LATIN1", "ASCII",
                        // JSON 相关
                        "JSON", "JSON_ARRAY", "JSON_OBJECT", "JSON_EXTRACT", "JSON_SET", "JSON_INSERT",
                        "JSON_REPLACE", "JSON_REMOVE", "JSON_MERGE", "JSON_MERGE_PATCH", "JSON_MERGE_PRESERVE",
                        "JSON_QUOTE", "JSON_UNQUOTE", "JSON_ARRAY_APPEND", "JSON_ARRAY_INSERT", "JSON_CONTAINS",
                        "JSON_CONTAINS_PATH", "JSON_DEPTH", "JSON_DOCUMENT", "JSON_EACH", "JSON_EXISTS",
                        "JSON_EXTRACT", "JSON_GET", "JSON_HAS", "JSON_INSERT", "JSON_KEYS", "JSON_LENGTH",
                        "JSON_MERGE", "JSON_MERGE_PATCH", "JSON_MERGE_PRESERVE", "JSON_OBJECT", "JSON_OVERLAPS",
                        "JSON_PRETTY", "JSON_QUERY", "JSON_QUOTE", "JSON_REMOVE", "JSON_REPLACE", "JSON_SCHEMA",
                        "JSON_SEARCH", "JSON_SET", "JSON_STORAGE_FREE", "JSON_STORAGE_SIZE", "JSON_TABLE",
                        "JSON_TYPE", "JSON_UNQUOTE", "JSON_VALID", "JSON_VALUE",
                        // 表达式与操作符
                        "REGEXP", "RLIKE", "SOUNDS", "XOR", "DIV", "DUAL", "BOTH", "LEADING", "TRAILING",
                        // 优化与分析
                        "ANALYZE", "EXPLAIN", "FORCE", "IGNORE", "OPTIMIZE", "STRAIGHT_JOIN", "SQL_CACHE",
                        "SQL_NO_CACHE", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SQL_BIG_RESULT",
                        "SQL_BUFFER_RESULT", "SQL_SAFE_UPDATES", "PROCEDURE", "FUNCTION",
                        // 复制
                        "MASTER", "SLAVE", "REPLICATION", "BINLOG", "SOURCE", "REPLICA", "GTID", "SERIALIZABLE",
                        "CHANNEL", "COLUMNS", "DOCUMENT", "EVENT", "EVENTS", "FILE", "GENERAL", "HOSTS",
                        "LOGS", "MASTER_LOG_FILE", "MASTER_LOG_POS", "MASTER_POS_WAIT", "RELAY_LOG_FILE",
                        "RELAY_LOG_POS", "RELAY_THREAD", "SOURCE_LOG_FILE", "SOURCE_LOG_POS",
                        // 会话
                        "SESSION", "GLOBAL", "SESSION_USER", "SYSTEM_USER", "CURRENT_USER", "CURRENT_ROLE",
                        "FOUND_ROWS", "LAST_INSERT_ID", "ROW_COUNT", "VERSION", "DATABASE", "SCHEMA",
                        // 窗口函数 (MySQL 8.0+)
                        "OVER", "WINDOW", "PARTITION", "RANGE", "ROWS", "UNBOUNDED", "PRECEDING", "FOLLOWING",
                        "CURRENT ROW", "EXCLUDE", "NO OTHERS", "TIES", "GROUPS",
                        // 其他
                        "ALWAYS", "CHANGE", "CHAR", "DISTINCTROW", "FLOAT4", "FLOAT8", "GET", "SET",
                        "INFILE", "KILL", "LINES", "OPTION", "OPTIONALLY", "PURGE", "QUICK", "RANK",
                        "RENAM", "REOKE", "ROW", "ROWS", "SHOW", "START", "STOP", "TRUNCATE", "UNLOCK",
                        "UPADTE", "ZEROFILL", "SIGNAL", "RESIGNAL", "GET_DIAGNOSTICS", "CONDITION_HANDLER",
                        "CONTINUE", "EXIT", "UNDO", "START TRANSACTION", "COMMIT", "ROLLBACK", "SAVEPOINT",
                        // 约束
                        "RESTRICT", "CASCADE", "SET NULL", "NO ACTION", "SET DEFAULT",
                        // 虚拟列与生成列
                        "GENERATED", "VIRTUAL", "STORED", "ALWAYS",
                        // 公共表表达式 (CTE)
                        "WITH", "RECURSIVE"
                );

                keywords.addAll(SqlStandardKeywords.KEYWORDS);
                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private MySQLKeywords()
        {
                /* DO NOTHING... */
        }
}