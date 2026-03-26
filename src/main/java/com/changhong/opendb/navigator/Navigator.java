package com.changhong.opendb.navigator;

import com.changhong.opendb.repository.ConnectionRepository;
import com.changhong.opendb.core.event.Event;
import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.EventListener;
import com.changhong.opendb.core.event.RefreshConnectionEvent;
import com.changhong.opendb.ui.dialog.connection.ConnectionDialog;
import com.changhong.opendb.navigator.node.ODBNConnection;
import com.changhong.opendb.model.ConnectionModel;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class Navigator extends VBox implements EventListener
{
        private final TabPane tabPane;
        private final TextField searchField;
        private final TreeView<String> treeView;
        private final ContextMenu rootContextMenu;

        private final Map<String, ODBNConnection> connections
                = new HashMap<>();

        public Navigator()
        {
                this.tabPane = createTabPane();
                this.searchField = createSearchField();
                this.treeView = createTreeView();
                this.rootContextMenu = createRootContextMenu();

                EventBus.subscribe(RefreshConnectionEvent.class, this);

                setupContextMenu();
                initializeLayout();
                refreshODBNConnection();
        }

        @Override
        public void onEvent(Event event)
        {
                if (event instanceof RefreshConnectionEvent)
                        refreshODBNConnection();
        }

        private TabPane createTabPane()
        {
                TabPane tabPane = new TabPane();

                Tab navTab = new Tab("连接管理");
                navTab.setClosable(false);

                tabPane.getTabs().addAll(navTab);

                return tabPane;
        }

        private TextField createSearchField()
        {
                TextField searchField = new TextField();
                searchField.setPromptText("搜索...");
                return searchField;
        }

        private TreeView<String> createTreeView()
        {
                TreeItem<String> rootItem = new TreeItem<>("我的连接");

                TreeView<String> treeView = new TreeView<>(rootItem);
                treeView.setShowRoot(true);

                return treeView;
        }

        private ContextMenu createRootContextMenu()
        {
                ContextMenu rootContextMenu = new ContextMenu();

                Menu connectMenu = new Menu("新建连接");
                MenuItem mysqlItem =  new MenuItem("MySQL");
                MenuItem postgreSQLItem =  new MenuItem("PostgreSQL");
                connectMenu.getItems().addAll(mysqlItem, postgreSQLItem);

                MenuItem openAllItem =  new MenuItem("打开所有连接");
                MenuItem closeAllItem =  new MenuItem("关闭所有连接");
                MenuItem refreshAllItem =  new MenuItem("刷新连接");

                rootContextMenu.getItems().addAll(
                        connectMenu,
                        openAllItem,
                        closeAllItem,
                        refreshAllItem);

                /* 设置事件 */
                mysqlItem.setOnAction(event -> openMySQLConnectionDialog());
                refreshAllItem.setOnAction(event -> refreshODBNConnection());

                return rootContextMenu;
        }

        private void setupContextMenu()
        {
                treeView.setOnContextMenuRequested(event -> {

                        Node node = event.getPickResult().getIntersectedNode();

                        while (node != null && !(node instanceof TreeCell<?>))
                                node = node.getParent();

                        if (node instanceof TreeCell<?> cell) {
                                if (cell.getTreeItem() == treeView.getRoot()) {
                                        rootContextMenu.show(cell, event.getScreenX(), event.getScreenY());
                                }
                        }

                        event.consume();

                });
        }

        private void initializeLayout()
        {
                VBox vbox = new VBox(searchField, treeView);
                vbox.setSpacing(2);

                tabPane.getTabs().getFirst().setContent(vbox);
                VBox.setVgrow(treeView, Priority.ALWAYS);

                getChildren().add(tabPane);
                setVgrow(tabPane, Priority.ALWAYS);
        }

        private void refreshODBNConnection()
        {
                List<ODBNConnection> removeList = new ArrayList<>();
                List<ConnectionModel> models = ConnectionRepository.loadConnections();

                connections.forEach((k, v) -> {

                        boolean isMatch = models.stream()
                                .anyMatch(e -> e.getName().equals(k));

                        if (!isMatch)
                                removeList.add(v);

                });

                ObservableList<TreeItem<String>> children = treeView.getRoot().getChildren();

                removeList.forEach(children::remove);

                for (ConnectionModel model : models) {

                        if (connections.containsKey(model.getName()))
                                continue;

                        ODBNConnection connection = new ODBNConnection(model);

                        connections.put(model.getName(), connection);
                        children.add(connection);

                }
        }

        private void openMySQLConnectionDialog()
        {
                ConnectionDialog dialog = new ConnectionDialog();
                dialog.showAndWait();
        }
}
