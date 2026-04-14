package com.changhong.openvdb.app.navigator;

import com.changhong.openvdb.app.model.UINodeGlobalStatus;
import com.changhong.openvdb.app.menu.ConnectionMenuBuilder;
import com.changhong.openvdb.app.navigator.node.UIExplorerNode;
import com.changhong.openvdb.core.model.ConnectionProfile;
import com.changhong.openvdb.core.repository.ConnectionRepository;
import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.bus.EventListener;
import com.changhong.openvdb.app.event.RefreshConnectionEvent;
import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.navigator.node.UIConnectionNode;
import com.changhong.openvdb.app.model.ConnectionPropertyModel;
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

        private final Map<String, UIConnectionNode> connections
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
                refreshConnectionNode();

                treeView.getRoot().setExpanded(true);
        }

        @Override
        public void onEvent(Event event)
        {
                if (event instanceof RefreshConnectionEvent)
                        refreshConnectionNode();
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
                refreshAllItem.setOnAction(event -> refreshConnectionNode());

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

                                if (item instanceof UIExplorerNode vdbNode) {
                                        vdbNode.showContextMenu(cell, x, y);
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

                                if (treeItem instanceof UIExplorerNode vdbNode) {
                                        vdbNode.onSelectedEvent(vdbNode);
                                }

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

                                        if (!(item instanceof UIExplorerNode vdbNode))
                                                return;

                                        vdbNode.onMouseDoubleClickEvent(event);
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

        private void refreshConnectionNode()
        {
                List<UIConnectionNode> removeList = new ArrayList<>();
                List<ConnectionProfile> profiles = ConnectionRepository.loadConnections();

                connections.forEach((k, v) -> {
                        boolean isMatch = profiles.stream()
                                .anyMatch(e -> e.getName().equals(k));

                        if (!isMatch)
                                removeList.add(v);
                });

                ObservableList<TreeItem<String>> children = treeView.getRoot().getChildren();

                if (!removeList.isEmpty()) {
                        for (UIConnectionNode connection : removeList) {
                                children.remove(connection);
                                connections.remove(connection.getName());
                        }
                }

                for (ConnectionProfile profile : profiles) {
                        if (connections.containsKey(profile.getName()))
                                continue;

                        ConnectionPropertyModel propertyModel = new ConnectionPropertyModel(profile);

                        UIConnectionNode connection = new UIConnectionNode(propertyModel);
                        UINodeGlobalStatus.getInstance().addConnection(connection);
                        connections.put(profile.getName(), connection);
                        children.add(connection);
                }
        }

}
