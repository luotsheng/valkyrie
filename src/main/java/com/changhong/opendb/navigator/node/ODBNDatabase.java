package com.changhong.opendb.navigator.node;

import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.resource.ResourceManager;
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

                MenuItem openItem = new MenuItem("打开数据库");
                openItem.setOnAction(event -> openDatabase());

                MenuItem closeItem = new MenuItem("关闭数据库");
                closeItem.setOnAction(event -> closeDatabase());

                menu.getItems().addAll(openItem, closeItem);

                return menu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openDatabase();
        }
}
