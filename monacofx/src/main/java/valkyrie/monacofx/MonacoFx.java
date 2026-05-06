package valkyrie.monacofx;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Setter;
import netscape.javascript.JSObject;

import static valkyrie.utils.io.IOUtils.printf;

/**
 * Monaco 编辑器
 *
 * @author Luo Tiansheng
 * @since 2026/5/6
 */
public class MonacoFx extends StackPane
{
        private final WebView webView = new WebView();
        private final WebEngine engine = webView.getEngine();

        private ContextMenu contextMenu = null;

        @Setter
        private ShowContextMenuRequestEvent showContextMenuRequestEvent = null;

        public interface ShowContextMenuRequestEvent {
                void onRequest(ContextMenu contextMenu);
        }

        public interface ValueChangeEvent {
                void onChange();
        }

        public MonacoFx()
        {
                this(null);
        }

        @SuppressWarnings("DataFlowIssue")
        public MonacoFx(String initText)
        {
                webView.setContextMenuEnabled(false);
                engine.load(getClass().getResource("/static/editor.html").toExternalForm());
                getChildren().add(webView);

                webView.setOnMousePressed(event -> {
                        if (event.getButton() == MouseButton.SECONDARY) {
                                showContextMenu(event.getScreenX(), event.getScreenY());
                        } else {
                                hideContextMenu();
                        }
                });

                if (initText != null)
                        replaceSelection(initText);

                setConsole();
        }

        public static class Console {
                @SuppressWarnings("unused")
                public void log(Object message) {
                        printf("[JavaScript] %s\n", message);
                }
        }

        private void setConsole()
        {
                waitAndRun(() -> {
                        JSObject window = (JSObject) engine.executeScript("window");
                        window.setMember("javaConsole", new Console());
                        engine.executeScript(
                                """
                                   console.log = function(message) {
                                       window.javaConsole.log(message);
                                   };
                                   """
                        );
                });
        }

        public void bindContextMenu(ContextMenu contextMenu)
        {
                this.contextMenu = contextMenu;
        }

        private void showContextMenu(double x, double y)
        {
                if (contextMenu == null)
                        return;

                if (showContextMenuRequestEvent != null)
                        showContextMenuRequestEvent.onRequest(contextMenu);

                contextMenu.show(webView, x, y);
        }

        private void hideContextMenu()
        {
                if (contextMenu == null)
                        return;

                contextMenu.hide();
        }

        public String getValue()
        {
                return (String) engine.executeScript("editor.getValue()");
        }

        public String getValueInSelectionRange()
        {
                return (String) engine.executeScript(
                        "window.editor.getModel().getValueInRange(editor.getSelection())"
                );
        }

        public void replaceSelection(String text)
        {
                waitAndRun(() -> {
                        engine.executeScript(
                                "editor.executeEdits('', [{ range: editor.getSelection(), text: " + toJsString(text) + " }])"
                        );
                });
        }

        public void clear()
        {
                waitAndRun(() -> engine.executeScript("editor.setValue('')"));
        }

        public void appendText(String text)
        {
                waitAndRun(() -> {
                        engine.executeScript(
                                "editor.setValue(editor.getValue() + " + toJsString(text) + ")"
                        );
                });
        }

        public void setOnKeyPressedEvent(
                EventHandler<? super KeyEvent> value) {
                webView.setOnKeyPressed(value);
        }

        private void waitAndRun(Runnable task)
        {
                Platform.runLater(() -> {
                        Object ready = engine.executeScript(
                                "typeof window.editor !== 'undefined'"
                        );

                        if (Boolean.TRUE.equals(ready)) {
                                task.run();
                        } else {
                                waitAndRun(task);
                        }
                });
        }

        private static Clipboard clipboard()
        {
                return Clipboard.getSystemClipboard();
        }

        private static String toJsString(String str)
        {
                if (str == null)
                        return "''";

                return "'" + str
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\r", "")
                        .replace("\n", "\\n")
                        + "'";
        }
}
