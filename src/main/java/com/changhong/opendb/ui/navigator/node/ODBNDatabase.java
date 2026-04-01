package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.core.event.*;
import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.TableInfo;
import com.changhong.opendb.driver.datasource.MySQLDataSourceProxy;
import com.changhong.opendb.model.QueryInfo;
import com.changhong.opendb.repository.QueryScriptRepository;
import com.changhong.opendb.resource.ResourceManager;
import com.changhong.opendb.ui.pane.DatabaseDetailPane;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({
        "FieldCanBeLocal"
})
public class ODBNDatabase extends ODBNode implements EventListener
{
        private final ODBNConnection connection;
        private final JdbcTemplate jdbcTemplate;
        private boolean openFlag = false;
        private List<TableInfo> tables;

        // Tree Items
        final TreeItem<String> tableItem
                = new ODBInternalNode(this, "数据表", ResourceManager.use("table"));;
        final TreeItem<String> queryItem
                = new ODBInternalNode(this, "查询脚本", ResourceManager.use("sql"));;

        // Menu Items
        private MenuItem openMenuItem;
        private MenuItem closeMenuItem;
        private MenuItem newQueryMenuItem;

        private final DatabaseDetailPane detailPane = new DatabaseDetailPane();
        private final OpenWorkbenchPaneEvent openWorkbenchPaneEvent = new OpenWorkbenchPaneEvent(detailPane);
        private final CloseWorkbenchPaneEvent closeWorkbenchPaneEvent = new CloseWorkbenchPaneEvent(detailPane);

        /**
         * 内部通用节点
         */
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

        public ODBNDatabase(ODBNConnection connection,
                            JdbcTemplate jdbcTemplate,
                            String name)
        {
                super(name);
                this.connection = connection;
                setGraphic(ResourceManager.use("database1"));
                this.jdbcTemplate = jdbcTemplate;
                setupListenerEvent();

                EventBus.subscribe(RefreshTableNodeEvent.class, this);
                EventBus.subscribe(RefreshQueryNodeEvent.class, this);
        }

        @SuppressWarnings("unchecked")
        public void openDatabase()
        {
                if (openFlag)
                        return;

                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                getChildren().addAll(tableItem, queryItem);

                                refreshTableNode();
                                refreshQueryNode();

                                setExpanded(true);
                                detailPane.update(jdbcTemplate, getName(), tables);
                                onSelectedEvent();

                                openFlag = true;

                                Platform.runLater(this::onSelected);
                        } catch (Throwable e) {
                                Platform.runLater(() -> EventBus.publish(e));
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
                EventBus.publish(closeWorkbenchPaneEvent);

                openFlag = false;
        }

        private void refreshTableNode()
        {
                tables = jdbcTemplate.getTables(name);
                tableItem.getChildren().clear();
                for (TableInfo table : tables) {
                        ODBNTable tableNode = new ODBNTable(jdbcTemplate, this, table);
                        tableNode.setSelectedEvent(this::onSelected);
                        tableItem.getChildren().add(tableNode);
                }
        }

        private void refreshQueryNode()
        {
                queryItem.getChildren().clear();
                List<QueryInfo> queryInfos = QueryScriptRepository.loadQueryInfo(connection, this);
                queryInfos.forEach(query -> queryItem.getChildren().add(new ODBNQuery(this, query)));
        }

        private void onSelected()
        {
                if (tables != null && openFlag)
                        EventBus.publish(openWorkbenchPaneEvent);
                connection.setSelectedDatabase(this);
        }

        private void newQueryScript()
        {
                EventBus.publish(new NewQueryScriptEvent(connection.getInfo()));
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
                        refreshTableNode();

                if (event instanceof RefreshQueryNodeEvent)
                        refreshQueryNode();
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

                menu.getItems().addAll(
                        openMenuItem,
                        closeMenuItem,
                        new SeparatorMenuItem(),
                        newQueryMenuItem
                );

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

        public boolean isOpen()
        {
                return openFlag;
        }
}
