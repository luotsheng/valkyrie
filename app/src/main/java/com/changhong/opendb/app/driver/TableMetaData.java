package com.changhong.opendb.app.driver;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 表结构
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@Data
@ToString
public class TableMetaData
{
        /**
         * 数据库名字
         */
        private String name;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 修改时间
         */
        private Date updateTime;

        /**
         * 存储引擎
         */
        private String engine;

        /**
         * 数据库大小（KB）
         */
        private Float size;

        /**
         * 数据行数
         */
        private Integer rows;

        /**
         * 表注释
         */
        private String comment;

        /**
         * 所属数据库
         */
        private String database;
}
