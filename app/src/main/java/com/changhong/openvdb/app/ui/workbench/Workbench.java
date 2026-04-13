package com.changhong.openvdb.app.ui.workbench;

import com.changhong.openvdb.app.VFXApplication;
import com.changhong.openvdb.app.core.event.*;
import com.changhong.openvdb.app.resource.Assets;
import com.changhong.openvdb.app.ui.pane.TableStructureDesignerPane;
import com.changhong.openvdb.app.ui.pane.PreviewTableDataPane;
import com.changhong.openvdb.app.ui.widgets.VFXTabPane;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.changhong.string.StringStaticize.strwfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class Workbench extends VBox implements EventListener
{
        private final VFXTabPane tabPane = new VFXTabPane();
        private final Tab detailTab = new Tab("详情");
        private final List<ScriptEditor> editors = new ArrayList<>();
        
        private final Map<String, Tab> dataGridMgr = new HashMap<>();
        private final Map<String, Tab> TableMgr = new HashMap<>();

        private final ContextMenu tabPaneContextMenu = new ContextMenu();
        private final MenuItem closeCurrent = new MenuItem("关闭当前");;
        private final MenuItem closeAll = new MenuItem("关闭所有");;
        private final MenuItem closeLeft = new MenuItem("关闭左侧");;
        private final MenuItem closeRight = new MenuItem("关闭右侧");;
        private final MenuItem closeOther = new MenuItem("关闭其他");;

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                VBox.setVgrow(tabPane, Priority.ALWAYS);

                setupTabPane();
                setupContextMenu();
                setupDetailTab();

                // 订阅事件
                EventBus.subscribe(OpenWorkbenchPaneEvent.class, this);
                EventBus.subscribe(CloseWorkbenchPaneEvent.class, this);
                EventBus.subscribe(OpenQueryScriptEvent.class, this);
                EventBus.subscribe(OpenDataGridPaneEvent.class, this);
                EventBus.subscribe(RemoveScriptEditorTabEvent.class, this);
                EventBus.subscribe(OpenDesignTablePaneEvent.class, this);
        }

        private void setupTabPane()
        {
                tabPane.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                        Node node = (Node) event.getTarget();

                        while (node != null && !(node instanceof VFXTabPane)) {
                                if (node.getStyleClass().contains("tab")) {
                                        Tab tab = (Tab) node.getProperties().get(Tab.class);

                                        if (tab == detailTab)
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

                VFXApplication.runLater((stage, scene) -> {
                        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                                if (tabPaneContextMenu.isShowing())
                                        tabPaneContextMenu.hide();
                        });
                });
        }

        private void setupDetailTab()
        {
                detailTab.setClosable(false);
                tabPane.add(detailTab);
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
                        case OpenWorkbenchPaneEvent e      -> handleOpenWorkbenchPaneEvent(e);
                        case CloseWorkbenchPaneEvent e     -> handleCloseWorkbenchPaneEvent(e);
                        case RemoveScriptEditorTabEvent e  -> handleRemoveScriptEditorTabEvent(e);
                        case OpenQueryScriptEvent e        -> handleOpenQueryScriptEvent(e);
                        case OpenDataGridPaneEvent e       -> handleOpenDataGridPaneEvent(e);
                        case OpenDesignTablePaneEvent e    -> handleOpenDesignTablePaneEvent(e);
                        default -> {}
                }
        }

        private void handleOpenWorkbenchPaneEvent(OpenWorkbenchPaneEvent event)
        {
                detailTab.setContent(event.getPane());
                tabPane.select(detailTab);
        }

        @SuppressWarnings("unused")
        private void handleCloseWorkbenchPaneEvent(CloseWorkbenchPaneEvent event)
        {
                if (detailTab.getContent() == event.getPane())
                        detailTab.setContent(null);
        }

        private void handleRemoveScriptEditorTabEvent(RemoveScriptEditorTabEvent event)
        {
                for (ScriptEditor editor : editors) {
                        if (editor.sqlFileEquals(event.sqlFile))
                                tabPane.remove(editor.getOwnerTab());
                }
        }

        private void handleOpenQueryScriptEvent(OpenQueryScriptEvent event)
        {
                Tab queryTab = new Tab();
                queryTab.setGraphic(Assets.use("sql"));
                ScriptEditor scriptEditor;

                if (event.scriptFile != null) {
                        scriptEditor = new ScriptEditor(null, event.scriptFile, queryTab);
                } else {
                        scriptEditor = new ScriptEditor(null, null, queryTab);
                }

                queryTab.setContent(scriptEditor);
                queryTab.setOnCloseRequest(e -> {
                        if (queryTab.getContent() instanceof ScriptEditor editor) {
                                editor.close();
                        }
                });

                editors.add(scriptEditor);
                tabPane.add(queryTab);
                tabPane.select(queryTab);
        }

        private void handleOpenDataGridPaneEvent(OpenDataGridPaneEvent event)
        {
                String id = strwfmt("%s@%s (%s)",
                        event.table.getName(),
                        event.session.catalog(),
                        event.connectionName);

                Tab tab;

                if (dataGridMgr.containsKey(id)) {
                        tab = dataGridMgr.get(id);
                        tabPane.select(tab);
                        return;
                } else {
                        tab = new Tab(id);
                }

                PreviewTableDataPane pane = new PreviewTableDataPane(
                        tab,
                        event.session,
                        event.driver,
                        event.table
                );

                tab.setGraphic(Assets.use("table"));
                tab.setContent(pane);
                tab.setOnCloseRequest(closeEvent -> {
                        Tab closeTab = (Tab) closeEvent.getTarget();
                        dataGridMgr.remove(closeTab.getText());
                });

                tabPane.addAndSelect(tab);
                dataGridMgr.put(id, tab);

                pane.asyncUpdate();
        }

        private void handleOpenDesignTablePaneEvent(OpenDesignTablePaneEvent e)
        {
                Tab tab = TableMgr.get(e.id());

                if (tab == null) {
                        tab = new Tab(e.id());
                        tab.setContent(new TableStructureDesignerPane(tab, e.session, e.driver, e.table));
                        tab.setGraphic(Assets.use("struct1"));
                        TableMgr.put(e.id(), tab);
                }

                tabPane.addAndSelect(tab);
        }
}
