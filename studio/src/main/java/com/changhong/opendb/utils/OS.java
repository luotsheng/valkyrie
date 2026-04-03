package com.changhong.opendb.utils;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OS
{
        public static boolean isMac()
        {
                return System.getProperty("os.name")
                        .toLowerCase()
                        .contains("mac");
        }

        public static boolean isWindows()
        {
                return System.getProperty("os.name")
                        .toLowerCase()
                        .contains("win");
        }

        public static boolean isLinux()
        {
                return System.getProperty("os.name")
                        .toLowerCase()
                        .contains("linux");
        }
}
