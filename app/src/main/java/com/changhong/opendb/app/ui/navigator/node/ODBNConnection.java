package com.changhong.opendb.app.ui.navigator.node;

import com.changhong.driver.api.Driver;
import com.changhong.driver.api.PooledDataSource;
import com.changhong.driver.mysql.MySQLDriver;
import com.changhong.opendb.app.model.ConnectionProperty;
import com.changhong.opendb.app.model.ODBNStatus;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.dialog.connection.ConnectionDialog;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialogHelper;
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
        private final ConnectionProperty info;

        private boolean openFlag = false;
        private PooledDataSource dataSource;

        @Getter
        private Driver driver;

        // Menu Items
        private MenuItem openOrCloseMenuItem;
        private MenuItem editMenuItem;

        @Getter
        private final List<ODBNDatabase> databases = new ArrayList<>();

        @Setter
        @Getter
        private ODBNDatabase selectedDatabase;

        public ODBNConnection(ConnectionProperty info)
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
                                dataSource = new PooledDataSource(info.toConnectionConfig());
                                driver = new MySQLDriver(dataSource);
                                setupDatabases(driver.getCatalogs());
                                setExpanded(true);
                                openFlag = true;
                        } catch (Throwable e) {
                                Platform.runLater(() -> VFXDialogHelper.alert(e));
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
                VFXDialogHelper.runWith(dataSource::close);
                ODBNStatus.getInstance().removeConnection(this);

                openFlag = false;
        }

        private void editConnection()
        {
                if (openFlag) {
                        if (VFXDialogHelper.ask("编辑需要关闭当前连接，是否关闭？")) {
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
                        databases.add(new ODBNDatabase(this, driver, name));
                getChildren().addAll(databases);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isOpen()
        {
                return openFlag;
        }
}
