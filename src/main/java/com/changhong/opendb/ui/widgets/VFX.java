package com.changhong.opendb.ui.widgets;

import com.changhong.opendb.resource.ResourceManager;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public class VFX
{
        public static ContextMenu tabPaneContextMenu = null;

        private static MenuItem closeCurrent = null;
        private static MenuItem closeAll = null;
        private static MenuItem closeLeft = null;
        private static MenuItem closeRight = null;
        private static MenuItem closeOther = null;

        public static <S> TableView<S> newTableView()
        {
                TableView<S> table = new TableView<>();
                table.getStyleClass().add("vfx-table-view");
                table.setFixedCellSize(26);
                return table;
        }

        public static <S, T> TableColumn<S, T> newTableColumn(String name)
        {
                return new TableColumn<>(name);
        }

        public static Button newIconButton(String tip, String icon)
        {
                Button button = new Button();

                button.getStyleClass().add("vfx-icon-button");
                button.setTooltip(new Tooltip(tip));
                button.setGraphic(ResourceManager.use(icon));

                return button;
        }

        public static TabPane newTabPane()
        {
                TabPane tabPane = new TabPane();

                tabPane.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                        Node node = (Node) event.getTarget();

                        while (node != null && !(node instanceof TabPane)) {
                                if (node.getStyleClass().contains("tab")) {
                                        Tab tab = (Tab) node.getProperties().get(Tab.class);
                                        showTabContextMenu(tabPane, tab, event);
                                        break;
                                }
                                node = node.getParent();
                        }
                });

                return tabPane;
        }

        private static void showTabContextMenu(TabPane tabPane, Tab tab, ContextMenuEvent e)
        {
                if (tabPaneContextMenu == null) {
                        tabPaneContextMenu = new ContextMenu();
                        tabPaneContextMenu.setAutoHide(true);
                        tabPaneContextMenu.setConsumeAutoHidingEvents(false);

                        closeCurrent = new MenuItem("关闭当前");
                        closeAll = new MenuItem("关闭所有");
                        closeLeft = new MenuItem("关闭左侧");
                        closeRight = new MenuItem("关闭右侧");
                        closeOther = new MenuItem("关闭其他");

                        tabPaneContextMenu.getItems().addAll(
                                closeCurrent,
                                closeAll,
                                new SeparatorMenuItem(),
                                closeLeft,
                                closeRight,
                                closeOther);
                }

                closeCurrent.setOnAction(ev -> {
                        tabPane.getTabs().remove(tab);
                });

                closeAll.setOnAction(ev -> {
                        tabPane.getTabs().remove(1, tabPane.getTabs().size());
                });

                closeLeft.setOnAction(ev -> {
                        int index = tabPane.getTabs().indexOf(tab);
                        tabPane.getTabs().remove(1, index);
                        tabPane.getSelectionModel().select(tab);
                });

                closeRight.setOnAction(ev -> {
                        int index = tabPane.getTabs().indexOf(tab);
                        tabPane.getTabs().remove(index + 1, tabPane.getTabs().size());
                        tabPane.getSelectionModel().select(tab);
                });

                closeOther.setOnAction(ev -> {
                        tabPane.getTabs().remove(1, tabPane.getTabs().size());
                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                });

                tabPaneContextMenu.show(tabPane, e.getScreenX(), e.getScreenY());
        }
}
