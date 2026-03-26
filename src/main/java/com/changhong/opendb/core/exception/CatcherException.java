package com.changhong.opendb.core.exception;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class CatcherException extends RuntimeException
{
        public CatcherException()
        {
        }

        public CatcherException(String message)
        {
                super(message);
        }

        public CatcherException(String message, Throwable cause)
        {
                super(message, cause);
        }

        public CatcherException(Throwable cause)
        {
                super(cause);
        }

        public CatcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
                super(message, cause, enableSuppression, writableStackTrace);
        }
}
