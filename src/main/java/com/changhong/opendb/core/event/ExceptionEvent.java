package com.changhong.opendb.core.event;

/**
 * 异常捕获事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class ExceptionEvent extends Event
{
        public final String message;

        public ExceptionEvent(String message)
        {
                super(null);
                this.message = message;
        }
}
