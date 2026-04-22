package com.changhong.openvdb.app.workbench;

import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.bus.EventListener;
import com.changhong.openvdb.app.event.workbench.CloseNavigationPaneEvent;
import com.changhong.openvdb.app.event.workbench.CloseWorkbenchTabEvent;
import com.changhong.openvdb.app.event.workbench.OpenNavigationPaneEvent;
import com.changhong.openvdb.app.event.workbench.OpenTabEvent;
import com.changhong.openvdb.app.exception.ApplicationException;
import com.changhong.openvdb.app.widgets.VFXTabPane;
import com.changhong.utils.collection.Lists;
import com.changhong.utils.collection.Maps;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.changhong.utils.collection.Lists.beg;
import static com.changhong.utils.string.StaticLibrary.streq;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class Workbench extends VBox implements EventListener
{
        private final VFXTabPane tabPane = new VFXTabPane();
        private final Tab navigationTab = new Tab("首页");
        private final Map<Object, List<Tab>> tabPaneManager = Maps.newHashMap();

        private final ContextMenu tabPaneContextMenu = new ContextMenu();
        private final MenuItem closeCurrent = new MenuItem("关闭当前");
        private final MenuItem closeAll = new MenuItem("关闭所有");
        private final MenuItem closeLeft = new MenuItem("关闭左侧");
        private final MenuItem closeRight = new MenuItem("关闭右侧");
        private final MenuItem closeOther = new MenuItem("关闭其他");

        private Object navigationTabOwner = null;

        public Workbench()
        {
                navigationTab.setGraphic(Assets.use("home"));

                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                VBox.setVgrow(tabPane, Priority.ALWAYS);

                setupTabPane();
                setupContextMenu();
                setupHomeTab();

                // 订阅事件
                EventBus.subscribe(OpenTabEvent.class, this);
                EventBus.subscribe(CloseWorkbenchTabEvent.class, this);
                EventBus.subscribe(OpenNavigationPaneEvent.class, this);
                EventBus.subscribe(CloseNavigationPaneEvent.class, this);
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

        private void setupHomeTab()
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
                        case CloseWorkbenchTabEvent e -> handleCloseTabEvent(e);
                        case OpenNavigationPaneEvent e -> handleSetNavigationPaneEvent(e);
                        case CloseNavigationPaneEvent e -> handleUnsetNavigationPaneEvent(e);
                        default -> throw new ApplicationException("unsupported event type");
                }
        }

        private void handleOpenTabEvent(OpenTabEvent e)
        {
                String tabId = e.tabId();

                if (tabPaneManager.containsKey(e.owner())) {
                        var tabs = tabPaneManager.get(e.owner()).stream()
                                .filter(o -> streq(o.getText(), tabId))
                                .toList();

                        if (!tabs.isEmpty()) {
                                tabPane.select(beg(tabs));
                                return;
                        }
                }

                Tab tab = new Tab(tabId);
                tab.setContent(e.createPane(tab));
                tabPaneManager.computeIfAbsent(e.owner(), tabPane -> Lists.newArrayList())
                        .add(tab);
                tabPane.addAndSelect(tab);

                tab.setOnCloseRequest(event ->
                        tabPaneManager.values().forEach(o -> o.remove(tab)));
        }

        private void handleCloseTabEvent(CloseWorkbenchTabEvent e)
        {
                if (!tabPaneManager.containsKey(e.owner()))
                        return;

                var tabs = tabPaneManager.remove(e.owner());
                tabs.forEach(tabPane.getTabs()::remove);
        }

        private void handleSetNavigationPaneEvent(OpenNavigationPaneEvent e)
        {
                navigationTab.setContent(e.getPane());
                navigationTabOwner = e.getOwner();
                tabPane.select(navigationTab);
        }

        private void handleUnsetNavigationPaneEvent(CloseNavigationPaneEvent e)
        {
                if (navigationTabOwner != null && navigationTabOwner == e.getOwner()) {
                        navigationTab.setContent(null);
                        navigationTabOwner = null;
                }
        }
}
