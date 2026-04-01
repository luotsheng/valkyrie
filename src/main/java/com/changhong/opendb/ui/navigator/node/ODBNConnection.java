package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.datasource.DataSourceProxy;
import com.changhong.opendb.driver.datasource.MySQLDataSourceProxy;
import com.changhong.opendb.model.ConnectionInfo;
import com.changhong.opendb.model.ODBNStatus;
import com.changhong.opendb.resource.Assets;
import com.changhong.opendb.ui.dialog.connection.ConnectionDialog;
import com.changhong.opendb.ui.widgets.ConfirmDialog;
import com.changhong.opendb.utils.Catcher;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNConnection extends ODBNode
{
        @Getter
        private final ConnectionInfo info;

        private boolean openFlag = false;
        private DataSourceProxy dataSource;

        @Getter
        private JdbcTemplate jdbcTemplate;

        // Menu Items
        private MenuItem openOrCloseMenuItem;
        private MenuItem editMenuItem;

        @Getter
        private final List<ODBNDatabase> databases = new ArrayList<>();

        @Setter
        @Getter
        private ODBNDatabase selectedDatabase;

        public ODBNConnection(ConnectionInfo info)
        {
                super(info.getName());
                setGraphic(Assets.use("database0"));
                this.info = info;
                setupListenerEvent();
        }

        public void openConnection()
        {
                if (openFlag)
                        return;

                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                dataSource = new MySQLDataSourceProxy(info);
                                jdbcTemplate = dataSource.newJdbcTemplate(getName());
                                setupDatabases(jdbcTemplate.getDatabases());
                                setExpanded(true);
                                openFlag = true;
                        } catch (Throwable e) {
                                Platform.runLater(() -> EventBus.publish(e));
                        } finally {
                                Platform.runLater(this::removeLoadingIndicator);
                        }
                }).start();
        }

        public void closeConnection()
        {
                if (!openFlag)
                        return;

                setExpanded(false);

                getChildren().forEach(db -> {
                        if (db instanceof ODBNDatabase odb)
                                odb.closeDatabase();
                });

                databases.clear();
                getChildren().clear();
                Catcher.tryCall(dataSource::close);

                openFlag = false;
        }

        private void editConnection()
        {
                if (openFlag) {
                        if (ConfirmDialog.showDialog("编辑需要关闭当前连接，是否关闭？")) {
                                closeConnection();
                                new ConnectionDialog(info).showAndWait();
                        }
                } else {
                        new ConnectionDialog(info).showAndWait();
                }
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu contextMenu = new ContextMenu();

                openOrCloseMenuItem = new MenuItem();

                editMenuItem = new MenuItem("编辑连接");
                editMenuItem.setOnAction(event -> editConnection());

                contextMenu.getItems().addAll(
                        openOrCloseMenuItem,
                        new SeparatorMenuItem(),
                        editMenuItem
                );

                return contextMenu;
        }

        @Override
        public void showContextMenu(Node node, double x, double y)
        {
                if (openFlag) {
                        openOrCloseMenuItem.setText("关闭连接");
                        openOrCloseMenuItem.setOnAction(event -> closeConnection());
                } else {
                        openOrCloseMenuItem.setText("打开连接");
                        openOrCloseMenuItem.setOnAction(event -> openConnection());
                }

                super.showContextMenu(node, x, y);
        }

        @Override
        public void onSelectedEvent()
        {
                ODBNStatus.getInstance().selectedConnection(this);
        }

        private void setupListenerEvent()
        {
                setMouseDoubleClickEvent(event -> openConnection());
        }

        private void setupDatabases(List<String> databaseNames)
        {
                for (String name : databaseNames)
                        databases.add(new ODBNDatabase(this, jdbcTemplate, name));
                getChildren().addAll(databases);
        }

        public boolean isOpen()
        {
                return openFlag;
        }
}
