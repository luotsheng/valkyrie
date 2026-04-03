package com.changhong.opendb.app;

import atlantafx.base.theme.CupertinoLight;
import com.changhong.opendb.ui.layout.MainLayout;
import com.changhong.opendb.ui.widgets.ErrorDialog;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/23
 */
public class Application extends javafx.application.Application
{
        private static final Class<Application> aClass = Application.class;
        private static final List<LauncherRunnable> runnables = new ArrayList<>();

        public interface LauncherRunnable
        {
                void run(Stage stage, Scene scene);
        }

        public static void runLater(LauncherRunnable runnable)
        {
                runnables.add(runnable);
        }

        public static void copyToClipboard(String text)
        {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(text);
                clipboard.setContent(clipboardContent);
        }

        public static String getClipboardText()
        {
                return Clipboard.getSystemClipboard().getString();
        }

        private static void addVFXStylesheet(Scene scene, String path)
        {
                URL url = aClass.getResource(path);
                ObservableList<String> stylesheets = scene.getStylesheets();

                if (url != null)
                        stylesheets.add(url.toExternalForm());

        }

        static void initialize()
        {
                ErrorDialog.initializeListener();
        }

        static void initializeVFX(Scene scene)
        {
                addVFXStylesheet(scene, "/css/vfx-table-view.css");
                addVFXStylesheet(scene, "/css/vfx-icon-button.css");
                addVFXStylesheet(scene, "/css/vfx-code-area.css");
        }

        @Override
        public void start(Stage stage)
        {
                initialize();
                javafx.application.Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                Scene scene = new Scene(new MainLayout(), 1200, 800);
                initializeVFX(scene);
                stage.setTitle("数据库可视化工具");
                stage.setScene(scene);
                stage.setMaximized(true);

                ObservableList<Screen> screens = Screen.getScreens();

                if (screens.size() > 1) {
                        Screen second = screens.get(1);
                        Rectangle2D bounds = second.getVisualBounds();
                        stage.setX(bounds.getMinX());
                        stage.setY(bounds.getMinY());
                }

                runnables.forEach(runnable -> runnable.run(stage, scene));

                stage.show();
        }

        public static void main(String[] args)
        {
                launch();
        }
}
