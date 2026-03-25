package com.changhong.opendb.layout;

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

        public Workbench()
        {
                setStyle("-fx-background-color: #ffffff;");

                Tab objectTab = new Tab("对象");
                objectTab.setClosable(false);

                tabPane.getTabs().addAll(objectTab);
                getChildren().add(tabPane);
        }

}
