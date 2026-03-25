package com.changhong.opendb;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Luo Tiansheng
 * @since 2026/3/23
 */
public class Launcher extends Application
{
        @Override
        public void start(Stage stage)
        {
                Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                Scene scene = new Scene(new MainLayout(), 1200, 800);
                stage.setTitle("数据库可视化工具");
                stage.setScene(scene);
                stage.show();
        }

        public static void main(String[] args)
        {
                launch();
        }
}
