package com.changhong.opendb;

import com.changhong.opendb.layout.ContainerLayout;
import com.changhong.opendb.menu.ODBMenuBar;
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
                setTop(new ODBMenuBar());
                setCenter(new ContainerLayout());
        }
}
