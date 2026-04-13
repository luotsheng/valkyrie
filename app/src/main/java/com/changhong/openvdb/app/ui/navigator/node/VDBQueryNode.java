package com.changhong.openvdb.app.ui.navigator.node;

import com.changhong.openvdb.app.VFXApplication;
import com.changhong.openvdb.app.core.event.EventBus;
import com.changhong.openvdb.app.core.event.OpenQueryScriptEvent;
import com.changhong.openvdb.app.core.event.RefreshQueryNodeEvent;
import com.changhong.openvdb.app.core.event.RemoveScriptEditorTabEvent;
import com.changhong.openvdb.app.resource.Assets;
import com.changhong.openvdb.app.ui.dialog.RenameQueryScriptDialog;
import com.changhong.openvdb.app.ui.navigator.VDBNode;
import com.changhong.openvdb.app.ui.widgets.dialog.VFXDialogHelper;
import com.changhong.openvdb.core.model.ScriptFile;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;

import java.awt.*;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class VDBQueryNode extends VDBNode
{
        private final ScriptFile scriptFile;
        private final VDBDatabaseNode database;

        public VDBQueryNode(VDBDatabaseNode database, ScriptFile scriptFile)
        {
                super(scriptFile.getName());
                this.database = database;
                setGraphic(Assets.use("sql"));
                this.scriptFile = scriptFile;
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu menu = new ContextMenu();

                MenuItem openNewTabQueryItem = new MenuItem("打开查询脚本");
                openNewTabQueryItem.setOnAction(event -> openNewTabQuery());

                MenuItem renameQueryItem = new MenuItem("重命名查询");
                renameQueryItem.setOnAction(event -> renameQuery());

                MenuItem copyPathItem = new MenuItem("复制文件路径");
                copyPathItem.setOnAction(event -> copyFilePath());

                MenuItem openDesktopItem = new MenuItem("打开文件所在目录");
                openDesktopItem.setOnAction(event -> openDesktop());

                MenuItem deleteQueryItem = new MenuItem("删除查询");
                deleteQueryItem.setOnAction(event -> deleteQuery());

                menu.getItems().addAll(
                        openNewTabQueryItem,
                        new SeparatorMenuItem(),
                        renameQueryItem,
                        copyPathItem,
                        openDesktopItem,
                        new SeparatorMenuItem(),
                        deleteQueryItem
                );

                return menu;
        }

        private void openNewTabQuery()
        {
                EventBus.publish(new OpenQueryScriptEvent(scriptFile));
        }

        private void renameQuery()
        {
                RenameQueryScriptDialog.showDialog(scriptFile);
        }

        private void copyFilePath()
        {
                VFXApplication.copyToClipboard(scriptFile.getAbsolutePath());
        }

        private void openDesktop()
        {
                VFXDialogHelper.runWith(() -> {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.browseFileDirectory(scriptFile);
                });
        }

        private void deleteQuery()
        {
                scriptFile.delete();
                database.queryItem.getChildren().remove(this);
                EventBus.publish(new RemoveScriptEditorTabEvent(scriptFile));
                EventBus.publish(new RefreshQueryNodeEvent());
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openNewTabQuery();
        }
}