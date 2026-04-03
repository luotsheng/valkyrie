package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.ui.pane.DetailPane;
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
        private DetailPane pane;

        public CloseWorkbenchPaneEvent(DetailPane pane)
        {
                this.pane = pane;
        }
}
