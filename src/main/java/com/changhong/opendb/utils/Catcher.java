package com.changhong.opendb.utils;

import com.changhong.opendb.ebus.EventBus;
import com.changhong.opendb.ebus.event.CatcherEvent;
import com.changhong.opendb.exception.CatcherException;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class Catcher
{

        public interface $CatcherRunnable
        {
                void run() throws Throwable;
        }

        public static void ithrow(String msg)
        {
                ithrow(new CatcherException(msg));
        }

        /**
         * 通过 ithrow 抛出运行时异常
         */
        public static void ithrow(Throwable throwable)
        {
                CatcherException e = null;

                if (throwable instanceof CatcherException catcherException) {
                        e = catcherException;
                } else {
                        e = new CatcherException(throwable);
                }

                EventBus.publish(new CatcherEvent(e.getCause().getMessage()));

                throw e;
        }

        /**
         * 将异常转为运行时异常抛出
         */
        public static void tryCall($CatcherRunnable runnable)
        {
                try {
                        runnable.run();
                } catch (Throwable e) {
                        ithrow(e);
                }
        }

}
