package com.changhong.opendb.layout;

import com.changhong.opendb.dialog.connection.ConnectingDialog;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class Navigator extends VBox
{
        private final TabPane tabPane;
        private final TextField searchField;
        private final TreeView<String> treeView;
        private final ContextMenu rootContextMenu;

        public Navigator()
        {
                this.tabPane = createTabPane();
                this.searchField = createSearchField();
                this.treeView = createTreeView();
                this.rootContextMenu = createRootContextMenu();

                setupContextMenu();
                initializeLayout();
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

                rootContextMenu.getItems().addAll(
                        connectMenu,
                        openAllItem,
                        closeAllItem);

                /* 设置事件 */
                mysqlItem.setOnAction(event -> openMySQLConnectionDialog());

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

        private void openMySQLConnectionDialog()
        {
                ConnectingDialog dialog = new ConnectingDialog();
                dialog.showAndWait();
        }
}
