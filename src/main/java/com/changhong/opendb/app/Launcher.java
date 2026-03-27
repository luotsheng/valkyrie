package com.changhong.opendb.app;

import atlantafx.base.theme.CupertinoLight;
import com.changhong.opendb.ui.layout.MainLayout;
import com.changhong.opendb.ui.widgets.ErrorDialog;
import com.changhong.opendb.utils.Catcher;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * @author Luo Tiansheng
 * @since 2026/3/23
 */
public class Launcher extends Application
{
        private static final Class<Launcher> aClass = Launcher.class;

        static void initialize()
        {
                ErrorDialog.initializeListener();
        }

        static void initializeCSS(Scene scene)
        {
                ObservableList<String> stylesheets = scene.getStylesheets();

                URL url = aClass.getResource("/css/no-line-table.css");
                if (url != null)
                        stylesheets.add(url.toExternalForm());
        }

        @Override
        public void start(Stage stage)
        {
                initialize();
                Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                Scene scene = new Scene(new MainLayout(), 1200, 800);
                initializeCSS(scene);
                stage.setTitle("数据库可视化工具");
                stage.setScene(scene);
                stage.show();
        }

        public static void main(String[] args)
        {
                launch();
        }
}
