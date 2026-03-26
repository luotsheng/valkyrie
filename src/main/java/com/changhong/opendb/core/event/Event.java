package com.changhong.opendb.core.event;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public abstract class Event
{
        /**
         * 表示哪个对象触发的当前事件
         */
        public final Object object;

        public Event(Object object)
        {
                this.object = object;
        }
}
