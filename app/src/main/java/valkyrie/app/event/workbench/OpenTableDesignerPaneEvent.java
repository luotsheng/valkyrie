package valkyrie.app.event.workbench;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import valkyrie.app.assets.Assets;
import valkyrie.app.pane.TableDesignerPane;
import valkyrie.driver.api.Driver;
import valkyrie.driver.api.Session;
import valkyrie.driver.api.Table;

import static valkyrie.utils.string.StaticLibrary.fmt;

/**
 * 打开设计表面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenTableDesignerPaneEvent extends OpenTabEvent
{
        private final String conn;
        private final Session session;
        private final Driver driver;
        private final Table table;

        public OpenTableDesignerPaneEvent(Object owner, String conn, Session session, Driver driver, Table table)
        {
                super(owner);
                this.conn = conn;
                this.session = session;
                this.driver = driver;
                this.table = table;
        }

        @Override
        public String tabId()
        {
                return fmt("%s@%s(%s)", table.getName(), session.scope(), conn);
        }

        @Override
        public Node createPane(Tab tab)
        {
                TableDesignerPane pane = new TableDesignerPane(tab, session, driver, table);
                tab.setGraphic(Assets.use("struct1"));
                return pane;
        }
}
