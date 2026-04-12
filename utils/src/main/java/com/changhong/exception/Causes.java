package com.changhong.exception;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
public class Causes
{
        public static Throwable original(Throwable e)
        {
                while (e.getCause() != null)
                        e = e.getCause();
                return e;
        }

        public static String message(Throwable e)
        {
                return original(e).getMessage();
        }
}
