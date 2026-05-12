package valkyrie.app.event.workbench;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import valkyrie.app.assets.Assets;
import valkyrie.app.explorer.UICatalogNode;
import valkyrie.app.pane.TableDataPane;
import valkyrie.driver.api.Driver;
import valkyrie.driver.api.Session;
import valkyrie.driver.api.Table;

import static valkyrie.utils.string.StaticLibrary.fmt;

/**
 * 打开数据库表预览面板
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenTableDataPaneEvent extends OpenTabEvent
{
        public Session session;
        public Driver driver;
        public String db;
        public Table table;
        public String conn;

        public OpenTableDataPaneEvent(UICatalogNode catalog, Table table)
        {
                this(catalog, catalog.getSession(), catalog.getDriver(), table,
                        catalog.getConnection().getName(),
                        catalog.getName());
        }

        public OpenTableDataPaneEvent(Object owner, Session session, Driver driver, Table table,
                                      String conn, String db)
        {
                super(owner);
                this.session = session;
                this.driver = driver;
                this.table = table;
                this.conn = conn;
                this.db = db;
        }

        @Override
        public String tabId()
        {
                return fmt("%s@%s(%s)", table.getName(), session.scope(), conn);
        }

        @Override
        public Node createPane(Tab tab)
        {
                tab.setGraphic(Assets.use("table"));
                TableDataPane pane = new TableDataPane(tab, session, driver, table);
                pane.asyncUpdate();
                return pane;
        }
}
