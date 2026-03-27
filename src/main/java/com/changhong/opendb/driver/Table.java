package com.changhong.opendb.driver;

import lombok.Data;

import java.util.Date;

/**
 * 表结构
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@Data
public class Table
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
        private Integer size;

        /**
         * 数据行数
         */
        private Integer rows;

        /**
         * 表注释
         */
        private String comment;
}
