package com.changhong.opendb.utils;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
public class Causes
{
        public static String message(Throwable e)
        {
                if (e.getCause() != null)
                        return e.getCause().getMessage();

                return e.getMessage();
        }
}
