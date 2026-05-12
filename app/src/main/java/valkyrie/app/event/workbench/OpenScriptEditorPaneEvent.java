package valkyrie.app.event.workbench;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import valkyrie.app.assets.Assets;
import valkyrie.app.explorer.UICatalogNode;
import valkyrie.app.explorer.UIConnectionNode;
import valkyrie.app.workbench.ScriptEditor;
import valkyrie.core.model.ScriptFile;

import static valkyrie.utils.string.StaticLibrary.fmt;
import static valkyrie.utils.string.StaticLibrary.strnempty;

/**
 * 打开脚本编辑器
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenScriptEditorPaneEvent extends OpenTabEvent
{
        private final UICatalogNode catalog;
        private final UIConnectionNode connection;
        private final ScriptFile scriptFile;

        public OpenScriptEditorPaneEvent(UICatalogNode owner, UIConnectionNode connection)
        {
                this(owner, connection, null);
        }

        public OpenScriptEditorPaneEvent(UICatalogNode owner, UIConnectionNode connection, ScriptFile scriptFile)
        {
                super(owner);
                this.catalog = owner;
                this.connection = connection;
                this.scriptFile = scriptFile;
        }

        @Override
        public String tabId()
        {
                if (scriptFile != null)
                        return fmt("%s@%s(%s)", scriptFile.getName(), catalog.getName(), connection.getName());
                return null;
        }

        @Override
        public Node createPane(Tab tab)
        {
                tab.setGraphic(Assets.use("sql"));

                ScriptEditor scriptEditor = new ScriptEditor(connection, scriptFile, tab);

                /* tabId 不为空时，重新设置 Tab 标题 */
                if (strnempty(tabId()))
                        tab.setText(tabId());

                return scriptEditor;
        }
}
