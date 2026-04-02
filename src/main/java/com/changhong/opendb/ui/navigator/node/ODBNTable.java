package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.app.Application;
import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.NewQueryResultSetPaneEvent;
import com.changhong.opendb.driver.executor.SQLExecutor;
import com.changhong.opendb.driver.TableMetadata;
import com.changhong.opendb.resource.Assets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNTable extends ODBNode
{
        private final SQLExecutor sqlExecutor;
        private final ODBNDatabase database;
        private final TableMetadata table;

        public ODBNTable(SQLExecutor sqlExecutor,
                         ODBNDatabase database,
                         TableMetadata table)
        {
                super(table.getName());
                this.database = database;
                setGraphic(Assets.use("table"));
                this.sqlExecutor = sqlExecutor;
                this.table = table;
        }

        @Override
        public void onSelectedEvent()
        {
                // database.onSelected();
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem copyTableNameItem = new MenuItem("复制表名");
                copyTableNameItem.setOnAction(event -> {
                        Application.copyToClipboard(getName());
                });

                contextMenu.getItems().addAll(copyTableNameItem);

                return contextMenu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                EventBus.publish(new NewQueryResultSetPaneEvent(sqlExecutor, database.getName(), table));
        }
}