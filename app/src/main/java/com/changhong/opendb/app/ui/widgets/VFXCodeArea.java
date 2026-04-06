package com.changhong.opendb.app.ui.widgets;

import com.changhong.opendb.app.ui.workbench.SqlKeyWordDefine;
import com.changhong.opendb.app.utils.OS;
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

import static com.changhong.string.StringUtils.strempty;

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

                if (OS.isMac())
                        setStyle("-fx-font-family: 'Monaco'; -fx-font-size: 19px;");

                if (OS.isWindows())
                        setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 19px;");

                if (OS.isLinux())
                        setStyle("-fx-font-family: 'DejaVu Sans Mono'; -fx-font-size: 19px;");

                getStyleClass().add("vfx-code-area");
                setParagraphGraphicFactory(LineNumberFactory.get(this));

                multiPlainChanges()
                        .successionEnds(Duration.ofMillis(200))
                        .subscribe(ignore -> applyHighlighting());

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

        public void applyHighlighting()
        {
                String text = getText();
                clearStyle(0, text.length());

                if (text.split("\n").length > 5000)
                        return;

                Matcher matcher = PATTERN.matcher(text);
                while (matcher.find()) {
                        if (matcher.group("KEYWORD") != null) {
                                setStyleClass(matcher.start(), matcher.end(), "keyword");
                        } else if (matcher.group("STRING") != null) {
                                setStyleClass(matcher.start(), matcher.end(), "string");
                        } else if (matcher.group("COMMENT") != null) {
                                setStyleClass(matcher.start(), matcher.end(), "comment");
                        } else if (matcher.group("NUMBER") != null) {
                                setStyleClass(matcher.start(), matcher.end(), "number");
                        }
                }

                highlightingListeners.forEach(listener -> listener.apply(this));
        }
}
