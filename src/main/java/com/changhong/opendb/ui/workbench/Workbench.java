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
        private final Tab objectTab;

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");

                objectTab = new Tab("对象");
                objectTab.setClosable(false);

                tabPane.getTabs().addAll(objectTab);
                getChildren().add(tabPane);
        }
}
