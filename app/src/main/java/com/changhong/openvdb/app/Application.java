package com.changhong.openvdb.app;

import atlantafx.base.theme.CupertinoLight;
import com.changhong.openvdb.app.layout.MainLayout;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
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
        public static final String TITLE = "OpenVDB v1.0.0-arch.1";

        private static final Class<Application> aClass = Application.class;
        private static final List<LauncherTask> tasks = new ArrayList<>();

        private static Stage primaryStage = null;

        public interface LauncherTask
        {
                void run(Stage stage, Scene scene);
        }

        public static void runLater(LauncherTask runnable)
        {
                tasks.add(runnable);
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

        public static Stage createByPrimaryStage()
        {
                Stage stage = new Stage();
                
                stage.initOwner(primaryStage);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.centerOnScreen();

                return stage;
        }

        static void initializeVFX(Scene scene)
        {
                addVFXStylesheet(scene, "/css/vfx-table-view.css");
                addVFXStylesheet(scene, "/css/vfx-icon-button.css");
                addVFXStylesheet(scene, "/css/vfx-code-area.css");
        }

        @Override
        @SuppressWarnings("CommentedOutCode")
        public void start(Stage stage)
        {
                primaryStage = stage;

                // javafx.application.Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                Scene scene = new Scene(new MainLayout(), 1200, 800);
                initializeVFX(scene);
                stage.setTitle(TITLE);
                stage.setScene(scene);
                stage.setMaximized(true);

                // ObservableList<Screen> screens = Screen.getScreens();
                //
                // if (screens.size() > 1) {
                //         Screen second = screens.get(1);
                //         Rectangle2D bounds = second.getVisualBounds();
                //         stage.setX(bounds.getMinX());
                //         stage.setY(bounds.getMinY());
                // }

                tasks.forEach(task -> task.run(stage, scene));

                stage.show();
        }

        public static void start()
        {
                launch();
        }
}
