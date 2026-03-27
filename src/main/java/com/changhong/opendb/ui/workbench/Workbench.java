package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.core.event.Event;
import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.EventListener;
import com.changhong.opendb.core.event.OpenWorkbenchPaneEvent;
import com.changhong.opendb.ui.pane.DatabaseDetailPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class Workbench extends VBox implements EventListener
{
        private final TabPane tabPane = new TabPane();
        private final Tab detailTab = new Tab("详情");

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                VBox.setVgrow(tabPane, Priority.ALWAYS);
                setupDetailTab();

                // 订阅事件
                EventBus.subscribe(OpenWorkbenchPaneEvent.class, this);
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
        }
}
