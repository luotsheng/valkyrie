package com.changhong.opendb.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件总线管理器
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class EventBus
{
        private static final Map<Class<? extends Event>, CopyOnWriteArrayList<EventListener>> eventListeners
                = new ConcurrentHashMap<>();

        /**
         * 订阅事件
         */
        public static void subscribe(Class<? extends Event> event, EventListener listener)
        {
                eventListeners.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>())
                        .add(listener);
        }

        /**
         * 发布事件
         */
        public static void publish(Event event)
        {
                eventListeners.get(event.getClass()).forEach(listener -> listener.onEvent(event));
        }

        /**
         * 发布异常事件
         */
        public static void publish(Throwable e)
        {
                publish(new ExceptionEvent(e));
        }

}
