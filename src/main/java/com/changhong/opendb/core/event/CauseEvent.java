package com.changhong.opendb.core.event;

/**
 * 异常捕获事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class CauseEvent extends Event
{
        public final String message;

        public CauseEvent(Throwable e)
        {
                Throwable cause = e.getCause();
                this.message = cause != null
                        ? cause.getMessage()
                        : e.getMessage();
        }
}
