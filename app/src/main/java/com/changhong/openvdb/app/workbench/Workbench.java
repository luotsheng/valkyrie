package com.changhong.openvdb.app.workbench;

import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.bus.EventListener;
import com.changhong.openvdb.app.event.workbench.OpenNavigationPaneEvent;
import com.changhong.openvdb.app.event.workbench.OpenTabEvent;
import com.changhong.openvdb.app.exception.ApplicationException;
import com.changhong.openvdb.app.widgets.VFXTabPane;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class Workbench extends VBox implements EventListener
{
        private final VFXTabPane tabPane = new VFXTabPane();
        private final Tab navigationTab = new Tab("首页");

        private final ContextMenu tabPaneContextMenu = new ContextMenu();
        private final MenuItem closeCurrent = new MenuItem("关闭当前");;
        private final MenuItem closeAll = new MenuItem("关闭所有");;
        private final MenuItem closeLeft = new MenuItem("关闭左侧");;
        private final MenuItem closeRight = new MenuItem("关闭右侧");;
        private final MenuItem closeOther = new MenuItem("关闭其他");;

        public Workbench()
        {
                navigationTab.setGraphic(Assets.use("home"));

                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                VBox.setVgrow(tabPane, Priority.ALWAYS);

                setupTabPane();
                setupContextMenu();
                setuphomeTab();

                // 订阅事件
                EventBus.subscribe(OpenTabEvent.class, this);
                EventBus.subscribe(OpenNavigationPaneEvent.class, this);
        }

        private void setupTabPane()
        {
                tabPane.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                        Node node = (Node) event.getTarget();

                        while (node != null && !(node instanceof VFXTabPane)) {
                                if (node.getStyleClass().contains("tab")) {
                                        Tab tab = (Tab) node.getProperties().get(Tab.class);

                                        if (tab == navigationTab)
                                                return;

                                        showMenu(tabPane, tab, event);
                                        break;
                                }
                                node = node.getParent();
                        }
                });
        }

        private void setupContextMenu()
        {
                tabPaneContextMenu.getItems().addAll(
                        closeCurrent,
                        closeAll,
                        new SeparatorMenuItem(),
                        closeLeft,
                        closeRight,
                        closeOther);

                Application.runLater((stage, scene) -> {
                        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                                if (tabPaneContextMenu.isShowing())
                                        tabPaneContextMenu.hide();
                        });
                });
        }

        private void setuphomeTab()
        {
                navigationTab.setClosable(false);
                tabPane.add(navigationTab);
        }

        private void showMenu(VFXTabPane tabPane, Tab tab, ContextMenuEvent e)
        {
                closeCurrent.setOnAction(ev -> {
                        tabPane.remove(tab);
                });

                closeAll.setOnAction(ev -> {
                        tabPane.remove(1, tabPane.size());
                });

                closeLeft.setOnAction(ev -> {
                        int index = tabPane.indexOf(tab);
                        tabPane.remove(1, index);
                        tabPane.select(tab);
                });

                closeRight.setOnAction(ev -> {
                        int index = tabPane.indexOf(tab);
                        tabPane.remove(index + 1, tabPane.size());
                        tabPane.select(tab);
                });

                closeOther.setOnAction(ev -> {
                        tabPane.removeExcept(tab, 1, tabPane.size());
                        tabPane.add(tab);
                        tabPane.select(tab);
                });

                tabPaneContextMenu.show(tabPane, e.getScreenX(), e.getScreenY());
        }

        @Override
        public void onEvent(Event event)
        {
                switch (event) {
                        case OpenTabEvent e -> handleOpenTabEvent(e);
                        case OpenNavigationPaneEvent e -> handleOpenWorkbenchPaneEvent(e);
                        default -> throw new ApplicationException("unsupported event type");
                }
        }

        private void handleOpenTabEvent(OpenTabEvent e)
        {
                Tab tab = new Tab(e.tabId());
                tab.setContent(e.createPane(tab));
                tabPane.addAndSelect(tab);
        }

        private void handleOpenWorkbenchPaneEvent(OpenNavigationPaneEvent event)
        {
                navigationTab.setContent(event.getPane());
                tabPane.select(navigationTab);
        }
}
