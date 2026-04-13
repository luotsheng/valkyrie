package com.changhong.openvdb.app.core.event;

/**
 * 事件监听器，所有事件通过事件总线发送。
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public interface EventListener
{
        /**
         * 事件处理实现
         */
        void onEvent(Event event);
}
