package valkyrie.driver.dm;

import valkyrie.driver.keywords.SqlStandardKeywords;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 达梦标准关键字
 *
 * @author Luo Tiansheng
 * @since 2026/5/7
 */
public class DMKeywords
{
        public static final Set<String> KEYWORDS;

        static {
                Set<String> keywords = new LinkedHashSet<>();

                Collections.addAll(keywords,
                        // 系统函数
                        "SYSDATE", "SYSTIMESTAMP", "UID", "USER", "USERENV", "SYS_GUID",
                        "FROM_BASE64",
                        // 层次查询
                        "CONNECT_BY_ISCYCLE", "CONNECT_BY_ISLEAF", "CONNECT_BY_ROOT", "PRIOR", "LEVEL",
                        // 序列
                        "CURRVAL", "NEXTVAL", "INCREMENT", "CYCLE", "NOMAXVALUE", "NOMINVALUE", "MAXSIZE",
                        "MINEXTENTS", "MAXEXTENTS", "CACHE", "NOCACHE", "ORDER", "NOORDER",
                        // 表空间与数据文件
                        "TABLESPACE", "DATAFILE", "DBFILE", "TEMPFILE", "TEMP", "UNDO", "ONLINE", "OFFLINE",
                        "AUTOEXTEND", "NEXT", "MAXSIZE", "UNLIMITED", "FREELIST", "FREELISTS", "PCTFREE",
                        "PCTUSED", "INITRANS", "MAXTRANS",
                        // 归档与恢复
                        "ARCHIVELOG", "NOARCHIVELOG", "ARCHIVEDIR", "RECOVER", "BACKUP", "RESTORE",
                        // 用户与权限
                        "IDENTIFIED", "AUTHID", "SYSDBA", "DBA", "PUBLIC", "PRIVATE", "ROLE",
                        // 闪回
                        "AS OF", "TIMESTAMP", "SCN", "FLASHBACK", "VERSIONS", "MINING",
                        // 约束
                        "DISABLE", "ENABLE", "VALIDATE", "NOVALIDATE", "DEFERRED", "IMMEDIATE",
                        // 索引类型
                        "BITMAP", "HASH", "CLUSTER", "FUNCTION", "DOMAIN",
                        // 会话与事务
                        "SESSION", "INSTANCE", "TRANSACTION", "MOUNT", "EXCLUSIVE", "RESOURCE",
                        // PL/SQL 相关
                        "PRAGMA", "EXCEPTION", "RAISE", "ASSERT", "AUTONOMOUS_TRANSACTION",
                        // 行列转换
                        "PIVOT", "UNPIVOT", "ANY", "SOME",
                        // 分区
                        "PARTITION", "SUBPARTITION", "RANGE", "LIST", "HASH", "VALUE", "KEY",
                        // 其他达梦特有
                        "ABORT", "AUTO", "BLOCKSIZE", "BLOCK_SIZE", "BUFFER", "CLUSTER", "COMMENT", "COMPRESS",
                        "CONNECT", "DECRYPT", "ENCRYPT", "EXIT", "FILE", "FREELIST", "GLOBALLY", "KEEP",
                        "LOGFILE", "LOGGING", "MODE", "MOUNT", "NOCOMPRESS", "NOCYCLE", "NOLOGGING", "NOSORT",
                        "NOWAIT", "NUMBER", "NVARCHAR", "NVARCHAR2", "PACKAGE", "PLAN", "RAW", "REUSE",
                        "ROWID", "ROWNUM", "SAVEPOINT", "SEQUENCE", "SHARE", "SIZE", "SNAPSHOT", "START",
                        "SYNONYM", "THREAD", "TRIGGER", "UNLIMITED", "VARCHAR", "VARCHAR2", "INT", "WAIT",
                        "DUAL", "DECLARE", "END", "BODY", "FOR", "LOOP", "WHILE", "IF", "THEN", "ELSE",
                        "ELSIF", "CASE", "WHEN", "RETURN", "OUT", "INOUT", "IS", "AS", "PROCEDURE",
                        "FUNCTION", "PACKAGE", "TYPE", "RECORD", "TABLE", "VARRAY", "CURSOR", "OPEN",
                        "CLOSE", "FETCH", "NULL", "TRUE", "FALSE", "DEFAULT", "NOT", "UNIQUE", "CHECK",
                        "PRIMARY", "FOREIGN", "REFERENCES", "CONSTRAINT", "INDEX", "VIEW", "SEQUENCE",
                        "GRANT", "REVOKE", "COMMIT", "ROLLBACK", "SAVEPOINT", "SET", "SHOW", "SVRMODE",
                        "LINK", "DATABASE", "SCHEMA", "DATAFILE", "LOGFILE", "SIZE", "OFFLINE", "ONLINE",
                        "BEGIN", "DECLARE", "EXCEPTION", "EXISTS", "KILL", "PURGE", "REBUILD", "RENAME",
                        "REPAIR", "STORAGE", "TRUNCATE", "UNDOFILE", "WORK", "RELEASE", "ROW", "ROWS",
                        "READ", "WRITE", "ONLY", "WITH", "GRAGMENT", "LOCATION", "REPLICATED", "LOCAL",
                        "GLOBAL", "IMMUTABLE", "STABLE", "VOLATILE", "CALLED", "RETURNING", "BULK",
                        "COLLECT", "FORALL", "LIMIT", "REJECT", "LOG", "SKIP", "SWAP", "TRIM", "CONCAT",
                        "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "TIMEZONE", "DAY", "TIME",
                        // 函数包
                        "UTL_ENCODE", "BASE64_DECODE",
                        "UTL_RAW", "CAST_TO_VARCHAR2", "CAST_TO_RAW"
                );

                keywords.addAll(SqlStandardKeywords.KEYWORDS);
                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private DMKeywords()
        {
                /* DO NOTHING... */
        }
}