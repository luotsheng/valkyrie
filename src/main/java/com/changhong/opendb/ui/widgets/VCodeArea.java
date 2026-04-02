package com.changhong.opendb.ui.widgets;

import com.changhong.opendb.ui.workbench.SqlKeyWordDefine;
import com.changhong.opendb.utils.OS;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luo Tiansheng
 * @since 2026/4/2
 */
public class VCodeArea extends CodeArea
{
        static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>\\b(" + String.join("|", SqlKeyWordDefine.KEYWORDS) + ")\\b)"
                        + "|(?<STRING>'([^'\\\\]|\\\\.)*')"
                        + "|(?<COMMENT>--[^\\n]*)"
                        + "|(?<NUMBER>\\b\\d+(?:\\.\\d+)?\\b)",
                Pattern.CASE_INSENSITIVE
        );

        public interface HighlightingListener {
                void apply(VCodeArea area);
        }

        private final List<HighlightingListener> highlightings = new ArrayList<>();

        private final ContextMenu contextMenu = new ContextMenu();

        public VCodeArea()
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

                MenuItem copyTextItem = new MenuItem("复制");
                copyTextItem.setOnAction(event -> copy());

                MenuItem pasteTextItem = new MenuItem("粘贴");
                pasteTextItem.setOnAction(event -> paste());

                contextMenu.getItems().addAll(
                        copyTextItem,
                        pasteTextItem
                );

                setContextMenu(contextMenu);
        }

        public void addHighlightingListener(HighlightingListener listener)
        {
                highlightings.add(listener);
        }

        public void applyHighlighting()
        {
                String text = getText();
                clearStyle(0, text.length());

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

                highlightings.forEach(listener -> listener.apply(this));
        }
}
