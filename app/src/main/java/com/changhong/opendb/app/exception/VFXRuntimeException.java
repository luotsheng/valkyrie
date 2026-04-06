package com.changhong.opendb.app.exception;

/**
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
public class VFXRuntimeException extends RuntimeException
{
        public VFXRuntimeException()
        {
        }

        public VFXRuntimeException(String message)
        {
                super(message);
        }

        public VFXRuntimeException(String message, Throwable cause)
        {
                super(message, cause);
        }

        public VFXRuntimeException(Throwable cause)
        {
                super(cause);
        }

        public VFXRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
                super(message, cause, enableSuppression, writableStackTrace);
        }
}
