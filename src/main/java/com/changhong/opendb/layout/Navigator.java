package com.changhong.opendb.layout;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal", "unchecked"})
public class Navigator extends VBox
{
        private final TabPane tabPane = new TabPane();

        public Navigator()
        {
                setPadding(new Insets(0));

                Tab navTab = new Tab("连接管理");
                navTab.setClosable(false);

                tabPane.getTabs().addAll(
                        navTab
                );

                TextField searchField = new TextField();
                searchField.setPromptText("搜索...");

                TreeItem<String> rootItem = new TreeItem<>("根节点");

                TreeItem<String> child1Item = new TreeItem<>("本地数据库");
                TreeItem<String> child2Item = new TreeItem<>("其他数据库");
                TreeItem<String> child3Item = new TreeItem<>("长虹数据库");

                rootItem.getChildren().addAll(child1Item, child2Item, child3Item);

                TreeView<String> treeView = new TreeView<>(rootItem);
                treeView.setShowRoot(true);

                VBox vbox = new VBox(searchField, treeView);
                vbox.setSpacing(2);

                VBox.setVgrow(treeView, Priority.ALWAYS);

                navTab.setContent(vbox);

                getChildren().add(tabPane);
                setVgrow(tabPane, Priority.ALWAYS);
        }
}
