package com.changhong.openvdb.app.core.event;

import com.changhong.openvdb.app.ui.pane.BrowserPane;
import lombok.Getter;

/**
 * 在工作区打开面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class CloseWorkbenchPaneEvent extends Event
{
        @Getter
        private BrowserPane pane;

        public CloseWorkbenchPaneEvent(BrowserPane pane)
        {
                this.pane = pane;
        }
}
