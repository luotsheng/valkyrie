package com.changhong.opendb.app.ui.navigator.node;

import com.changhong.opendb.app.VFXApplication;
import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.NewMutableDataGridPaneEvent;
import com.changhong.opendb.app.core.event.OpenDesignTablePaneEvent;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.resource.Assets;
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
        private final TableMetaData table;

        public ODBNTable(SQLExecutor sqlExecutor,
                         ODBNDatabase database,
                         TableMetaData table)
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
                        VFXApplication.copyToClipboard(getName());
                });

                MenuItem designTableItem = new MenuItem("设计表");
                designTableItem.setOnAction(event -> {
                        EventBus.publish(new OpenDesignTablePaneEvent(
                                sqlExecutor,
                                database.getConnection().getName(),
                                table));
                });

                contextMenu.getItems().addAll(
                        copyTableNameItem,
                        designTableItem
                );

                return contextMenu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                EventBus.publish(new NewMutableDataGridPaneEvent(sqlExecutor, database.getName(), table));
        }
}