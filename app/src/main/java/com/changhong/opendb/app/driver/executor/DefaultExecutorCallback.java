package com.changhong.opendb.app.driver.executor;

import com.changhong.opendb.app.ui.widgets.ErrorDialog;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
public class DefaultExecutorCallback
        implements SQLExecutor.ExecuteCallback
{
        private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorCallback.class);

        @Override
        public void doCallback(String info, SQLExecutorStatus status)
        {
                if (status == SQLExecutorStatus.ERROR)
                        LOG.error(info);
        }
}
