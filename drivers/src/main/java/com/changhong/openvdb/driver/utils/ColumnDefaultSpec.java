package com.changhong.openvdb.driver.utils;

import lombok.Data;

/**
 * 字段默认值
 *
 * @author Luo Tiansheng
 * @since 2026/4/7
 */
@Data
public class ColumnDefaultSpec
{
        /**
         * 字段名称
         */
        private String name;
        /**
         * 默认值
         */
        private String defaultValue;
}
