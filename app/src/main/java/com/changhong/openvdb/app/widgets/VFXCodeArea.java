package com.changhong.openvdb.app.widgets;

import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.workbench.SqlKeyWordDefine;
import com.changhong.utils.system.OS;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.changhong.utils.string.StaticLibrary.strempty;

/**
 * @author Luo Tiansheng
 * @since 2026/4/2
 */
public class VFXCodeArea extends CodeArea
{
        static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>\\b(" + String.join("|", SqlKeyWordDefine.KEYWORDS) + ")\\b)"
                        + "|(?<STRING>'([^'\\\\]|\\\\.)*')"
                        + "|(?<COMMENT>--[^\\n]*)"
                        + "|(?<NUMBER>\\b\\d+(?:\\.\\d+)?\\b)",
                Pattern.CASE_INSENSITIVE
        );

        public interface HighlightingListener {
                void apply(VFXCodeArea area);
        }

        public interface ShowingMenuListener {
                void apply(WindowEvent event);
        }

        private final List<HighlightingListener> highlightingListeners = new ArrayList<>();
        private final List<ShowingMenuListener> showingMenuListeners = new ArrayList<>();

        private final ContextMenu contextMenu = new ContextMenu();

        public VFXCodeArea()
        {
                this(new VFXCodeAreaConfig());
        }

        public VFXCodeArea(VFXCodeAreaConfig config)
        {
                setStyle("-fx-font-weight: normal;");

                if (OS.isMacOS())
                        setStyle("-fx-font-family: 'Menlo'; -fx-font-size: 16px;");

                if (OS.isWindows())
                        setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 18px;");

                if (OS.isLinux())
                        setStyle("-fx-font-family: 'DejaVu Sans Mono'; -fx-font-size: 16px;");

                getStyleClass().add("vfx-code-area");
                setParagraphGraphicFactory(LineNumberFactory.get(this));

                multiPlainChanges()
                        .successionEnds(Duration.ofMillis(50))
                        .subscribe(ignore -> applyHighlightingCurrentLine());

                /* 配置菜单 */
                contextMenu.setStyle("-fx-font-size: 13px");

                if (config.enableCopy) {
                        MenuItem copyTextItem = new MenuItem("复制");
                        copyTextItem.setOnAction(event -> copy());
                        contextMenu.getItems().add(copyTextItem);
                }

                if (config.enablePaste) {
                        MenuItem pasteTextItem = new MenuItem("粘贴");
                        pasteTextItem.setOnAction(event -> paste());
                        contextMenu.getItems().add(pasteTextItem);
                }

                showingMenuListeners.add(event -> {
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

                contextMenu.setOnShowing(event -> showingMenuListeners.forEach(listener -> listener.apply(event)));

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

}
