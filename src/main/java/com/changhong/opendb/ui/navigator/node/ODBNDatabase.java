package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.NewQueryScriptEvent;
import com.changhong.opendb.core.event.OpenWorkbenchPaneEvent;
import com.changhong.opendb.driver.Table;
import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.resource.ResourceManager;
import com.changhong.opendb.ui.pane.DatabaseDetailPane;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unchecked"
})
public class ODBNDatabase extends ODBNode
{
        private final DataSourceProvider dataSource;
        private boolean openFlag = false;
        private List<Table> tables;

        // Tree Items
        private TreeItem<String> tableItem;
        private TreeItem<String> queryItem;

        // Menu Items
        private MenuItem openMenuItem;
        private MenuItem closeMenuItem;
        private MenuItem newQueryMenuItem;

        private final DatabaseDetailPane detailPane = new DatabaseDetailPane();
        private final OpenWorkbenchPaneEvent openWorkbenchPaneEvent = new OpenWorkbenchPaneEvent(detailPane);

        public static class ODBInternalNode extends ODBNode
        {
                private final ODBNDatabase parent;

                public ODBInternalNode(ODBNDatabase parent, String name, ImageView icon)
                {
                        super(name);
                        setGraphic(icon);
                        this.parent = parent;
                }

                @Override
                public void onSelectedEvent()
                {
                        parent.onSelectedEvent();
                }
        }

        public ODBNDatabase(DataSourceProvider dataSource,
                            String name)
        {
                super(name);
                setGraphic(ResourceManager.use("database1"));
                this.dataSource = dataSource;
                setupListenerEvent();
        }

        private void openDatabase()
        {
                if (openFlag)
                        return;

                tableItem = new ODBInternalNode(this, "数据表", ResourceManager.use("table"));
                queryItem = new ODBInternalNode(this, "查询脚本", ResourceManager.use("sql"));
                getChildren().addAll(tableItem, queryItem);

                tables = dataSource.getTables(name);
                for (Table table : tables) {
                        ODBNTable tableNode = new ODBNTable(dataSource, table);
                        tableNode.setSelectedEvent(this::openWorkbenchPane);
                        tableItem.getChildren().add(tableNode);
                }

                setExpanded(true);
                detailPane.update(tables);
                onSelectedEvent();

                openFlag = true;
        }

        private void closeDatabase()
        {
                if (!openFlag)
                        return;

                getChildren().clear();

                openFlag = false;
        }

        private void openWorkbenchPane()
        {
                if (tables != null)
                        EventBus.publish(openWorkbenchPaneEvent);
        }

        private void newQueryScript()
        {
                EventBus.publish(new NewQueryScriptEvent());
        }

        public void setupListenerEvent()
        {
                setSelectedEvent(this::openWorkbenchPane);
                setMouseDoubleClickEvent(event -> openDatabase());
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu menu = new ContextMenu();

                openMenuItem = new MenuItem("打开数据库");
                openMenuItem.setOnAction(event -> openDatabase());

                closeMenuItem = new MenuItem("关闭数据库");
                closeMenuItem.setOnAction(event -> closeDatabase());

                newQueryMenuItem = new MenuItem("新建查询");
                newQueryMenuItem.setOnAction(event -> newQueryScript());

                menu.getItems().addAll(openMenuItem, closeMenuItem, newQueryMenuItem);

                return menu;
        }

        @Override
        public void showContextMenu(Node node, double x, double y)
        {
                if (openFlag) {
                        openMenuItem.setDisable(true);
                        closeMenuItem.setDisable(false);
                } else {
                        openMenuItem.setDisable(false);
                        closeMenuItem.setDisable(true);
                }

                super.showContextMenu(node, x, y);
        }
}
