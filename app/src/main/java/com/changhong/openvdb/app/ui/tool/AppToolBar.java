package com.changhong.openvdb.app.ui.tool;

import com.changhong.openvdb.app.core.event.EventBus;
import com.changhong.openvdb.app.core.event.OpenQueryScriptEvent;
import com.changhong.openvdb.app.model.VDBNodeStatus;
import com.changhong.openvdb.app.ui.navigator.node.VDBConnectionNode;
import com.changhong.openvdb.app.ui.widgets.VFXIconButton;
import com.changhong.openvdb.app.ui.widgets.VFXSeparator;
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
                newQueryButton.setOnAction(event -> newScriptEditor());

                getItems().addAll(
                        newConnectionButton,
                        newQueryButton,
                        new VFXSeparator()
                );
        }

        private void newScriptEditor()
        {
                VDBNodeStatus instance = VDBNodeStatus.getInstance();
                VDBConnectionNode selectedConnection = instance.getSelectedConnection();
                EventBus.publish(new OpenQueryScriptEvent(selectedConnection));
        }
}
