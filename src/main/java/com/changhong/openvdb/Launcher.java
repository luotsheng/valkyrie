package com.changhong.openvdb;

import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Luo Tiansheng
 * @since 2026/3/23
 */
public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        MainLayout mainLayout = new MainLayout(stage);
        Scene scene = new Scene(mainLayout, 1200, 800);
        stage.setTitle("数据库可视化工具");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
