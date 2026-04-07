package com.changhong.opendb.app.driver;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

/**
 * 列元数据
 *
 * @author Luo Tiansheng
 * @since 2026/4/02
 */
@Data
public class ColumnMetaData
{
        /**
         * 列名（自定义 AS 语句）
         */
        private String label;

        /**
         * 真实列名
         */
        private String name;

        /**
         * 列序号（从 0 开始）
         */
        private int index;

        /**
         * 数据库字段类型
         */
        private String type;

        /**
         * JDBC 类型（java.sql.Types）
         */
        private int jdbcType;

        /**
         * 字段长度
         */
        private int length;

        /**
         * 小数位数
         */
        private int scale;

        /**
         * 是否允许 NULL
         */
        private boolean nullable;

        /**
         * 是否主键
         */
        private boolean primary;

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
         */
        private String comment;

        /**
         * 所属表名
         */
        private String table;

        /**
         * 所属 schema
         */
        private String schema;

        private BooleanProperty isPrimary;

        public BooleanProperty primaryProperty()
        {
                return isPrimary;
        }

}
