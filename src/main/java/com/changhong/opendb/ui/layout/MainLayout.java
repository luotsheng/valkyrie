package com.changhong.opendb.ui.layout;

import com.changhong.opendb.ui.menu.AppMenuBar;
import javafx.scene.layout.BorderPane;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class MainLayout extends BorderPane
{
        public MainLayout()
        {
                setTop(new AppMenuBar());
                setCenter(new ContainerLayout());
        }
}
