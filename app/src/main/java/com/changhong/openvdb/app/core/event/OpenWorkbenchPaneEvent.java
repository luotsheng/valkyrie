package com.changhong.openvdb.app.core.event;

import com.changhong.openvdb.app.ui.pane.BrowserPane;
import lombok.Getter;

/**
 * 在工作区打开面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
@Getter
public class OpenWorkbenchPaneEvent extends Event
{
        private final BrowserPane pane;

        public OpenWorkbenchPaneEvent(BrowserPane pane)
        {
                this.pane = pane;
        }
}
