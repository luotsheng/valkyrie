package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.core.event.*;
import com.changhong.opendb.ui.widgets.SqlEditor;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class Workbench extends VBox implements EventListener
{
        private final TabPane tabPane = new TabPane();
        private final Tab detailTab = new Tab("详情");

        private final List<Tab> queryTabs = new ArrayList<>();

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                VBox.setVgrow(tabPane, Priority.ALWAYS);
                setupDetailTab();

                // 订阅事件
                EventBus.subscribe(OpenWorkbenchPaneEvent.class, this);
                EventBus.subscribe(NewQueryScriptEvent.class, this);
        }

        private void setupDetailTab()
        {
                detailTab.setClosable(false);
                tabPane.getTabs().add(detailTab);
        }

        @Override
        public void onEvent(Event event)
        {
                if (event instanceof OpenWorkbenchPaneEvent e)
                        detailTab.setContent(e.pane);

                if (event instanceof NewQueryScriptEvent) {
                        Tab queryTab = new Tab("查询脚本");
                        tabPane.getTabs().add(queryTab);
                        queryTab.setContent(new SqlEditor());
                        queryTab.setOnCloseRequest(e -> queryTabs.remove(queryTab));
                        queryTabs.add(queryTab);
                        tabPane.getSelectionModel().select(queryTab);
                }
        }
}
