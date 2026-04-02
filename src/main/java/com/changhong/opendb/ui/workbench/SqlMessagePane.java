package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.ui.widgets.VCodeArea;
import com.changhong.opendb.ui.widgets.VCodeAreaConfig;
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
                codeArea.appendText(strfmt("[  OK  ] %s\n", text));
        }

        public void appendSkip(String text)
        {
                codeArea.appendText(strfmt("[ SKIP ] %s\n", text));
        }

        public void appendError(String text)
        {
                codeArea.appendText(strfmt("[ FAIL ] %s\n", text));
        }

}
