package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.ui.widgets.VCodeArea;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luo Tiansheng
 * @since 2026/4/2
 */
public class SqlMessagePane extends VirtualizedScrollPane<VCodeArea>
{
        private final VCodeArea codeArea;

        static final Pattern PATTERN = Pattern.compile(
                "(?<INFO>\\[\\s*OK\\s*])"
                        + "|(?<FAIL>\\[\\s*FAIL\\s*])",
                Pattern.CASE_INSENSITIVE
        );

        public SqlMessagePane()
        {
                super(new VCodeArea());
                codeArea = getContent();
                codeArea.setEditable(false);
                codeArea.addHighlightingListener(SqlMessagePane::applyHighlighting);
        }

        public static void applyHighlighting(VCodeArea area)
        {
                String text = area.getText();

                Matcher matcher = PATTERN.matcher(text);
                while (matcher.find()) {
                        if (matcher.group("INFO") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "info");
                        } else if (matcher.group("FAIL") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "fail");
                        }
                }
        }

        public void appendInfo(String text)
        {
                codeArea.appendText("[  OK  ] " + text + "\n");
        }

        public void appendError(String text)
        {
                codeArea.appendText("[ FAIL ] " + text + "\n");
        }
}
