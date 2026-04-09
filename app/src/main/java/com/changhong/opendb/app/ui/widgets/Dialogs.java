package com.changhong.opendb.app.ui.widgets;

import com.changhong.exception.SystemRuntimeException;
import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.exception.CatcherException;
import javafx.application.Platform;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class Dialogs
{
        public interface DialogCallback {
                void apply() throws Throwable;
        }

        public static void tryCall(DialogCallback callback)
        {
                try {
                        callback.apply();
                } catch (Throwable e) {
                        openError(e);
                }
        }

        public static void openError(String message)
        {
                openError(new SystemRuntimeException(message));
        }

        /**
         * 弹出异常提示框
         */
        public static void openError(Throwable throwable)
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

}
