package com.changhong.openvdb.driver.api;

import lombok.Getter;

import static com.changhong.utils.string.StaticLibrary.uppercase;

/**
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public enum DriverType
{
        MYSQL("MySQL"),
        DM("达梦数据库"),
        UNKNOWN("未知数据库"),
        ;

        @Getter
        private final String alias;

        DriverType(String alias)
        {
                this.alias = alias;
        }

        public static DriverType toDriverType(String type)
        {
                return valueOf(uppercase(type));
        }
}
