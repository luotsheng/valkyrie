package com.changhong.opendb.core.event;

import com.changhong.opendb.ui.pane.DetailPane;
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
