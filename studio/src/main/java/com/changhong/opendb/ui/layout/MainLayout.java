package com.changhong.opendb.ui.layout;

import com.changhong.opendb.ui.menu.AppMenuBar;
import com.changhong.opendb.ui.tool.AppToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class MainLayout extends BorderPane
{
        public MainLayout()
        {
                AppMenuBar appMenuBar = new AppMenuBar();
                AppToolBar appToolBar = new AppToolBar();

                VBox vBox = new VBox(appMenuBar, appToolBar);
                VBox.setVgrow(appToolBar, Priority.ALWAYS);

                setTop(vBox);
                setCenter(new ContainerLayout());
        }
}
