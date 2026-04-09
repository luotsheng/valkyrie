package com.changhong.opendb.app.ui.navigator.node;

import com.changhong.opendb.app.core.event.*;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.model.QueryInfo;
import com.changhong.opendb.app.repository.QueryScriptRepository;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.pane.DatabaseDetailPane;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialogHelper;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.sql.SQLException;
import java.util.ArrayList;
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
        @Getter
        private final ODBNConnection connection;
        @Getter
        private final SQLExecutor sqlExecutor;
        private boolean openFlag = false;
        private final List<TableMetaData> tables = new ArrayList<>();

        // Tree Items
        final TreeItem<String> tableItem
                = new ODBInternalNode(this, "数据表", Assets.use("table"));;
        final TreeItem<String> queryItem
                = new ODBInternalNode(this, "查询脚本", Assets.use("sql"));;

        // Menu Items
        private MenuItem openOrCloseMenuItem;
        private MenuItem newQueryMenuItem;

        private final DatabaseDetailPane detailPane = new DatabaseDetailPane(this);
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
                            SQLExecutor sqlExecutor,
                            String name)
        {
                super(name);
                this.connection = connection;
                setGraphic(Assets.use("database1"));
                this.sqlExecutor = sqlExecutor;

                setupTableNode();
                setupListenerEvent();

                EventBus.subscribe(RefreshTableNodeEvent.class, this);
                EventBus.subscribe(RefreshQueryNodeEvent.class, this);
        }

        private void setupTableNode()
        {
                var node = (ODBNode) tableItem;

                ContextMenu nodeContextMenu = new ContextMenu();

                MenuItem refreshTableItem = new MenuItem("刷新");
                refreshTableItem.setOnAction(event -> refreshTableNode());

                nodeContextMenu.getItems().addAll(refreshTableItem);
                node.setContextMenu(nodeContextMenu);
        }

        private void reloadTableMetadata()
        {
                tables.clear();
                tables.addAll(sqlExecutor.getTables(name));
        }

        public final void dropTable(TableMetaData tbMetaData) throws SQLException
        {
                sqlExecutor.dropTable(name, tbMetaData.getName());
        }

        @SuppressWarnings("unchecked")
        public void openDatabase()
        {
                if (openFlag)
                        return;

                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                reloadTableMetadata();

                                Platform.runLater(() -> {
                                        getChildren().addAll(tableItem, queryItem);

                                        refreshTableNode();
                                        refreshQueryNode();

                                        setExpanded(true);
                                        onSelectedEvent();

                                        openFlag = true;
                                });
                        } catch (Throwable e) {
                                VFXDialogHelper.warn(e);
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

        public void refreshTableNode()
        {
                reloadTableMetadata();

                tableItem.getChildren().clear();

                for (TableMetaData table : tables) {
                        ODBNTable tableNode = new ODBNTable(sqlExecutor, this, table);
                        tableNode.setSelectedEvent(this::onSelected);
                        tableItem.getChildren().add(tableNode);
                }

                detailPane.update(tables);
        }

        private void refreshQueryNode()
        {
                queryItem.getChildren().clear();
                List<QueryInfo> queryInfos = QueryScriptRepository.loadQueryInfo(connection, this);
                queryInfos.forEach(query -> queryItem.getChildren().add(new ODBNQuery(this, query)));
        }

        public void onSelected()
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

        public boolean isOpen()
        {
                return openFlag;
        }
}
