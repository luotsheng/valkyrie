package com.changhong.opendb.navigator.node;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.driver.datasource.MySQLDataSourceProvider;
import com.changhong.opendb.model.ConnectionInfo;
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
public class ODBNConnection extends ODBNode
{
        private final ConnectionInfo info;
        private boolean openFlag = false;
        private DataSourceProvider dataSource;

        // Menu Items
        private MenuItem openMenuItem;
        private MenuItem closeMenuItem;

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

                openMenuItem = new MenuItem("打开连接");
                openMenuItem.setOnAction(event -> openConnection());

                closeMenuItem = new MenuItem("关闭连接");
                closeMenuItem.setOnAction(event -> closeConnection());

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
                openConnection();
        }

        private void setupDatabases(List<String> databaseNames)
        {
                for (String name : databaseNames)
                        getChildren().add(new ODBNDatabase(dataSource, name));
        }
}
