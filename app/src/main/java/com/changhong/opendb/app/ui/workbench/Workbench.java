package com.changhong.opendb.app.ui.workbench;

import com.changhong.opendb.app.Application;
import com.changhong.opendb.app.core.event.*;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.pane.TableDesignerTablePane;
import com.changhong.opendb.app.ui.pane.PreviewTableDataPane;
import com.changhong.opendb.app.ui.widgets.VFXTabPane;
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

import static com.changhong.string.StringUtils.strwfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class Workbench extends VBox implements EventListener
{
        private final VFXTabPane tabPane = new VFXTabPane();
        private final Tab detailTab = new Tab("详情");
        private final List<SqlEditor> editors = new ArrayList<>();
        
        private final Map<String, Tab> mutableDataGridMgr = new HashMap<>();
        private final Map<TableMetaData, Tab> tableMetaDataMgr = new HashMap<>();

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
                EventBus.subscribe(NewQueryScriptEvent.class, this);
                EventBus.subscribe(NewMutableDataGridPaneEvent.class, this);
                EventBus.subscribe(RemoveSqlEditorTabEvent.class, this);
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

                Application.runLater((stage, scene) -> {
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
                        case RemoveSqlEditorTabEvent e     -> handleRemoveSqlEditorTabEvent(e);
                        case NewQueryScriptEvent e         -> handleNewQueryScriptEvent(e);
                        case NewMutableDataGridPaneEvent e -> handleNewMutableDataGridPaneEvent(e);
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

        private void handleRemoveSqlEditorTabEvent(RemoveSqlEditorTabEvent event)
        {
                for (SqlEditor editor : editors) {
                        if (editor.sqlFileEquals(event.sqlFile))
                                tabPane.remove(editor.getOwnerTab());
                }
        }

        private void handleNewQueryScriptEvent(NewQueryScriptEvent event)
        {
                Tab queryTab = new Tab();
                queryTab.setGraphic(Assets.use("sql"));
                SqlEditor sqlEditor;

                if (event.queryInfo != null) {
                        sqlEditor = new SqlEditor(event.queryInfo, queryTab);
                } else {
                        sqlEditor = new SqlEditor(null, queryTab);
                }

                queryTab.setContent(sqlEditor);
                queryTab.setOnCloseRequest(e -> {
                        if (queryTab.getContent() instanceof SqlEditor editor) {
                                editor.close();
                        }
                });

                editors.add(sqlEditor);
                tabPane.add(queryTab);
                tabPane.select(queryTab);
        }

        private void handleNewMutableDataGridPaneEvent(NewMutableDataGridPaneEvent event)
        {
                String id = strwfmt("%s@%s (%s)",
                        event.info.getName(),
                        event.database,
                        event.sqlExecutor.getConnectionName());

                Tab tab;

                if (mutableDataGridMgr.containsKey(id)) {
                        tab = mutableDataGridMgr.get(id);
                        tabPane.select(tab);
                        return;
                } else {
                        tab = new Tab(id);
                }

                PreviewTableDataPane pane = new PreviewTableDataPane(
                        tab,
                        event.sqlExecutor,
                        event.database,
                        event.info
                );

                tab.setGraphic(Assets.use("table"));
                tab.setContent(pane);
                tab.setOnCloseRequest(closeEvent -> {
                        Tab closeTab = (Tab) closeEvent.getTarget();
                        mutableDataGridMgr.remove(closeTab.getText());
                });

                tabPane.addAndSelect(tab);
                mutableDataGridMgr.put(id, tab);

                pane.asyncUpdate();
        }

        private void handleOpenDesignTablePaneEvent(OpenDesignTablePaneEvent e)
        {
                Tab tab = new Tab(e.id());
                tab.setContent(new TableDesignerTablePane(tab, e.executor, e.table));
                tab.setGraphic(Assets.use("struct1"));
                tabPane.addAndSelect(tab);
                tableMetaDataMgr.put(e.table, tab);
        }
}
