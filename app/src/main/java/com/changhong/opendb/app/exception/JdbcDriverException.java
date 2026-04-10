package com.changhong.opendb.app.exception;

/**
 * 数据访问异常
 *
 * @author Luo Tiansheng
 * @since 2026/4/10
 */
public class JdbcDriverException extends RuntimeException
{
        public JdbcDriverException()
        {
        }

        public JdbcDriverException(String message)
        {
                super(message);
        }

        public JdbcDriverException(String message, Throwable cause)
        {
                super(message, cause);
        }

        public JdbcDriverException(Throwable cause)
        {
                super(cause);
        }

        public JdbcDriverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
                super(message, cause, enableSuppression, writableStackTrace);
        }
}
