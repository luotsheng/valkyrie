package com.changhong.opendb.app.ui.navigator.node;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.NewQueryScriptEvent;
import com.changhong.opendb.app.core.event.RefreshQueryNodeEvent;
import com.changhong.opendb.app.core.event.RemoveSqlEditorTabEvent;
import com.changhong.opendb.app.model.QueryInfo;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.dialog.RenameQueryScriptDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNQuery extends ODBNode
{
        private final QueryInfo queryInfo;
        private final ODBNDatabase database;

        public ODBNQuery(ODBNDatabase database, QueryInfo queryInfo)
        {
                super(queryInfo.getName());
                this.database = database;
                setGraphic(Assets.use("sql"));
                this.queryInfo = queryInfo;
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu menu = new ContextMenu();

                MenuItem openNewTabQueryItem = new MenuItem("在新标签打开");
                openNewTabQueryItem.setOnAction(event -> openNewTabQuery());

                MenuItem renameQueryItem = new MenuItem("重命名");
                renameQueryItem.setOnAction(event -> renameQuery());
                
                MenuItem deleteQueryItem = new MenuItem("删除查询");
                deleteQueryItem.setOnAction(event -> deleteQuery());

                menu.getItems().addAll(
                        openNewTabQueryItem,
                        new SeparatorMenuItem(),
                        renameQueryItem,
                        deleteQueryItem
                );

                return menu;
        }

        private void renameQuery()
        {
                RenameQueryScriptDialog.showDialog(queryInfo);
        }

        private void openNewTabQuery()
        {
                EventBus.publish(new NewQueryScriptEvent(queryInfo));
        }

        private void deleteQuery()
        {
                queryInfo.delete();
                database.queryItem.getChildren().remove(this);
                EventBus.publish(new RemoveSqlEditorTabEvent(queryInfo.getSqlFile()));
                EventBus.publish(new RefreshQueryNodeEvent());
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openNewTabQuery();
        }
}