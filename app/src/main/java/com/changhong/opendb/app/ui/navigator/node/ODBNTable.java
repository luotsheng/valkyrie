package com.changhong.opendb.app.ui.navigator.node;

import com.changhong.driver.api.Driver;
import com.changhong.driver.api.Table;
import com.changhong.opendb.app.VFXApplication;
import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.OpenDataGridPaneEvent;
import com.changhong.opendb.app.core.event.OpenDesignTablePaneEvent;
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
        private final Driver driver;
        private final ODBNDatabase database;
        private final Table table;

        public ODBNTable(Driver driver,
                         ODBNDatabase database,
                         Table table)
        {
                super(table.getName());
                setGraphic(Assets.use("table"));

                this.database = database;
                this.driver = driver;
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