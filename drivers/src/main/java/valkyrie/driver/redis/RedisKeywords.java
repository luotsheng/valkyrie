package valkyrie.driver.redis;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Redis 关键字
 *
 * @author Luo Tiansheng
 * @since 2026/5/7
 */
public class RedisKeywords
{
        static final Set<String> KEYWORDS;

        static {
                Set<String> keywords = new LinkedHashSet<>();

                Collections.addAll(keywords,
                        // 连接与认证
                        "AUTH:Function", "ECHO:Function", "HELLO:Function", "PING:Function", "QUIT:Function", "SELECT:Function", "SWAPDB:Function",
                        // 字符串操作
                        "APPEND:Function", "DECR:Function", "DECRBY:Function", "GET:Function", "GETDEL:Function", "GETEX:Function", "GETRANGE:Function", "GETSET:Function",
                        "INCR:Function", "INCRBY:Function", "INCRBYFLOAT:Function", "MGET:Function", "MSET:Function", "MSETNX:Function", "PSETEX:Function", "SET:Function",
                        "SETBIT:Function", "SETEX:Function", "SETNX:Function", "SETRANGE:Function", "STRLEN:Function", "SUBSTR:Function",
                        // 列表操作
                        "BLMOVE:Function", "BLPOP:Function", "BRPOP:Function", "BRPOPLPUSH:Function", "LINDEX:Function", "LINSERT:Function", "LLEN:Function", "LMOVE:Function",
                        "LPOP:Function", "LPOS:Function", "LPUSH:Function", "LPUSHX:Function", "LRANGE:Function", "LREM:Function", "LSET:Function", "LTRIM:Function", "RPOP:Function",
                        "RPOPLPUSH:Function", "RPUSH:Function", "RPUSHX:Function",
                        // 集合操作
                        "SADD:Function", "SCARD:Function", "SDIFF:Function", "SDIFFSTORE:Function", "SINTER:Function", "SINTERSTORE:Function", "SISMEMBER:Function",
                        "SMISMEMBER:Function", "SMEMBERS:Function", "SMOVE:Function", "SPOP:Function", "SRANDMEMBER:Function", "SREM:Function", "SSCAN:Function", "SUNION:Function",
                        "SUNIONSTORE:Function",
                        // 有序集合操作
                        "ZADD:Function", "ZCARD:Function", "ZCOUNT:Function", "ZDIFF:Function", "ZDIFFSTORE:Function", "ZINCRBY:Function", "ZINTER:Function", "ZINTERSTORE:Function",
                        "ZLEXCOUNT:Function", "ZMSCORE:Function", "ZPOPMAX:Function", "ZPOPMIN:Function", "ZRANDMEMBER:Function", "ZRANGE:Function", "ZRANGEBYLEX:Function",
                        "ZRANGEBYSCORE:Function", "ZRANK:Function", "ZREM:Function", "ZREMRANGEBYLEX:Function", "ZREMRANGEBYRANK:Function", "ZREMRANGEBYSCORE:Function",
                        "ZREVRANGE:Function", "ZREVRANGEBYLEX:Function", "ZREVRANGEBYSCORE:Function", "ZREVRANK:Function", "ZSCORE:Function", "ZUNION:Function",
                        "ZUNIONSTORE:Function",
                        // 哈希操作
                        "HDEL:Function", "HEXISTS:Function", "HGET:Function", "HGETALL:Function", "HINCRBY:Function", "HINCRBYFLOAT:Function", "HKEYS:Function", "HLEN:Function",
                        "HMGET:Function", "HMSET:Function", "HRANDFIELD:Function", "HSCAN:Function", "HSET:Function", "HSETNX:Function", "HSTRLEN:Function", "HVALS:Function",
                        // 超长文本/比特操作
                        "BITCOUNT:Function", "BITFIELD:Function", "BITOP:Function", "BITPOS:Function", "GETBIT:Function", "SETBIT:Function",
                        // Geo 地理操作
                        "GEOADD:Function", "GEODIST:Function", "GEOHASH:Function", "GEOPOS:Function", "GEORADIUS:Function", "GEORADIUSBYMEMBER:Function", "GEOSEARCH:Function",
                        "GEOSEARCHSTORE:Function",
                        // Stream 流操作
                        "XACK:Function", "XADD:Function", "XAUTOCLAIM:Function", "XCLAIM:Function", "XDEL:Function", "XGROUP:Function", "XINFO:Function", "XLEN:Function", "XPENDING:Function",
                        "XRANGE:Function", "XREAD:Function", "XREADGROUP:Function", "XREVRANGE:Function", "XTRIM:Function",
                        // 键操作
                        "COPY:Function", "DEL:Function", "EXISTS:Function", "EXPIRE:Function", "EXPIREAT:Function", "KEYS:Function", "MOVE:Function", "OBJECT:Function", "PERSIST:Function",
                        "PEXPIRE:Function", "PEXPIREAT:Function", "PTTL:Function", "RANDOMKEY:Function", "RENAME:Function", "RENAMENX:Function", "RESTORE:Function",
                        "SORT:Function", "TOUCH:Function", "TTL:Function", "TYPE:Function", "UNLINK:Function", "WAIT:Function", "WATCH:Function", "UNWATCH:Function",
                        // 数据库操作
                        "DBSIZE:Function", "FLUSHALL:Function", "FLUSHDB:Function", "LASTSAVE:Function", "SAVE:Function",
                        // 脚本与事务
                        "EVAL:Function", "EVALSHA:Function", "EVALSHA_RO:Function", "EVAL_RO:Function", "FCALL:Function", "FCALL_RO:Function", "SCRIPT:Function",
                        "DEBUG:Function", "MULTI:Function", "EXEC:Function", "DISCARD:Function",
                        // 集群操作
                        "CLUSTER:Keyword", "READONLY:Function", "READWRITE:Function", "REPLICAOF:Function", "SLAVEOF:Function", "ROLE:Function",
                        // 慢查询与监控
                        "SLOWLOG:Function", "LATENCY:Function", "MONITOR:Function", "MEMORY:Function", "INFO:Function",
                        // 配置与服务器
                        "ACL:Keyword", "ACL_CAT:Function", "ACL_DELUSER:Function", "ACL_DRYRUN:Function", "ACL_GETUSER:Function", "ACL_LIST:Function",
                        "ACL_LOAD:Function", "ACL_LOG:Function", "ACL_SETUSER:Function", "ACL_USERS:Function", "ACL WHOAMI:Function",
                        "BGREWRITEAOF:Function", "BGSAVE:Function", "COMMAND:Function", "COMMAND COUNT:Function", "COMMAND DOCS:Function", "COMMAND GETKEYS:Function",
                        "COMMAND GETKEYSANDFLAGS:Function", "COMMAND INFO:Function", "COMMAND LIST:Function", "CONFIG:Keyword", "FAILOVER:Function",
                        "MODULE:Keyword", "MODULE LIST:Function", "MODULE LOAD:Function", "MODULE UNLOAD:Function", "PUBLISH:Function", "PUBSUB:Keyword",
                        "PSUBSCRIBE:Function", "PUNSUBSCRIBE:Function", "SHUTDOWN:Function", "SUBSCRIBE:Function", "UNSUBSCRIBE:Function",
                        // 哨兵
                        "SENTINEL:Keyword", "MASTERS:Function", "SLAVES:Function", "INFO:Function", "GET:Function", "RESET:Function", "FAILOVER:Function", "RESET:Function",
                        "SET:Function", "MONITOR:Function", "REMOVE:Function", "CKQUORUM:Function", "DEBUG:Function", "FLUSHCONFIG:Function", "GETMASTER:Function",
                        "HARD_RESET:Function", "INFO:Function", "IS_MASTER_DOWN_BY_ADDR:Function", "MASTER:Keyword", "MYID:Function", "pending:Function",
                        "POSTRUN:Function", "PREREQUISITES:Function", "REMOVE:Function", "RESET:Function", "SET:Function", "SOFT_RESET:Function", "TYPE:Function"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private RedisKeywords()
        {
                /* DO NOTHING... */
        }
}