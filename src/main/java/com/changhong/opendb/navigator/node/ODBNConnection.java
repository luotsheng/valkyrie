package com.changhong.opendb.navigator.node;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.driver.datasource.MySQLDataSourceProvider;
import com.changhong.opendb.model.ConnectionInfo;
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
public class ODBNConnection extends ODBNode
{
        private final ConnectionInfo info;
        private boolean openFlag = false;
        private DataSourceProvider dataSource;

        public ODBNConnection(ConnectionInfo info)
        {
                super(info.getName());
                setGraphic(ResourceManager.use("database0"));
                this.info = info;
        }

        private void openConnection()
        {
                if (openFlag)
                        return;

                try {
                        dataSource = new MySQLDataSourceProvider(info);
                        setupDatabases(dataSource.getDatabases());
                        openFlag = true;
                } catch (Exception e) {
                        EventBus.publish(e);
                }
        }

        private void closeConnection()
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

                MenuItem openItem = new MenuItem("打开连接");
                openItem.setOnAction(event -> openConnection());

                MenuItem closeItem = new MenuItem("关闭连接");
                closeItem.setOnAction(event -> closeConnection());

                menu.getItems().addAll(openItem, closeItem);

                return menu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                openConnection();
        }

        private void setupDatabases(List<String> databaseNames)
        {
                for (String name : databaseNames)
                        getChildren().add(new ODBNDatabase(dataSource, name));
        }
}
