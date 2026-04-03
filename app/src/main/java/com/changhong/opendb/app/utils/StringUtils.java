package com.changhong.opendb.app.utils;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public class StringUtils
{
        public static boolean strempty(String value)
        {
                return value == null || value.isEmpty();
        }

        public static String strfmt(String fmt, Object ...args)
        {
                return String.format(fmt, args);
        }
}
