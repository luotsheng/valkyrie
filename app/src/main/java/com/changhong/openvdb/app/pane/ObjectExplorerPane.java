package com.changhong.openvdb.app.pane;

import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.event.RefreshConnectionEvent;
import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.bus.EventListener;
import com.changhong.openvdb.app.explorer.UIConnectionNode;
import com.changhong.openvdb.app.explorer.UIExplorerNode;
import com.changhong.openvdb.app.menu.ConnectionMenuBuilder;
import com.changhong.openvdb.app.model.ConnectionPropertyModel;
import com.changhong.openvdb.app.model.UINodeGlobalStatus;
import com.changhong.openvdb.app.widgets.VFXTextField;
import com.changhong.openvdb.core.model.ConnectionProfile;
import com.changhong.openvdb.core.repository.ConnectionRepository;
import com.changhong.utils.thread.ThreadPool;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.text.Collator;
import java.util.*;

import static com.changhong.utils.string.StaticLibrary.strimatch;

/**
 * 导航面板
 *
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class ObjectExplorerPane extends VBox implements EventListener
{
        private final TabPane tabPane;
        private final TreeView<String> treeView;
        private final ContextMenu rootContextMenu;

        private final TreeItem<String> root = new TreeItem<>("我的连接", Assets.use("chain"));
        private final VFXTextField search = new VFXTextField();
        private final PauseTransition searchDelay = new PauseTransition(Duration.millis(100));
        private final Map<String, UIConnectionNode> connections = new HashMap<>();

        public ObjectExplorerPane()
        {
                this.tabPane = createTabPane();
                this.treeView = createTreeView();
                this.rootContextMenu = createRootContextMenu();

                EventBus.subscribe(RefreshConnectionEvent.class, this);

                setupContextMenu();
                setupSearchField();
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
                navTab.setGraphic(Assets.use("nav0"));
                navTab.setClosable(false);

                tabPane.getTabs().addAll(navTab);

                return tabPane;
        }

        private void setupSearchField()
        {
                search.setPromptText("搜索...");

                search.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyEvent);

                search.textProperty().addListener((observable, oldVal, newVal) -> {
                        searchDelay.setOnFinished(event -> {
                                if (newVal == null || newVal.isBlank()) {
                                        treeView.setRoot(root);
                                        return;
                                }

                                TreeItem<String> filteredRoot = filterTree(treeView.getRoot(), newVal);

                                filteredRoot.setExpanded(true);
                                treeView.setRoot(filteredRoot);
                        });
                        searchDelay.playFromStart();
                });
        }

        private void onKeyEvent(KeyEvent e)
        {
                if (e.isAltDown() && e.getCode() == KeyCode.BACK_SPACE)
                        search.clear();
        }

        private TreeItem<String> filterTree(TreeItem<String> root, String keyword)
        {
                TreeItem<String> result = new TreeItem<>(root.getValue(), root.getGraphic());

                for (TreeItem<String> child : root.getChildren()) {
                        TreeItem<String> filteredChild = filterTree(child, keyword);

                        boolean matched = strimatch(child.getValue(), keyword);

                        if (matched || !filteredChild.getChildren().isEmpty()) {
                                result.setExpanded(true);
                                result.getChildren().add(filteredChild);
                        }
                }

                return result;
        }

        private TreeView<String> createTreeView()
        {
                TreeView<String> treeView = new TreeView<>(root);
                treeView.setShowRoot(true);

                return treeView;
        }

        private ContextMenu createRootContextMenu()
        {
                ContextMenu rootContextMenu = new ContextMenu();

                Menu newConnectionMenu = ConnectionMenuBuilder.buildNewConnectionMenu();
                MenuItem openAllItem = new MenuItem("打开所有连接");
                openAllItem.setOnAction(event -> batchOpenConnection());
                MenuItem closeAllItem = new MenuItem("关闭所有连接");
                closeAllItem.setOnAction(event -> batchCloseConnection());
                MenuItem refreshAllItem = new MenuItem("刷新连接");

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

        private void batchOpenConnection()
        {
                for (UIConnectionNode node : connections.values()) {
                        if (!node.isOpen())
                                ThreadPool.taskSubmit(() -> Platform.runLater(node::openConnection));
                }
        }

        private void batchCloseConnection()
        {
                for (UIConnectionNode node : connections.values()) {
                        if (node.isOpen())
                                ThreadPool.taskSubmit(node::closeConnection);
                }
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
                VBox vbox = new VBox(search, treeView);
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

                        connection.setDeleteRequestListener(children::remove);
                }

                Collator collator = Collator.getInstance(Locale.CHINA);
                children.sort(Comparator.comparing(
                        node -> ((UIConnectionNode) node).getName(), collator));
        }

}
