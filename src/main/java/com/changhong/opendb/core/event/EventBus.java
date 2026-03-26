package com.changhong.opendb.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件总线管理器
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class EventBus
{
        private static final Map<Class<? extends Event>, List<EventListener>> eventListeners
                = new ConcurrentHashMap<>();

        /**
         * 订阅事件
         */
        public static void subscribe(Class<? extends Event> event, EventListener listener)
        {
                eventListeners.computeIfAbsent(event, k -> new ArrayList<>()).add(listener);
        }

        /**
         * 发布事件
         */
        public static void publish(Event event)
        {
                eventListeners.get(event.getClass()).forEach(listener -> listener.onEvent(event));
        }
}
