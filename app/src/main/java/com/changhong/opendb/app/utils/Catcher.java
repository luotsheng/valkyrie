package com.changhong.opendb.app.utils;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.exception.CatcherException;
import javafx.application.Platform;

import static com.changhong.string.StringUtils.strwfmt;

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

        public static void ithrow(String msg, Object... args)
        {
                ithrow(new CatcherException(strwfmt(msg, args)));
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

                CatcherException copy = e;
                Platform.runLater(() -> EventBus.publish(copy));

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
