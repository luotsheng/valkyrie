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
                        "SYSDATE:Function", "SYSTIMESTAMP:Function", "UID:Function", "USER:Keyword", "USERENV:Function", "SYS_GUID:Function",
                        "FROM_BASE64:Function",
                        // 层次查询
                        "CONNECT_BY_ISCYCLE:Function", "CONNECT_BY_ISLEAF:Function", "CONNECT_BY_ROOT:Function", "PRIOR:Keyword", "LEVEL:Keyword",
                        // 序列
                        "CURRVAL:Keyword", "NEXTVAL:Keyword", "INCREMENT:Keyword", "CYCLE:Keyword", "NOMAXVALUE:Keyword", "NOMINVALUE:Keyword", "MAXSIZE:Keyword",
                        "MINEXTENTS:Keyword", "MAXEXTENTS:Keyword", "CACHE:Keyword", "NOCACHE:Keyword", "ORDER:Keyword", "NOORDER:Keyword",
                        // 表空间与数据文件
                        "TABLESPACE:Keyword", "DATAFILE:Keyword", "DBFILE:Keyword", "TEMPFILE:Keyword", "TEMP:Keyword", "UNDO:Keyword", "ONLINE:Keyword", "OFFLINE:Keyword",
                        "AUTOEXTEND:Keyword", "NEXT:Keyword", "MAXSIZE:Keyword", "UNLIMITED:Keyword", "FREELIST:Keyword", "FREELISTS:Keyword", "PCTFREE:Keyword",
                        "PCTUSED:Keyword", "INITRANS:Keyword", "MAXTRANS:Keyword",
                        // 归档与恢复
                        "ARCHIVELOG:Keyword", "NOARCHIVELOG:Keyword", "ARCHIVEDIR:Keyword", "RECOVER:Keyword", "BACKUP:Keyword", "RESTORE:Keyword",
                        // 用户与权限
                        "IDENTIFIED:Keyword", "AUTHID:Keyword", "SYSDBA:Keyword", "DBA:Keyword", "PUBLIC:Keyword", "PRIVATE:Keyword", "ROLE:Keyword",
                        // 闪回
                        "AS OF:Keyword", "TIMESTAMP:Keyword", "SCN:Keyword", "FLASHBACK:Keyword", "VERSIONS:Keyword", "MINING:Keyword",
                        // 约束
                        "DISABLE:Keyword", "ENABLE:Keyword", "VALIDATE:Keyword", "NOVALIDATE:Keyword", "DEFERRED:Keyword", "IMMEDIATE:Keyword",
                        // 索引类型
                        "BITMAP:Keyword", "HASH:Keyword", "CLUSTER:Keyword", "FUNCTION:Keyword", "DOMAIN:Keyword",
                        // 会话与事务
                        "SESSION:Keyword", "INSTANCE:Keyword", "TRANSACTION:Keyword", "MOUNT:Keyword", "EXCLUSIVE:Keyword", "RESOURCE:Keyword",
                        // PL/SQL 相关
                        "PRAGMA:Keyword", "EXCEPTION:Keyword", "RAISE:Keyword", "ASSERT:Keyword", "AUTONOMOUS_TRANSACTION:Keyword",
                        // 行列转换
                        "PIVOT:Keyword", "UNPIVOT:Keyword", "ANY:Keyword", "SOME:Keyword",
                        // 分区
                        "PARTITION:Keyword", "SUBPARTITION:Keyword", "RANGE:Keyword", "LIST:Keyword", "HASH:Keyword", "VALUE:Keyword", "KEY:Keyword",
                        // 其他达梦特有
                        "ABORT:Keyword", "AUTO:Keyword", "BLOCKSIZE:Keyword", "BLOCK_SIZE:Keyword", "BUFFER:Keyword", "CLUSTER:Keyword", "COMMENT:Keyword", "COMPRESS:Keyword",
                        "CONNECT:Keyword", "DECRYPT:Function", "ENCRYPT:Function", "EXIT:Keyword", "FILE:Keyword", "FREELIST:Keyword", "GLOBALLY:Keyword", "KEEP:Keyword",
                        "LOGFILE:Keyword", "LOGGING:Keyword", "MODE:Keyword", "MOUNT:Keyword", "NOCOMPRESS:Keyword", "NOCYCLE:Keyword", "NOLOGGING:Keyword", "NOSORT:Keyword",
                        "NOWAIT:Keyword", "NUMBER:Keyword", "NVARCHAR:Keyword", "NVARCHAR2:Keyword", "PACKAGE:Keyword", "PLAN:Keyword", "RAW:Keyword", "REUSE:Keyword",
                        "ROWID:Keyword", "ROWNUM:Keyword", "SAVEPOINT:Keyword", "SEQUENCE:Keyword", "SHARE:Keyword", "SIZE:Keyword", "SNAPSHOT:Keyword", "START:Keyword",
                        "SYNONYM:Keyword", "THREAD:Keyword", "TRIGGER:Keyword", "UNLIMITED:Keyword", "VARCHAR:Keyword", "VARCHAR2:Keyword", "INT:Keyword", "WAIT:Keyword",
                        "DUAL:Keyword", "DECLARE:Keyword", "END:Keyword", "BODY:Keyword", "FOR:Keyword", "LOOP:Keyword", "WHILE:Keyword", "IF:Keyword", "THEN:Keyword", "ELSE:Keyword",
                        "ELSIF:Keyword", "CASE:Keyword", "WHEN:Keyword", "RETURN:Keyword", "OUT:Keyword", "INOUT:Keyword", "IS:Keyword", "AS:Keyword", "PROCEDURE:Keyword",
                        "FUNCTION:Keyword", "PACKAGE:Keyword", "TYPE:Keyword", "RECORD:Keyword", "TABLE:Keyword", "VARRAY:Keyword", "CURSOR:Keyword", "OPEN:Keyword",
                        "CLOSE:Keyword", "FETCH:Keyword", "NULL:Keyword", "TRUE:Keyword", "FALSE:Keyword", "DEFAULT:Keyword", "NOT:Operator", "UNIQUE:Keyword", "CHECK:Keyword",
                        "PRIMARY:Keyword", "FOREIGN:Keyword", "REFERENCES:Keyword", "CONSTRAINT:Keyword", "INDEX:Keyword", "VIEW:Keyword", "SEQUENCE:Keyword",
                        "GRANT:Keyword", "REVOKE:Keyword", "COMMIT:Keyword", "ROLLBACK:Keyword", "SAVEPOINT:Keyword", "SET:Keyword", "SHOW:Keyword", "SVRMODE:Keyword",
                        "LINK:Keyword", "DATABASE:Keyword", "SCHEMA:Keyword", "DATAFILE:Keyword", "LOGFILE:Keyword", "SIZE:Keyword", "OFFLINE:Keyword", "ONLINE:Keyword",
                        "BEGIN:Keyword", "DECLARE:Keyword", "EXCEPTION:Keyword", "EXISTS:Keyword", "KILL:Keyword", "PURGE:Keyword", "REBUILD:Keyword", "RENAME:Keyword",
                        "REPAIR:Keyword", "STORAGE:Keyword", "TRUNCATE:Keyword", "UNDOFILE:Keyword", "WORK:Keyword", "RELEASE:Keyword", "ROW:Keyword", "ROWS:Keyword",
                        "READ:Keyword", "WRITE:Keyword", "ONLY:Keyword", "WITH:Keyword", "GRAGMENT:Keyword", "LOCATION:Keyword", "REPLICATED:Keyword", "LOCAL:Keyword",
                        "GLOBAL:Keyword", "IMMUTABLE:Keyword", "STABLE:Keyword", "VOLATILE:Keyword", "CALLED:Keyword", "RETURNING:Keyword", "BULK:Keyword",
                        "COLLECT:Keyword", "FORALL:Keyword", "LIMIT:Keyword", "REJECT:Keyword", "LOG:Keyword", "SKIP:Keyword", "SWAP:Keyword", "TRIM:Function", "CONCAT:Function",
                        "YEAR:Keyword", "MONTH:Keyword", "DAY:Keyword", "HOUR:Keyword", "MINUTE:Keyword", "SECOND:Keyword", "TIMEZONE:Keyword", "TIME:Keyword",
                        // 函数包
                        "UTL_ENCODE:Module", "BASE64_DECODE:Function",
                        "UTL_RAW:Module", "CAST_TO_VARCHAR2:Function", "CAST_TO_RAW:Function"
                );

                keywords.addAll(SqlStandardKeywords.KEYWORDS);
                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private DMKeywords()
        {
                /* DO NOTHING... */
        }
}