package com.changhong.opendb.ui.workbench;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class Workbench extends VBox
{
        private final TabPane tabPane = new TabPane();
        private final Tab detailTab = new Tab("详情");

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");
                getChildren().add(tabPane);
                setupDetailTab();
        }

        private void setupDetailTab()
        {
                detailTab.setClosable(false);
                tabPane.getTabs().add(detailTab);
        }
}
