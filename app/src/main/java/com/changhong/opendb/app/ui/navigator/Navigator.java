package com.changhong.opendb.app.ui.navigator;

import com.changhong.opendb.app.model.ODBNStatus;
import com.changhong.opendb.app.ui.menu.ConnectionMenuBuilder;
import com.changhong.opendb.app.ui.navigator.node.ODBNode;
import com.changhong.opendb.app.repository.ConnectionRepository;
import com.changhong.opendb.app.core.event.Event;
import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.EventListener;
import com.changhong.opendb.app.core.event.RefreshConnectionEvent;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.model.ConnectionInfo;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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
                setupMouseClickListener();
                setupTreeNodeSelectedEvent();
                initializeLayout();
                refreshODBNConnection();

                treeView.getRoot().setExpanded(true);
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
                ImageView chain = Assets.use("chain");
                TreeItem<String> rootItem = new TreeItem<>("我的连接", chain);

                TreeView<String> treeView = new TreeView<>(rootItem);
                treeView.setShowRoot(true);

                return treeView;
        }

        private ContextMenu createRootContextMenu()
        {
                ContextMenu rootContextMenu = new ContextMenu();

                Menu newConnectionMenu = ConnectionMenuBuilder.buildNewConnectionMenu();
                MenuItem openAllItem =  new MenuItem("打开所有连接");
                MenuItem closeAllItem =  new MenuItem("关闭所有连接");
                MenuItem refreshAllItem =  new MenuItem("刷新连接");

                rootContextMenu.getItems().addAll(
                        newConnectionMenu,
                        new SeparatorMenuItem(),
                        openAllItem,
                        closeAllItem,
                        refreshAllItem);

                /* 设置事件 */
                refreshAllItem.setOnAction(event -> refreshODBNConnection());

                return rootContextMenu;
        }

        private void setupContextMenu()
        {
                treeView.setOnContextMenuRequested(event -> {
                        Node node = event.getPickResult().getIntersectedNode();

                        double x = event.getScreenX();
                        double y = event.getScreenY();

                        while (node != null && !(node instanceof TreeCell<?>))
                                node = node.getParent();

                        if (node instanceof TreeCell<?> cell) {
                                TreeItem<?> item = cell.getTreeItem();

                                if (item == null)
                                        return;

                                if (item == treeView.getRoot()) {
                                        rootContextMenu.show(cell, x, y);
                                        return;
                                }

                                if (item instanceof ODBNode odbNode) {
                                        odbNode.showContextMenu(cell, x, y);
                                        return;
                                }
                        }

                        event.consume();
                });
        }

        private void setupTreeNodeSelectedEvent()
        {
                treeView.getSelectionModel().selectedIndexProperty()
                        .addListener((observable, oldVal, newVal) -> {
                                TreeItem<String> treeItem = treeView.getTreeItem(newVal.intValue());

                                if (!(treeItem instanceof ODBNode odbNode))
                                        return;

                                odbNode.onSelectedEvent();
                        });
        }

        private void setupMouseClickListener()
        {
                treeView.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                                Node node = event.getPickResult().getIntersectedNode();

                                while (node != null && !(node instanceof TreeCell<?>))
                                        node = node.getParent();

                                if (node instanceof TreeCell<?> cell) {
                                        TreeItem<?> item = cell.getTreeItem();

                                        if (!(item instanceof ODBNode odbNode))
                                                return;

                                        odbNode.onMouseDoubleClickEvent(event);
                                }

                                event.consume();
                        }
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
                List<ConnectionInfo> models = ConnectionRepository.loadConnections();

                connections.forEach((k, v) -> {
                        boolean isMatch = models.stream()
                                .anyMatch(e -> e.getName().equals(k));

                        if (!isMatch)
                                removeList.add(v);
                });

                ObservableList<TreeItem<String>> children = treeView.getRoot().getChildren();

                if (!removeList.isEmpty()) {
                        for (ODBNConnection connection : removeList) {
                                children.remove(connection);
                                connections.remove(connection.getName());
                        }
                }

                for (ConnectionInfo info : models) {
                        if (connections.containsKey(info.getName()))
                                continue;

                        ODBNConnection connection = new ODBNConnection(info);
                        ODBNStatus.getInstance().addConnection(connection);
                        connections.put(info.getName(), connection);
                        children.add(connection);
                }
        }

}
