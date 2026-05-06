package valkyrie.monacofx;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Setter;

import java.util.Objects;

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
                void request(ContextMenu contextMenu);
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

                webView.prefWidthProperty().bind(widthProperty());
                webView.prefHeightProperty().bind(heightProperty());

                webView.setOnMousePressed(event -> {
                        if (event.getButton() == MouseButton.SECONDARY) {
                                showContextMenu(event.getScreenX(), event.getScreenY());
                        } else {
                                hideContextMenu();
                        }
                });

                // 好像 WebView 中不能直接通过 Document.copy() 函数直接复制内容到
                // 系统剪贴板，所以这里在外层自己实现 Ctrl+C 操作。
                webView.setOnKeyPressed(e -> {
                        if (e.isShortcutDown()) {
                                if (Objects.requireNonNull(e.getCode()) == KeyCode.C)
                                        onCopy();
                        }
                });

                if (initText != null)
                        replaceSelection(initText);
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
                        showContextMenuRequestEvent.request(contextMenu);

                contextMenu.show(webView, x, y);
        }

        private void hideContextMenu()
        {
                if (contextMenu == null)
                        return;

                contextMenu.hide();
        }

        private void onCopy()
        {
                Platform.runLater(() -> {
                        ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.putString(getValueInSelectionRange());
                        clipboard().setContent(clipboardContent);
                });
        }

        private void onPaste()
        {
                replaceSelection(clipboard().getString());
        }

        public String getValue()
        {
                return (String) engine.executeScript(
                        "editor.getValue()"
                );
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
