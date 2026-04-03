package com.changhong.opendb.app.ui.workbench;

/**
 * @author Luo Tiansheng
 * @since 2026/3/31
 */
public class SqlKeyWordDefine
{
        public static final String[] KEYWORDS = {
                // DML（数据操作语言）
                "select", "from", "where", "insert", "update", "delete", "merge", "replace",
                // DDL（数据定义语言）
                "create", "alter", "drop", "truncate", "rename", "comment", "add", "modify", "column",
                "table", "index", "view", "sequence", "schema", "database", "tablespace", "function",
                "procedure", "trigger", "package", "synonym", "materialized", "partition", "temporary",
                // DCL（数据控制语言）
                "grant", "revoke", "deny", "privileges", "role", "user", "password",
                // TCL（事务控制语言）
                "commit", "rollback", "savepoint", "transaction", "begin", "end", "start", "set", "autocommit",
                // 查询子句与条件
                "where", "group", "order", "by", "having", "limit", "offset", "top", "distinct", "all",
                "asc", "desc", "nulls", "first", "last", "for", "option", "with", "without", "filter",
                // 连接与集合操作
                "join", "inner", "left", "right", "full", "outer", "cross", "natural", "on", "using",
                "union", "intersect", "except", "minus", "exists", "in", "between", "like", "ilike", "similar",
                // 条件表达式
                "case", "when", "then", "else", "end", "if", "nullif", "coalesce", "greatest", "least",
                // 谓词与运算符
                "and", "or", "not", "true", "false", "unknown", "is", "null", "any", "some", "all",
                // 聚合函数
                "count", "sum", "avg", "min", "max", "group_concat", "array_agg", "string_agg", "listagg",
                // 日期/时间函数
                "now", "current_date", "current_time", "current_timestamp", "date", "time", "timestamp",
                "curdate", "curtime", "sysdate", "localtime", "localtimestamp", "datediff", "dateadd",
                "extract", "year", "month", "day", "hour", "minute", "second", "date_sub",
                // 字符串函数
                "char", "varchar", "text", "concat", "substr", "substring", "length", "trim", "ltrim", "rtrim",
                "upper", "lower", "initcap", "replace", "instr", "position", "left", "right", "repeat", "reverse",
                // 数学函数
                "abs", "ceil", "ceiling", "floor", "round", "trunc", "mod", "power", "sqrt", "exp", "log",
                "sign", "random", "rand",
                // 类型转换与格式化
                "cast", "convert", "to_char", "to_number", "to_date", "to_timestamp", "format",
                // 窗口函数/分析函数
                "row_number", "rank", "dense_rank", "percent_rank", "cume_dist", "lead", "lag", "first_value",
                "last_value", "nth_value", "ntile", "over", "partition", "rows", "range", "unbounded",
                "preceding", "following", "current", "row",
                // 约束与表属性
                "primary", "key", "foreign", "references", "unique", "check", "default", "auto_increment",
                "identity", "generated", "stored", "virtual", "on", "cascade", "restrict", "no", "action",
                // 数据库管理
                "analyze", "explain", "use", "show", "desc", "describe", "kill", "optimize", "backup",
                "restore", "checkpoint", "load", "unload", "declare", "execute", "prepare", "open", "fetch",
                "close", "cursor", "loop", "while", "repeat", "return", "call", "raise",
                // 额外常用关键字
                "as", "into", "values", "set", "from", "to", "by", "using", "with", "without", "off",
                "only", "type", "like", "escape", "dual", "dummy", "connect", "level", "prior", "sleep",
                "start", "stop", "skip", "first", "last", "sample", "seed", "parallel", "nologging",
                "flashback", "purge", "recyclebin", "edition", "visible", "invisible", "compress", "nocompress"
        };
}
