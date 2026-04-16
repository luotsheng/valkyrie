package com.changhong.openvdb.app.explorer;

import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.workbench.OpenTableDataPaneEvent;
import com.changhong.openvdb.app.event.workbench.OpenTableDesignerPaneEvent;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Table;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class UITableNode extends UIExplorerNode
{
        private final Driver driver;
        private final UICatalogNode catalog;
        private final Table table;

        public UITableNode(Driver driver, UICatalogNode catalog, Table table)
        {
                super(table.getName());
                setGraphic(getIcon());

                this.catalog = catalog;
                this.driver = driver;
                this.table = table;
        }

        @Override
        public ImageView getIcon()
        {
                return Assets.use("table");
        }

        @Override
        public void onSelectedEvent(UIExplorerNode node)
        {
                // catalog.onSelected();
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem openTableItem = new MenuItem("打开表");
                openTableItem.setOnAction(event -> openDataGridBrowserPane());

                MenuItem designTableItem = new MenuItem("设计表");
                designTableItem.setOnAction(event -> openTableDesignerPane());

                MenuItem copyTableNameItem = new MenuItem("复制表名");
                copyTableNameItem.setOnAction(event -> {
                        Application.copyToClipboard(getName());
                });

                MenuItem copyCreateTableDLLItem = new MenuItem("复制建表语句");
                copyCreateTableDLLItem.setOnAction(event -> {
                        Application.copyToClipboard(driver.showCreateTable(catalog.getSession(), getName()));
                });

                MenuItem refreshTableItem = new MenuItem("刷新列表");
                refreshTableItem.setOnAction(event -> catalog.reloadTableNode());

                contextMenu.getItems().addAll(
                        openTableItem,
                        designTableItem,
                        new SeparatorMenuItem(),
                        copyTableNameItem,
                        copyCreateTableDLLItem,
                        new SeparatorMenuItem(),
                        refreshTableItem
                );

                return contextMenu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openDataGridBrowserPane();
        }

        public void openDataGridBrowserPane()
        {
                EventBus.publish(new OpenTableDataPaneEvent(catalog, table));
        }

        public void openTableDesignerPane()
        {
                EventBus.publish(new OpenTableDesignerPaneEvent(
                        catalog.getConnection().getName(),
                        catalog.getSession(),
                        driver,
                        table));
        }
}