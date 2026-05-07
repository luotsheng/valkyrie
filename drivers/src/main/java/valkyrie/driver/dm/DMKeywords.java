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
                        "ABORT",
                        "ARCHIVELOG",
                        "ARCHIVEDIR",
                        "AUTO",
                        "AUTOEXTEND",
                        "BITMAP",
                        "CACHE",
                        "CASCADED",
                        "CHECKPOINT",
                        "CLUSTER",
                        "COMMENT",
                        "COMPRESS",
                        "CONNECT",
                        "CONNECT_BY_ISCYCLE",
                        "CONNECT_BY_ISLEAF",
                        "CONNECT_BY_ROOT",
                        "CURRVAL",
                        "CYCLE",
                        "DATAFILE",
                        "DBFILE",
                        "DECRYPT",
                        "ENCRYPT",
                        "EXCLUSIVE",
                        "EXIT",
                        "FILE",
                        "FREELIST",
                        "FREELISTS",
                        "GLOBALLY",
                        "HASH",
                        "IDENTIFIED",
                        "INCREMENT",
                        "INITRANS",
                        "INSTANCE",
                        "KEEP",
                        "LOGFILE",
                        "LOGGING",
                        "MAXEXTENTS",
                        "MAXSIZE",
                        "MINEXTENTS",
                        "MINUS",
                        "MODE",
                        "MOUNT",
                        "NEXTVAL",
                        "NOARCHIVELOG",
                        "NOCACHE",
                        "NOCOMPRESS",
                        "NOCYCLE",
                        "NOLOGGING",
                        "NOMAXVALUE",
                        "NOMINVALUE",
                        "NOSORT",
                        "NOWAIT",
                        "NUMBER",
                        "NVARCHAR",
                        "NVARCHAR2",
                        "OFFLINE",
                        "ONLINE",
                        "PACKAGE",
                        "PCTFREE",
                        "PCTUSED",
                        "PLAN",
                        "PRAGMA",
                        "PRIOR",
                        "PRIVATE",
                        "PUBLIC",
                        "RAW",
                        "RECOVER",
                        "RESOURCE",
                        "REUSE",
                        "ROLE",
                        "ROWID",
                        "ROWNUM",
                        "SAVEPOINT",
                        "SEQUENCE",
                        "SESSION",
                        "SHARE",
                        "SIZE",
                        "SNAPSHOT",
                        "START",
                        "SYNONYM",
                        "SYSDBA",
                        "SYSDATE",
                        "SYSTIMESTAMP",
                        "TABLESPACE",
                        "TEMP",
                        "TEMPFILE",
                        "THREAD",
                        "TRANSACTION",
                        "TRIGGER",
                        "UID",
                        "UNLIMITED",
                        "VALIDATE",
                        "VARCHAR",
                        "VARCHAR2",
                        "INT",
                        "WAIT",
                        "DUAL"
                );

                keywords.addAll(SqlStandardKeywords.KEYWORDS);
                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private DMKeywords()
        {
                /* DO NOTHING... */
        }
}
