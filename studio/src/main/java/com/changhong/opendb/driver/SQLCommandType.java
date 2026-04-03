package com.changhong.opendb.driver;

/**
 * SQL 脚本命令类型
 *
 * @author Luo Tiansheng
 * @since 2026/4/02
 */
@SuppressWarnings({
        "ConvertToBasicLatin",
        "JavadocBlankLines",
})
public enum SQLCommandType
{
        /**
         * DDL（Data Definition Language）
         * 数据定义语言，用于定义或修改数据库结构对象。
         *
         * 常见语句：
         * CREATE、ALTER、DROP、TRUNCATE、RENAME
         */
        DDL,

        /**
         * DML（Data Manipulation Language）
         * 数据操作语言，用于对表中的数据进行增删改。
         *
         * 常见语句：
         * INSERT、UPDATE、DELETE、MERGE、UPSERT
         */
        DML,

        /**
         * DCL（Data Control Language）
         * 数据控制语言，用于权限管理。
         *
         * 常见语句：
         * GRANT、REVOKE
         */
        DCL,

        /**
         * TCL（Transaction Control Language）
         * 事务控制语言，用于管理数据库事务。
         *
         * 常见语句：
         * COMMIT、ROLLBACK、SAVEPOINT
         */
        TCL,

        /**
         * QUERY
         * 查询语句，用于从数据库中读取数据。
         *
         * 常见语句：
         * SELECT、SHOW、EXPLAIN、DESCRIBE
         */
        DQL,

        /**
         * ADMIN
         * 数据库管理或会话控制语句。
         *
         * 常见语句：
         * SET、USE
         */
        EXECUTE,

        /**
         * UNSUPPORTED
         * 不支持的命令
         */
        UNSUPPORTED,
}
