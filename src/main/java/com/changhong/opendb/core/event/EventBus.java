package com.changhong.opendb.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

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
                CopyOnWriteArrayList<EventListener> copyOnWriteEventListeners = eventListeners.get(event.getClass());

                copyOnWriteEventListeners.forEach(listener -> {
                        try {
                                if (event.isConsume())
                                        return;
                                listener.onEvent(event);
                        } catch (Throwable e) {
                                LOG.error("Consume event failed", e);
                        }
                });
        }

        /**
         * 发布异常事件
         */
        public static void publish(Throwable e)
        {
                publish(new CauseEvent(e));
        }

}
