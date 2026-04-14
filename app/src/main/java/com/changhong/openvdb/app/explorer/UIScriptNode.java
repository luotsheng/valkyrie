package com.changhong.openvdb.app.explorer;

import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.dialog.RenameScriptDialog;
import com.changhong.openvdb.app.event.RefreshQueryNodeEvent;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.workbench.OpenScriptEditorPaneEvent;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
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
public class UIScriptNode extends UIExplorerNode
{
        private final ScriptFile scriptFile;
        private final UICatalogNode catalog;

        public UIScriptNode(UICatalogNode catalog, ScriptFile scriptFile)
        {
                super(scriptFile.getName());
                this.catalog = catalog;
                setGraphic(Assets.use("sql"));
                this.scriptFile = scriptFile;
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu menu = new ContextMenu();

                MenuItem openNewTabQueryItem = new MenuItem("打开查询脚本");
                openNewTabQueryItem.setOnAction(event -> openScriptEditor());

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

        private void openScriptEditor()
        {
                EventBus.publish(new OpenScriptEditorPaneEvent(catalog.getConnection(), scriptFile));
        }

        private void renameQuery()
        {
                RenameScriptDialog.showDialog(scriptFile);
        }

        private void copyFilePath()
        {
                Application.copyToClipboard(scriptFile.getAbsolutePath());
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
                scriptFile.forceDelete();
                catalog.queryItem.getChildren().remove(this);
                EventBus.publish(new RefreshQueryNodeEvent());
        }

        @Override
        public void onSelectedEvent(UIExplorerNode node)
        {
                catalog.getConnection().setSelectedDatabase(catalog);
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openScriptEditor();
        }
}