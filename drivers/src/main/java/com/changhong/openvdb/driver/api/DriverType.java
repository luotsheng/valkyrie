package com.changhong.openvdb.driver.api;

import static com.changhong.string.StringStaticize.uppercase;

/**
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public enum DriverType
{
        MYSQL,
        ;

        public static DriverType toDriverType(String type)
        {
                return valueOf(uppercase(type));
        }
}
