package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.core.event.*;
import com.changhong.opendb.model.ConnectionInfo;
import com.changhong.opendb.ui.widgets.VFX;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class Workbench extends VBox implements EventListener
{
        private final TabPane tabPane = VFX.newTabPane();
        private final Tab detailTab = new Tab("详情");
        private final Map<String, Tab> queryResultTab = new HashMap<>();

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                VBox.setVgrow(tabPane, Priority.ALWAYS);
                setupDetailTab();

                // 订阅事件
                EventBus.subscribe(OpenWorkbenchPaneEvent.class, this);
                EventBus.subscribe(CloseWorkbenchPaneEvent.class, this);
                EventBus.subscribe(NewQueryScriptEvent.class, this);
                EventBus.subscribe(NewQueryResultSetPaneEvent.class, this);
        }

        private void setupDetailTab()
        {
                detailTab.setClosable(false);
                tabPane.getTabs().add(detailTab);
        }

        static int idx = 0;

        private static String queryName(ConnectionInfo info)
        {
                return strfmt("查询脚本@%s_%d.sql", info == null ? "[ N/A ]" : info.getName(), (idx++));
        }

        private void openNewQueryScriptPane(NewQueryScriptEvent event)
        {
                ConnectionInfo info = event.info;

                String name = queryName(info);
                Tab queryTab = new Tab(name);
                queryTab.setContent(new SqlEditor(name));
                tabPane.getTabs().add(queryTab);
                tabPane.getSelectionModel().select(queryTab);
        }

        @Override
        public void onEvent(Event event)
        {
                if (event instanceof OpenWorkbenchPaneEvent e)
                        detailTab.setContent(e.getPane());

                if (event instanceof CloseWorkbenchPaneEvent e &&
                        detailTab.getContent() == e.getPane()) {
                        detailTab.setContent(null);
                }

                if (event instanceof NewQueryScriptEvent e) {
                        openNewQueryScriptPane(e);
                }

                if (event instanceof NewQueryResultSetPaneEvent e) {
                        PreviewTableDataPane pane = new PreviewTableDataPane(
                                e.jdbcTemplate,
                                e.database,
                                e.info
                        );

                        String id = strfmt("%s@%s (%s)",
                                e.info.getName(),
                                e.database,
                                e.jdbcTemplate.getConnectionName());

                        if (queryResultTab.containsKey(id)) {
                                Tab tab = queryResultTab.get(id);
                                tabPane.getSelectionModel().select(tab);
                                return;
                        }

                        Tab tab = new Tab(id);
                        tab.setContent(pane);
                        tab.setOnCloseRequest(closeEvent -> {
                                Tab closeTab = (Tab) closeEvent.getTarget();
                                queryResultTab.remove(closeTab.getText());
                        });

                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                        queryResultTab.put(id, tab);
                }
        }
}
