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
                        "ACCESSIBLE:Keyword", "AGAINST:Function", "ASENSITIVE:Keyword", "CONDITION:Keyword", "CONTINUE:Keyword", "CURSOR:Keyword", "DETERMINISTIC:Keyword",
                        "EACH:Keyword", "ELSEIF:Keyword", "EXIT:Keyword", "FETCH:Keyword", "GOTO:Keyword", "INOUT:Keyword", "ITERATE:Keyword", "LEAVE:Keyword", "LOOP:Keyword", "MODIFIES:Keyword",
                        "PREPARE:Keyword", "READS:Keyword", "REPEAT:Keyword", "REQUIRE:Keyword", "RETURN:Keyword", "SCHEMAS:Keyword", "SENSITIVE:Keyword", "SPECIFIC:Keyword",
                        "SQLEXCEPTION:Keyword", "SQLSTATE:Keyword", "SQLWARNING:Keyword", "UNDO:Keyword", "WHILE:Keyword", "FROM_BASE64:Function",
                        // 全文搜索
                        "FULLTEXT:Keyword", "MATCH:Keyword",
                        // 索引
                        "KEY:Keyword", "KEYS:Keyword", "SPATIAL:Keyword", "USING:Keyword", "BTREE:Keyword", "HASH:Keyword",
                        // 字符串类型与函数
                        "ASCII:Function", "BINARY:Keyword", "BLOB:Keyword", "CHARACTER:Keyword", "COLLATE:Keyword", "VARCHAR:Keyword", "VARYING:Keyword", "LINEAR:Keyword",
                        // 数值类型
                        "BIT:Keyword", "INT:Keyword", "INTEGER:Keyword", "MEDIUMINT:Keyword", "SMALLINT:Keyword", "TINYINT:Keyword", "FLOAT:Keyword", "DOUBLE:Keyword", "REAL:Keyword",
                        "NUMERIC:Keyword", "DECIMAL:Keyword", "PRECISION:Keyword", "UNSIGNED:Keyword", "ZEROFILL:Keyword", "MAXVALUE:Keyword", "MINVALUE:Keyword",
                        // 存储与加载
                        "LINES:Keyword", "TERMINATED:Keyword", "ENCLOSED:Keyword", "ESCAPED:Keyword", "LOAD:Keyword", "INFILE:Keyword", "OUTFILE:Keyword", "DELIMITER:Keyword",
                        // 时间类型
                        "TIME:Keyword", "TIMESTAMP:Keyword", "INTERVAL:Keyword", "DAY_HOUR:Keyword", "DAY_MICROSECOND:Keyword", "DAY_MINUTE:Keyword",
                        "DAY_SECOND:Keyword", "HOUR_MICROSECOND:Keyword", "HOUR_MINUTE:Keyword", "HOUR_SECOND:Keyword", "MINUTE_MICROSECOND:Keyword",
                        "SECOND_MICROSECOND:Keyword", "YEAR_MONTH:Keyword",
                        // 排序与分区
                        "PARTITION:Keyword", "PARTITIONS:Keyword", "SUBPARTITION:Keyword", "SUBPARTITIONS:Keyword", "RANGE:Keyword", "LIST:Keyword",
                        "HASH:Keyword", "KEY:Keyword", "COLUMNS:Keyword", "LINEAR:Keyword", "ALGORITHM:Keyword",
                        // 事务与锁
                        "LOCK:Keyword", "UNLOCK:Keyword", "READ:Keyword", "WRITE:Keyword", "LOCAL:Keyword", "LOW_PRIORITY:Keyword", "HIGH_PRIORITY:Keyword",
                        "DELAYED:Keyword", "NO_WRITE_TO_BINLOG:Keyword", "BINLOG:Keyword",
                        // 用户与权限
                        "USER:Keyword", "ROLE:Keyword", "PASSWORD:Keyword", "GRANT:Keyword", "REVOKE:Keyword", "ssl:Keyword", "cipher:Keyword", "issuer:Keyword", "subject:Keyword",
                        // 服务器选项
                        "AUTO:Keyword", "AUTO_INCREMENT:Keyword", "AVG_ROW_LENGTH:Keyword", "BACKUP:Keyword", "CACHE:Keyword", "CHECKSUM:Keyword", "COMMENT:Keyword",
                        "COMPRESS:Keyword", "CONNECTION:Keyword", "DATA:Keyword", "DIRECTORY:Keyword", "DISK:Keyword", "DO:Keyword", "DUMPFILE:Keyword", "ENCRYPTION:Keyword",
                        "ENGINE:Keyword", "FIRST:Keyword", "FIXED:Keyword", "FLUSH:Keyword", "FULL:Keyword", "GLOBAL:Keyword", "IGNORE:Keyword", "INDEX:Keyword", "INSERT_METHOD:Keyword",
                        "KEY_BLOCK_SIZE:Keyword", "LAST:Keyword", "MAX_ROWS:Keyword", "MEMORY:Keyword", "MIN_ROWS:Keyword", "MODIFY:Keyword", "MRG_MYISAM:Keyword",
                        "PACK_KEYS:Keyword", "PERSISTENT:Keyword", "PROCEDURE:Keyword", "QUICK:Keyword", "RAID_0:Keyword", "RAID_1:Keyword", "RAID_5:Keyword",
                        "REBUILD:Keyword", "REPAIR:Keyword", "REPLICATE:Keyword", "RESTORE:Keyword", "ROLLUP:Keyword", "ROW_FORMAT:Keyword", "SLOW:Keyword",
                        "STORAGE:Keyword", "UNIQUE:Keyword", "USE_FRM:Keyword", "VIRTUAL:Keyword",
                        // 字符集与校验规则
                        "CHARSET:Keyword", "COLLATION:Keyword", "UPPERCASE:Keyword", "UNICODE:Keyword", "UTF8:Keyword", "UTF8MB4:Keyword", "LATIN1:Keyword", "ASCII:Function",
                        // JSON 相关
                        "JSON:Keyword", "JSON_ARRAY:Function", "JSON_OBJECT:Function", "JSON_EXTRACT:Function", "JSON_SET:Function", "JSON_INSERT:Function",
                        "JSON_REPLACE:Function", "JSON_REMOVE:Function", "JSON_MERGE:Function", "JSON_MERGE_PATCH:Function", "JSON_MERGE_PRESERVE:Function", "JSON_QUOTE:Function", "JSON_UNQUOTE:Function", "JSON_ARRAY_APPEND:Function", "JSON_ARRAY_INSERT:Function", "JSON_CONTAINS:Function",
                        "JSON_CONTAINS_PATH:Function", "JSON_DEPTH:Function", "JSON_DOCUMENT:Function", "JSON_EACH:Function", "JSON_EXISTS:Function",
                        "JSON_EXTRACT:Function", "JSON_GET:Function", "JSON_HAS:Function", "JSON_INSERT:Function", "JSON_KEYS:Function", "JSON_LENGTH:Function",
                        "JSON_MERGE:Function", "JSON_MERGE_PATCH:Function", "JSON_MERGE_PRESERVE:Function", "JSON_OBJECT:Function", "JSON_OVERLAPS:Function",
                        "JSON_PRETTY:Function", "JSON_QUERY:Function", "JSON_QUOTE:Function", "JSON_REMOVE:Function", "JSON_REPLACE:Function", "JSON_SCHEMA:Function",
                        "JSON_SEARCH:Function", "JSON_SET:Function", "JSON_STORAGE_FREE:Function", "JSON_STORAGE_SIZE:Function", "JSON_TABLE:Function",
                        "JSON_TYPE:Function", "JSON_UNQUOTE:Function", "JSON_VALID:Function", "JSON_VALUE:Function",
                        // 表达式与操作符
                        "REGEXP:Operator", "RLIKE:Operator", "SOUNDS:Function", "XOR:Operator", "DIV:Operator", "DUAL:Keyword", "BOTH:Keyword", "LEADING:Keyword", "TRAILING:Keyword",
                        // 优化与分析
                        "ANALYZE:Keyword", "EXPLAIN:Keyword", "FORCE:Keyword", "IGNORE:Keyword", "OPTIMIZE:Function", "STRAIGHT_JOIN:Keyword", "SQL_CACHE:Keyword",
                        "SQL_NO_CACHE:Keyword", "SQL_CALC_FOUND_ROWS:Keyword", "SQL_SMALL_RESULT:Keyword", "SQL_BIG_RESULT:Keyword",
                        "SQL_BUFFER_RESULT:Keyword", "SQL_SAFE_UPDATES:Keyword", "PROCEDURE:Keyword", "FUNCTION:Keyword",
                        // 复制
                        "MASTER:Keyword", "SLAVE:Keyword", "REPLICATION:Keyword", "BINLOG:Keyword", "SOURCE:Keyword", "REPLICA:Keyword", "GTID:Keyword", "SERIALIZABLE:Keyword",
                        "CHANNEL:Keyword", "COLUMNS:Keyword", "DOCUMENT:Keyword", "EVENT:Keyword", "EVENTS:Keyword", "FILE:Keyword", "GENERAL:Keyword", "HOSTS:Keyword",
                        "LOGS:Keyword", "MASTER_LOG_FILE:Keyword", "MASTER_LOG_POS:Keyword", "MASTER_POS_WAIT:Function", "RELAY_LOG_FILE:Keyword", "RELAY_LOG_POS:Keyword",
                        "RELAY_THREAD:Keyword", "SOURCE_LOG_FILE:Keyword", "SOURCE_LOG_POS:Keyword",
                        // 会话
                        "SESSION:Keyword", "GLOBAL:Keyword", "SESSION_USER:Keyword", "SYSTEM_USER:Keyword", "CURRENT_USER:Keyword", "CURRENT_ROLE:Keyword",
                        "FOUND_ROWS:Function", "LAST_INSERT_ID:Function", "ROW_COUNT:Function", "VERSION:Function", "DATABASE:Keyword", "SCHEMA:Keyword",
                        // 窗口函数 (MySQL 8.0+)
                        "OVER:Keyword", "WINDOW:Keyword", "PARTITION:Keyword", "RANGE:Keyword", "ROWS:Keyword", "UNBOUNDED:Keyword", "PRECEDING:Keyword", "FOLLOWING:Keyword",
                        "CURRENT ROW:Keyword", "EXCLUDE:Keyword", "NO OTHERS:Keyword", "TIES:Keyword", "GROUPS:Keyword",
                        // 其他
                        "ALWAYS:Keyword", "CHANGE:Keyword", "CHAR:Keyword", "DISTINCTROW:Keyword", "FLOAT4:Keyword", "FLOAT8:Keyword", "GET:Keyword", "SET:Keyword",
                        "INFILE:Keyword", "KILL:Keyword", "LINES:Keyword", "OPTION:Keyword", "OPTIONALLY:Keyword", "PURGE:Keyword", "QUICK:Keyword", "RANK:Function",
                        "RENAM:Keyword", "REOKE:Keyword", "ROW:Keyword", "ROWS:Keyword", "SHOW:Keyword", "START:Keyword", "STOP:Keyword", "TRUNCATE:Keyword", "UNLOCK:Keyword",
                        "UPADTE:Keyword", "ZEROFILL:Keyword", "SIGNAL:Keyword", "RESIGNAL:Keyword", "GET_DIAGNOSTICS:Keyword", "CONDITION_HANDLER:Keyword",
                        "CONTINUE:Keyword", "EXIT:Keyword", "UNDO:Keyword", "START TRANSACTION:Keyword", "COMMIT:Keyword", "ROLLBACK:Keyword", "SAVEPOINT:Keyword",
                        // 约束
                        "RESTRICT:Keyword", "CASCADE:Keyword", "SET NULL:Keyword", "NO ACTION:Keyword", "SET DEFAULT:Keyword",
                        // 虚拟列与生成列
                        "GENERATED:Keyword", "VIRTUAL:Keyword", "STORED:Keyword", "ALWAYS:Keyword",
                        // 公共表表达式 (CTE)
                        "WITH:Keyword", "RECURSIVE:Keyword"
                );

                keywords.addAll(SqlStandardKeywords.KEYWORDS);
                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private MySQLKeywords()
        {
                /* DO NOTHING... */
        }
}