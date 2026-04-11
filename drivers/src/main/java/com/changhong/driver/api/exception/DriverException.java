package com.changhong.driver.api.exception;

/**
 * 驱动运行时异常
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public class DriverException extends RuntimeException
{
        public DriverException()
        {
        }

        public DriverException(String message)
        {
                super(message);
        }

        public DriverException(String message, Throwable cause)
        {
                super(message, cause);
        }

        public DriverException(Throwable cause)
        {
                super(cause);
        }

        public DriverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
                super(message, cause, enableSuppression, writableStackTrace);
        }
}
