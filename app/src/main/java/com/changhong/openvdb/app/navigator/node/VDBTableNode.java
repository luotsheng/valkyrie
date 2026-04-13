package com.changhong.openvdb.app.navigator.node;

import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Table;
import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.OpenDataGridPaneEvent;
import com.changhong.openvdb.app.event.OpenDesignTablePaneEvent;
import com.changhong.openvdb.app.resource.Assets;
import com.changhong.openvdb.app.navigator.VDBNode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class VDBTableNode extends VDBNode
{
        private final Driver driver;
        private final VDBDatabaseNode database;
        private final Table table;

        public VDBTableNode(Driver driver,
                            VDBDatabaseNode database,
                            Table table)
        {
                super(table.getName());
                setGraphic(Assets.use("table"));

                this.database = database;
                this.driver = driver;
                this.table = table;
        }

        @Override
        public void onSelectedEvent(VDBNode node)
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

                MenuItem designTableItem = new MenuItem("设计表");
                designTableItem.setOnAction(event -> {
                        EventBus.publish(new OpenDesignTablePaneEvent(
                                database.getConnection().getName(),
                                database.getSession(),
                                driver,
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
                EventBus.publish(new OpenDataGridPaneEvent(database.getSession(),
                        driver,
                        database.getName(),
                        table,
                        database.getConnection().getName()));
        }
}