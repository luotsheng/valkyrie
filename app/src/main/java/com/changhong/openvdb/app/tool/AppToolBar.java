package com.changhong.openvdb.app.tool;

import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.workbench.OpenScriptEditorPaneEvent;
import com.changhong.openvdb.app.explorer.UIConnectionNode;
import com.changhong.openvdb.app.model.UINodeGlobalStatus;
import com.changhong.openvdb.app.widgets.VFXIconButton;
import com.changhong.openvdb.app.widgets.VFXSeparator;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
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
                newConnectionButton.setOnAction(event -> VFXDialogHelper.alert("暂不支持此按钮，请在我的连接右键新增对应连接！"));

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
                UINodeGlobalStatus instance = UINodeGlobalStatus.getInstance();
                UIConnectionNode selectedConnection = instance.getSelectedConnection();
                EventBus.publish(new OpenScriptEditorPaneEvent(selectedConnection));
        }
}
