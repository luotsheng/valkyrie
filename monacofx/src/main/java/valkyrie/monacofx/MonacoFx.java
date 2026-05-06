package valkyrie.monacofx;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.Objects;

import static valkyrie.utils.string.StaticLibrary.strempty;

/**
 * @author Luo Tiansheng
 * @since 2026/5/6
 */
public class MonacoFx extends StackPane
{
        private final WebView webView = new WebView();
        private final WebEngine engine = webView.getEngine();

        private final ContextMenu contextMenu = new ContextMenu();
        private final MenuItem copyMenuItem = new MenuItem("复制");
        private final MenuItem pasteMenuItem = new MenuItem("粘贴");

        public MonacoFx()
        {
                this(null);
        }

        @SuppressWarnings("DataFlowIssue")
        public MonacoFx(String initText)
        {
                initContextMenu();

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

        private void initContextMenu()
        {
                copyMenuItem.setOnAction(event -> onCopy());
                copyMenuItem.setAccelerator(
                        new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN)
                );

                pasteMenuItem.setOnAction(event -> onPaste());
                pasteMenuItem.setAccelerator(
                        new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN)
                );

                contextMenu.getItems().addAll(copyMenuItem, pasteMenuItem);
        }

        private void showContextMenu(double x, double y)
        {
                copyMenuItem.setDisable(strempty(getSelectedText()));
                contextMenu.show(webView, x, y);
        }

        private void hideContextMenu()
        {
                contextMenu.hide();
        }

        private void onCopy()
        {
                Platform.runLater(() -> {
                        ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.putString(getSelectedText());
                        clipboard().setContent(clipboardContent);
                });
        }

        private void onPaste()
        {
                replaceSelection(clipboard().getString());
        }

        public String getSelectedText()
        {
                return (String) engine.executeScript(
                        "window.editor.getModel().getValueInRange(editor.getSelection())"
                );
        }

        public void replaceSelection(String text)
        {
                engine.executeScript(
                        "editor.executeEdits('', [{ range: editor.getSelection(), text: " + toJsString(text) + " }])"
                );
        }

        public WebEngine engine()
        {
                return engine;
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
