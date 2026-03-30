package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.core.event.*;
import com.changhong.opendb.model.ConnectionInfo;
import com.changhong.opendb.ui.widgets.VFX;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

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

        private final List<Tab> queryTabs = new ArrayList<>();

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
        }

        private void setupDetailTab()
        {
                detailTab.setClosable(false);
                tabPane.getTabs().add(detailTab);
        }

        static int idx = 0;

        private void openNewQueryScriptPane(NewQueryScriptEvent event)
        {
                ConnectionInfo info = event.info;

                Tab queryTab = new Tab(strfmt("查询脚本@%s_%d.sql", info == null ? "[ N/A ]" : info.getName(), (idx++)));
                queryTab.setContent(new SqlEditor());
                queryTab.setOnCloseRequest(e -> queryTabs.remove(queryTab));

                queryTabs.add(queryTab);
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
        }
}
