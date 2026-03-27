package com.changhong.opendb.navigator.node;

import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.resource.ResourceManager;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNDatabase extends ODBNode
{
        private final DataSourceProvider dataSource;
        private boolean openFlag = false;

        // Menu Items
        private MenuItem openMenuItem;
        private MenuItem closeMenuItem;

        public ODBNDatabase(DataSourceProvider dataSource,
                            String name)
        {
                super(name);
                setGraphic(ResourceManager.use("database1"));
                this.dataSource = dataSource;
        }

        private void openDatabase()
        {
                if (openFlag)
                        return;

                List<String> tables = dataSource.getTables(name);
                for (String table : tables)
                        getChildren().add(new ODBNTable(dataSource, table));

                openFlag = true;
        }

        private void closeDatabase()
        {
                if (!openFlag)
                        return;

                getChildren().clear();

                openFlag = false;
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu menu = new ContextMenu();

                openMenuItem = new MenuItem("打开数据库");
                openMenuItem.setOnAction(event -> openDatabase());

                closeMenuItem = new MenuItem("关闭数据库");
                closeMenuItem.setOnAction(event -> closeDatabase());

                menu.getItems().addAll(openMenuItem, closeMenuItem);

                return menu;
        }

        @Override
        public void showContextMenu(Node node, double x, double y)
        {
                if (openFlag) {
                        openMenuItem.setDisable(true);
                        closeMenuItem.setDisable(false);
                } else {
                        openMenuItem.setDisable(false);
                        closeMenuItem.setDisable(true);
                }

                super.showContextMenu(node, x, y);
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openDatabase();
        }
}
