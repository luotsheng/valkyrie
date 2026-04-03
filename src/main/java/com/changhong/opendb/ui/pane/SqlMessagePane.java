package com.changhong.opendb.ui.pane;

import com.changhong.opendb.ui.widgets.VCodeArea;
import com.changhong.opendb.ui.widgets.VCodeAreaConfig;
import javafx.scene.control.MenuItem;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/4/2
 */
public class SqlMessagePane extends VirtualizedScrollPane<VCodeArea>
{
        private final VCodeArea codeArea;

        static final Pattern PATTERN = Pattern.compile(
                "(?<INFO>\\[\\s*OK\\s*])"
                        + "|(?<SKIP>\\[\\s*SKIP\\s*])"
                        + "|(?<FAIL>\\[\\s*FAIL\\s*])",
                Pattern.CASE_INSENSITIVE
        );

        public SqlMessagePane()
        {
                super(new VCodeArea(new VCodeAreaConfig(true, false)));

                codeArea = getContent();
                codeArea.setEditable(false);
                codeArea.addHighlightingListener(SqlMessagePane::applyHighlighting);

                setupContextMenu();
        }

        private void setupContextMenu()
        {
                MenuItem clearItem = new MenuItem("清空内容");
                clearItem.setOnAction(event -> clearAll());
                codeArea.addContextMenuGroup(clearItem);
        }

        private void clearAll()
        {
                codeArea.replaceText("");
        }

        public static void applyHighlighting(VCodeArea area)
        {
                String text = area.getText();

                Matcher matcher = PATTERN.matcher(text);
                while (matcher.find()) {
                        if (matcher.group("INFO") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "info");
                        } else if (matcher.group("SKIP") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "skip");
                        } else if (matcher.group("FAIL") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "fail");
                        }
                }
        }

        public void appendInfo(String text)
        {
                appendText(strfmt("[  OK  ] %s\n", text));
        }

        public void appendSkip(String text)
        {
                appendText(strfmt("[ SKIP ] %s\n", text));
        }

        public void appendError(String text)
        {
                appendText(strfmt("[ FAIL ] %s\n", text));
        }

        private void appendText(String text)
        {
                codeArea.appendText(text);
                codeArea.moveTo(codeArea.getLength());
                codeArea.requestFollowCaret();
        }

}
