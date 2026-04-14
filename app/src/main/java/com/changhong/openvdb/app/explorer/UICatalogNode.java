package com.changhong.openvdb.app.explorer;

import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.event.RefreshQueryNodeEvent;
import com.changhong.openvdb.app.event.RefreshTableNodeEvent;
import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.bus.EventListener;
import com.changhong.openvdb.app.event.workbench.OpenNavigationPaneEvent;
import com.changhong.openvdb.app.event.workbench.OpenScriptEditorPaneEvent;
import com.changhong.openvdb.app.pane.TableOverviewPane;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
import com.changhong.openvdb.core.model.ScriptFile;
import com.changhong.openvdb.core.repository.ScriptFileRepository;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;
import com.changhong.utils.collection.Maps;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.sql.SQLException;
import java.text.Collator;
import java.util.*;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({
        "FieldCanBeLocal"
})
public class UICatalogNode extends UIExplorerNode implements EventListener
{
        @Getter
        private final UIConnectionNode connection;
        @Getter
        private final Driver driver;
        private boolean openFlag = false;
        private final List<Table> tables = new ArrayList<>();
        private final Map<String, UITableNode> tableNodes = Maps.newHashMap();
        @Getter
        private final Session session;

        // Tree Items
        final TreeItem<String> tableItem
                = new UIInternalNode(this, "数据表", Assets.use("table"));;
        final TreeItem<String> queryItem
                = new UIInternalNode(this, "查询脚本", Assets.use("sql"));;

        // Menu Items
        private MenuItem openOrCloseMenuItem;
        private MenuItem newQueryMenuItem;

        private final TableOverviewPane overviewPane = new TableOverviewPane(this);
        private final OpenNavigationPaneEvent openWorkbenchPaneEvent = new OpenNavigationPaneEvent(overviewPane);

        /**
         * 内部通用节点
         */
        public static class UIInternalNode extends UIExplorerNode
        {
                private final UICatalogNode parent;

                public UIInternalNode(UICatalogNode parent, String name, ImageView icon)
                {
                        super(name);
                        setGraphic(icon);
                        this.parent = parent;
                }

                @Override
                public void onSelectedEvent(UIExplorerNode node)
                {
                        parent.onSelectedEvent(node);
                }
        }

        public UICatalogNode(UIConnectionNode connection,
                             Driver driver,
                             String databaseName)
        {
                super(databaseName);
                this.connection = connection;
                setGraphic(Assets.use("database1"));

                this.session = switch (connection.getDriverType()) {
                        case MYSQL -> Session.ofCatalog(databaseName);
                        case DM -> Session.ofSchema(databaseName);
                };

                this.driver = driver;

                setupTableNode();
                setupListenerEvent();

                EventBus.subscribe(RefreshTableNodeEvent.class, this);
                EventBus.subscribe(RefreshQueryNodeEvent.class, this);
        }

        private void setupTableNode()
        {
                var node = (UIExplorerNode) tableItem;

                ContextMenu nodeContextMenu = new ContextMenu();

                MenuItem reloadTableItem = new MenuItem("刷新");
                reloadTableItem.setOnAction(event -> reloadTableNode());

                nodeContextMenu.getItems().addAll(reloadTableItem);
                node.setContextMenu(nodeContextMenu);
        }

        private void reloadTable()
        {
                tables.clear();
                tables.addAll(driver.getTables(session));
                tables.sort(Comparator.comparing(Table::getName, Collator.getInstance(Locale.CHINA)));
        }

        public final void dropTable(Table table) throws SQLException
        {
                driver.dropTable(session, table.getName());
        }

        @SuppressWarnings("unchecked")
        public void openDatabase()
        {
                if (openFlag)
                        return;

                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                reloadTable();

                                Platform.runLater(() -> {
                                        getChildren().addAll(tableItem, queryItem);

                                        reloadTableNode();
                                        reloadQueryNode();

                                        setExpanded(true);
                                        onSelectedEvent(this);

                                        openFlag = true;
                                });
                        } catch (Throwable e) {
                                VFXDialogHelper.alert(e);
                        } finally {
                                Platform.runLater(this::removeLoadingIndicator);
                        }
                }).start();
        }

        public void closeDatabase()
        {
                if (!openFlag)
                        return;

                setExpanded(false);

                getChildren().clear();

                openFlag = false;
        }

        public void reloadTableNode()
        {
                reloadTable();

                tableNodes.clear();
                tableItem.getChildren().clear();

                for (Table table : tables) {
                        UITableNode tableNode = new UITableNode(driver, this, table);
                        tableNode.setSelectedEvent(this::onSelected);
                        tableNodes.put(table.getName(), tableNode);
                        tableItem.getChildren().add(tableNode);
                }

                overviewPane.update(tables);
        }

        private void reloadQueryNode()
        {
                queryItem.getChildren().clear();
                List<ScriptFile> scriptFiles = ScriptFileRepository.loadScriptFiles(connection.getName(), getName(), null);
                scriptFiles.forEach(query -> queryItem.getChildren().add(new UIScriptNode(this, query)));
        }

        public void onSelected(UIExplorerNode node)
        {
                if (openFlag && node == tableItem)
                        EventBus.publish(openWorkbenchPaneEvent);
                connection.setSelectedDatabase(this);
        }

        private void newQueryScript()
        {
                EventBus.publish(new OpenScriptEditorPaneEvent(connection));
        }

        public void setupListenerEvent()
        {
                setSelectedEvent(this::onSelected);
                setMouseDoubleClickEvent(event -> openDatabase());
        }

        @Override
        public void onEvent(Event event)
        {
                if (event instanceof RefreshTableNodeEvent)
                        reloadTableNode();

                if (event instanceof RefreshQueryNodeEvent)
                        reloadQueryNode();
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu menu = new ContextMenu();

                openOrCloseMenuItem = new MenuItem("打开数据库");
                openOrCloseMenuItem.setOnAction(event -> openDatabase());

                newQueryMenuItem = new MenuItem("新建查询");
                newQueryMenuItem.setOnAction(event -> newQueryScript());

                menu.getItems().addAll(
                        openOrCloseMenuItem,
                        new SeparatorMenuItem(),
                        newQueryMenuItem
                );

                return menu;
        }

        @Override
        public void showContextMenu(Node node, double x, double y)
        {
                if (openFlag) {
                        openOrCloseMenuItem.setText("关闭数据库");
                        openOrCloseMenuItem.setOnAction(event -> closeDatabase());
                } else {
                        openOrCloseMenuItem.setText("打开数据库");
                        openOrCloseMenuItem.setOnAction(event -> openDatabase());
                }

                super.showContextMenu(node, x, y);
        }

        public UITableNode getUITableNode(String name)
        {
                return tableNodes.get(name);
        }

        public boolean isOpen()
        {
                return openFlag;
        }
}
