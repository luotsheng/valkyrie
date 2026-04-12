package com.changhong.opendb.app.ui.tool;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.OpenQueryScriptEvent;
import com.changhong.opendb.app.model.VDBNodeStatus;
import com.changhong.opendb.app.ui.navigator.node.VDNConnectionNode;
import com.changhong.opendb.app.ui.widgets.VFXIconButton;
import com.changhong.opendb.app.ui.widgets.VFXSeparator;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class AppToolBar extends ToolBar
{
        public AppToolBar()
        {
                Button newConnectionButton = new VFXIconButton("连接", "chain");
                newConnectionButton.setText("新建连接");

                Button newQueryButton = new VFXIconButton("查询", "sql");
                newQueryButton.setText("新建查询");
                newQueryButton.setOnAction(event -> newQuery());

                getItems().addAll(
                        newConnectionButton,
                        newQueryButton,
                        new VFXSeparator()
                );
        }

        private void newQuery()
        {
                VDBNodeStatus instance = VDBNodeStatus.getInstance();
                VDNConnectionNode selectedConnection = instance.getSelectedConnection();
                EventBus.publish(new OpenQueryScriptEvent(selectedConnection == null ? null : selectedConnection.getInfo()));
        }
}
