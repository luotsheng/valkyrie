package com.changhong.opendb.app.driver;

/**
 * MySQL 表字段元数据结构，对应：
 *   SHOW FULL COLUMNS FROM table_name;
 *
 * @author Luo Tiansheng
 * @since 2026/4/01
 */
public class TableDescribe
{
        /**
         * 字段名称（列名）
         */
        private String column;

        /**
         * 完整字段类型
         * 示例：varchar(255)、int、bigint、datetime
         */
        private String type;

        /**
         * 是否允许 NULL
         */
        private boolean nullable;

        /**
         * 是否主键
         */
        private boolean primaryKey;

        /**
         * 是否唯一索引
         */
        private boolean uniqueKey;

        /**
         * 是否自增
         */
        private boolean autoIncrement;

        /**
         * 默认值
         */
        private String defaultValue;

        /**
         * 字段注释
         * 对应建表语句中的 COMMENT
         */
        private String comment;
}
