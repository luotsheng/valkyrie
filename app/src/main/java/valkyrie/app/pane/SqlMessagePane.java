package valkyrie.app.pane;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import static valkyrie.utils.string.StaticLibrary.fmt;

/**
 * @author Luo Tiansheng
 * @since 2026/4/2
 */
public class SqlMessagePane extends VirtualizedScrollPane<CodeArea>
{
        private final CodeArea codeArea;

        public SqlMessagePane()
        {
                super(new CodeArea());

                codeArea = getContent();
                codeArea.setEditable(false);

                setupContextMenu();
        }

        private void setupContextMenu()
        {

        }

        private void clearAll()
        {
                codeArea.replaceText("");
        }

        public void appendInfo(String text)
        {
                appendText(fmt("[  OK  ] %s\n", text));
        }

        public void appendSkip(String text)
        {
                appendText(fmt("[ SKIP ] %s\n", text));
        }

        public void appendError(String text)
        {
                appendText(fmt("[ FAIL ] %s\n", text));
        }

        private void appendText(String text)
        {
                codeArea.appendText(text);
                codeArea.moveTo(codeArea.getLength());
                codeArea.requestFollowCaret();
        }

}
