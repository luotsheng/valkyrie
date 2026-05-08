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
                        "AUTH", "ECHO", "HELLO", "PING", "QUIT", "SELECT", "SWAPDB",
                        // 字符串操作
                        "APPEND", "DECR", "DECRBY", "GET", "GETDEL", "GETEX", "GETRANGE", "GETSET",
                        "INCR", "INCRBY", "INCRBYFLOAT", "MGET", "MSET", "MSETNX", "PSETEX", "SET",
                        "SETBIT", "SETEX", "SETNX", "SETRANGE", "STRLEN", "SUBSTR",
                        // 列表操作
                        "BLMOVE", "BLPOP", "BRPOP", "BRPOPLPUSH", "LINDEX", "LINSERT", "LLEN", "LMOVE",
                        "LPOP", "LPOS", "LPUSH", "LPUSHX", "LRANGE", "LREM", "LSET", "LTRIM", "RPOP",
                        "RPOPLPUSH", "RPUSH", "RPUSHX",
                        // 集合操作
                        "SADD", "SCARD", "SDIFF", "SDIFFSTORE", "SINTER", "SINTERSTORE", "SISMEMBER",
                        "SMISMEMBER", "SMEMBERS", "SMOVE", "SPOP", "SRANDMEMBER", "SREM", "SSCAN", "SUNION",
                        "SUNIONSTORE",
                        // 有序集合操作
                        "ZADD", "ZCARD", "ZCOUNT", "ZDIFF", "ZDIFFSTORE", "ZINCRBY", "ZINTER", "ZINTERSTORE",
                        "ZLEXCOUNT", "ZMSCORE", "ZPOPMAX", "ZPOPMIN", "ZRANDMEMBER", "ZRANGE", "ZRANGEBYLEX",
                        "ZRANGEBYSCORE", "ZRANK", "ZREM", "ZREMRANGEBYLEX", "ZREMRANGEBYRANK", "ZREMRANGEBYSCORE",
                        "ZREVRANGE", "ZREVRANGEBYLEX", "ZREVRANGEBYSCORE", "ZREVRANK", "ZSCORE", "ZUNION",
                        "ZUNIONSTORE", "ZMSCORE",
                        // 哈希操作
                        "HDEL", "HEXISTS", "HGET", "HGETALL", "HINCRBY", "HINCRBYFLOAT", "HKEYS", "HLEN",
                        "HMGET", "HMSET", "HRANDFIELD", "HSCAN", "HSET", "HSETNX", "HSTRLEN", "HVALS",
                        // 超长文本/比特操作
                        "BITCOUNT", "BITFIELD", "BITOP", "BITPOS", "GETBIT", "SETBIT",
                        // Geo 地理操作
                        "GEOADD", "GEODIST", "GEOHASH", "GEOPOS", "GEORADIUS", "GEORADIUSBYMEMBER", "GEOSEARCH",
                        "GEOSEARCHSTORE",
                        // Stream 流操作
                        "XACK", "XADD", "XAUTOCLAIM", "XCLAIM", "XDEL", "XGROUP", "XINFO", "XLEN", "XPENDING",
                        "XRANGE", "XREAD", "XREADGROUP", "XREVRANGE", "XTRIM",
                        // 键操作
                        "COPY", "DEL", "EXISTS", "EXPIRE", "EXPIREAT", "KEYS", "MOVE", "OBJECT", "PERSIST",
                        "PEXPIRE", "PEXPIREAT", "PTTL", "RANDOMKEY", "RENAME", "RENAMENX", "RESTORE",
                        "SORT", "TOUCH", "TTL", "TYPE", "UNLINK", "WAIT", "WATCH", "UNWATCH",
                        // 数据库操作
                        "DBSIZE", "FLUSHALL", "FLUSHDB", "LASTSAVE", "SAVE",
                        // 脚本与事务
                        "EVAL", "EVALSHA", "EVALSHA_RO", "EVAL_RO", "FCALL", "FCALL_RO", "SCRIPT",
                        "DEBUG", "MULTI", "EXEC", "DISCARD",
                        // 集群操作
                        "CLUSTER", "READONLY", "READWRITE", "REPLICAOF", "SLAVEOF", "ROLE",
                        // 慢查询与监控
                        "SLOWLOG", "LATENCY", "MONITOR", "MEMORY", "INFO",
                        // 配置与服务器
                        "ACL", "ACL_CAT", "ACL_DELUSER", "ACL_DRYRUN", "ACL_GETUSER", "ACL_LIST",
                        "ACL_LOAD", "ACL_LOG", "ACL_SETUSER", "ACL_USERS", "ACL WHOAMI",
                        "BGREWRITEAOF", "BGSAVE", "COMMAND", "COMMAND COUNT", "COMMAND DOCS", "COMMAND GETKEYS",
                        "COMMAND GETKEYSANDFLAGS", "COMMAND INFO", "COMMAND LIST", "CONFIG", "FAILOVER",
                        "MODULE", "MODULE LIST", "MODULE LOAD", "MODULE UNLOAD", "PUBLISH", "PUBSUB",
                        "PSUBSCRIBE", "PUNSUBSCRIBE", "SHUTDOWN", "SUBSCRIBE", "UNSUBSCRIBE",
                        // 哨兵
                        "SENTINEL", "MASTERS", "SLAVES", "INFO", "GET", "RESET", "FAILOVER", "RESET",
                        "SET", "MONITOR", "REMOVE", "CKQUORUM", "DEBUG", "FLUSHCONFIG", "GETMASTER",
                        "HARD_RESET", "INFO", "IS_MASTER_DOWN_BY_ADDR", "MASTER", "MYID", "pending",
                        "POSTRUN", "PREREQUISITES", "REMOVE", "RESET", "SET", "SOFT_RESET", "TYPE"
                );

                KEYWORDS = Collections.unmodifiableSet(keywords);
        }

        private RedisKeywords()
        {
                /* DO NOTHING... */
        }
}