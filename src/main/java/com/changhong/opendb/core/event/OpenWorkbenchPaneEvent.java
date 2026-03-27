package com.changhong.opendb.core.event;

import javafx.scene.layout.Pane;

/**
 * 在工作区打开面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenWorkbenchPaneEvent extends Event
{
        public final Pane pane;

        public OpenWorkbenchPaneEvent(Pane pane)
        {
                super(null);
                this.pane = pane;
        }
}
