package valkyrie.app.widgets;

import valkyrie.app.Application;
import valkyrie.app.workbench.SqlKeyWordDefine;
import valkyrie.utils.system.OS;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.*;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static valkyrie.utils.string.StaticLibrary.strempty;

/**
 * @author Luo Tiansheng
 * @since 2026/4/2
 */
public class VkCodeArea extends CodeArea
{
        static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>\\b(" + String.join("|", SqlKeyWordDefine.KEYWORDS) + ")\\b)"
                        + "|(?<STRING>'([^'\\\\]|\\\\.)*')"
                        + "|(?<COMMENT>--[^\\n]*)"
                        + "|(?<NUMBER>\\b\\d+(?:\\.\\d+)?\\b)",
                Pattern.CASE_INSENSITIVE
        );

        public interface HighlightingListener {
                void apply(VkCodeArea area);
        }

        public interface ShowingMenuListener {
                void apply(WindowEvent event, ContextMenu contextMenu);
        }

        public interface ConfigureContextMenuHandler {
                void handle(ContextMenu contextMenu);
        }

        private final List<HighlightingListener> highlightingListeners = new ArrayList<>();
        private final List<ShowingMenuListener> showingMenuListeners = new ArrayList<>();

        private final ContextMenu contextMenu = new ContextMenu();

        public VkCodeArea()
        {
                this(new VkCodeAreaCreateInfo());
        }

        public VkCodeArea(VkCodeAreaCreateInfo config)
        {
                setStyle("-fx-font-weight: normal;");

                if (OS.isMacOS())
                        setStyle("-fx-font-family: 'Menlo'; -fx-font-size: 16px;");

                if (OS.isWindows())
                        setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 18px;");

                if (OS.isLinux())
                        setStyle("-fx-font-family: 'DejaVu Sans Mono'; -fx-font-size: 16px;");

                getStyleClass().add("vk-code-area");
                setParagraphGraphicFactory(LineNumberFactory.get(this));

                multiPlainChanges()
                        .successionEnds(Duration.ofMillis(50))
                        .subscribe(ignore -> applyHighlightingCurrentLine());

                /* 配置菜单 */
                contextMenu.setStyle("-fx-font-size: 13px");

                if (config.enableCopy) {
                        MenuItem copyTextItem = new MenuItem("复制");
                        copyTextItem.setOnAction(event -> copy());
                        copyTextItem.setAccelerator(
                                new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN)
                        );

                        contextMenu.getItems().add(copyTextItem);
                }

                if (config.enablePaste) {
                        MenuItem pasteTextItem = new MenuItem("粘贴");
                        pasteTextItem.setOnAction(event -> paste());
                        pasteTextItem.setAccelerator(
                                new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN)
                        );
                        contextMenu.getItems().add(pasteTextItem);
                }

                if (config.contextMenuHandler != null)
                        config.contextMenuHandler.handle(contextMenu);

                showingMenuListeners.add((event, contextMenu) -> {
                        contextMenu.getItems().forEach(menu -> {
                                if (menu.getText() == null)
                                        return;

                                if (menu.getText().equals("复制")) {
                                        menu.setDisable(strempty(getSelectedText()));
                                } else if (menu.getText().equals("粘贴")) {
                                        menu.setDisable(!Clipboard.getSystemClipboard().hasString());
                                }
                        });
                });

                if (config.showingMenuListener != null)
                        showingMenuListeners.add(config.showingMenuListener);

                addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        if (!event.isShortcutDown())
                                return;

                        switch (event.getCode()) {
                                case KeyCode.D -> {
                                        int line = getCurrentParagraph();
                                        var text = getParagraph(line).getText();
                                        int pos  = getAbsolutePosition(line, getParagraphLength(line));

                                        insertText(pos, "\n" + text);
                                }

                                case KeyCode.X -> {
                                        if (getSelectedText() == null) {
                                                int line = getCurrentParagraph();

                                                int start = getAbsolutePosition(line, 0);
                                                int end = start + getParagraph(line).length() + 1;

                                                String text = getText(start, end);
                                                Application.copyToClipboard(text);

                                                deleteText(start, end);
                                        }
                                }

                                default -> {}
                        }
                });

                contextMenu.setOnShowing(event -> showingMenuListeners.forEach(listener ->
                        listener.apply(event, contextMenu)));

                setContextMenu(contextMenu);
        }

        @Override
        public void copy()
        {
                Application.copyToClipboard(getSelectedText());
        }

        @Override
        public void paste()
        {
                String text = Application.getClipboardText();

                if (text == null)
                        return;

                replaceSelection(text.replaceAll("\t", "  "));
                applyHighlightAll();
        }

        @Override
        public void undo()
        {
                super.undo();
                applyHighlightAll();
        }

        public void addContextMenuGroup(MenuItem... items)
        {
                contextMenu.getItems().add(new SeparatorMenuItem());

                for (MenuItem item : items) {
                        contextMenu.getItems().add(item);
                }
        }

        public void addHighlightingListener(HighlightingListener listener)
        {
                highlightingListeners.add(listener);
        }

        public void addShowingMenuListener(ShowingMenuListener showingMenuListener)
        {
                showingMenuListeners.add(showingMenuListener);
        }

        public void applyHighlightAll()
        {
                String fullText = getText();
                // 清除所有样式
                clearStyle(0, fullText.length());

                Matcher matcher = PATTERN.matcher(fullText);
                while (matcher.find()) {
                        String styleClass = getHighlightStyleClass(matcher);
                        if (styleClass != null) {
                                setStyleClass(matcher.start(), matcher.end(), styleClass);
                        }
                }

                highlightingListeners.forEach(listener -> listener.apply(this));
        }

        private void applyHighlightingCurrentLine() {
                int lineIdx = getCurrentParagraph(); // RichTextFX 中段落即为行
                int lineStart = getAbsolutePosition(lineIdx, 0);
                int lineEnd = getAbsolutePosition(lineIdx, getParagraphLength(lineIdx));

                // 清除当前行的样式
                setStyleClass(lineStart, lineEnd, null); // 移除所有样式类

                // 获取当前行的文本内容
                String lineText = getText(lineIdx);
                if (lineText.isEmpty()) return;

                // 对当前行进行匹配
                Matcher matcher = PATTERN.matcher(lineText);
                while (matcher.find()) {
                        String styleClass = getHighlightStyleClass(matcher);
                        if (styleClass != null) {
                                int start = lineStart + matcher.start();
                                int end = lineStart + matcher.end();
                                setStyleClass(start, end, styleClass);
                        }
                }

                highlightingListeners.forEach(listener -> listener.apply(this));
        }

        private static String getHighlightStyleClass(Matcher matcher)
        {
                String styleClass = null;

                if (matcher.group("KEYWORD") != null) {
                        styleClass = "keyword";
                } else if (matcher.group("STRING") != null) {
                        styleClass = "string";
                } else if (matcher.group("COMMENT") != null) {
                        styleClass = "comment";
                } else if (matcher.group("NUMBER") != null) {
                        styleClass = "number";
                }

                return styleClass;
        }

        public String getCurrentWord()
        {
                int caret = getCaretPosition();
                String text = getText();

                int start = caret - 1;

                while (start >= 0 && Character.isLetterOrDigit(text.charAt(start)))
                        start--;

                start++;

                return text.substring(start, caret);
        }

}
