package com.changhong.opendb.app.exception;

/**
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
public class VfxRuntimeException extends RuntimeException
{
        public VfxRuntimeException()
        {
        }

        public VfxRuntimeException(String message)
        {
                super(message);
        }

        public VfxRuntimeException(String message, Throwable cause)
        {
                super(message, cause);
        }

        public VfxRuntimeException(Throwable cause)
        {
                super(cause);
        }

        public VfxRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
                super(message, cause, enableSuppression, writableStackTrace);
        }
}
