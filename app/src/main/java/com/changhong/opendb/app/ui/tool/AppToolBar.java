package com.changhong.opendb.app.ui.tool;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.NewQueryScriptEvent;
import com.changhong.opendb.app.model.ODBNStatus;
import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.ui.widgets.VFX;
import com.changhong.opendb.app.ui.widgets.VSeparator;
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
                Button newConnectionButton = VFX.newIconButton("连接", "chain");
                newConnectionButton.setText("新建连接");

                Button newQueryButton = VFX.newIconButton("查询", "sql");
                newQueryButton.setText("新建查询");
                newQueryButton.setOnAction(event -> newQuery());

                getItems().addAll(
                        newConnectionButton,
                        newQueryButton,
                        new VSeparator()
                );
        }

        private void newQuery()
        {
                ODBNStatus instance = ODBNStatus.getInstance();
                ODBNConnection selectedConnection = instance.getSelectedConnection();
                EventBus.publish(new NewQueryScriptEvent(selectedConnection == null ? null : selectedConnection.getInfo()));
        }
}
