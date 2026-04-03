package com.changhong.opendb.driver.executor;

import com.changhong.opendb.ui.widgets.ErrorDialog;
import javafx.application.Platform;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
public class DefaultExecutorCallback
        implements SQLExecutor.ExecuteCallback
{
        @Override
        public void doCallback(String info, SQLExecutorStatus status)
        {
                if (status == SQLExecutorStatus.ERROR)
                        Platform.runLater(() -> ErrorDialog.showDialog(info));
        }
}
