package com.changhong.openvdb.app.ui.navigator.node;

import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.PooledDataSource;
import com.changhong.openvdb.driver.mysql.MySQLDriver;
import com.changhong.openvdb.app.model.ConnectionPropertyModel;
import com.changhong.openvdb.app.model.VDBNodeStatus;
import com.changhong.openvdb.app.resource.Assets;
import com.changhong.openvdb.app.ui.dialog.connection.ConnectionDialog;
import com.changhong.openvdb.app.ui.navigator.VDBNode;
import com.changhong.openvdb.app.ui.widgets.dialog.VFXDialogHelper;
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
public class VDBConnectionNode extends VDBNode
{
        @Getter
        private final ConnectionPropertyModel propertyModel;

        private boolean openFlag = false;
        private PooledDataSource dataSource;

        @Getter
        private Driver driver;

        // Menu Items
        private MenuItem openOrCloseMenuItem;
        private MenuItem editMenuItem;

        @Getter
        private final List<VDBDatabaseNode> databases = new ArrayList<>();

        @Setter
        @Getter
        private VDBDatabaseNode selectedDatabase;

        public VDBConnectionNode(ConnectionPropertyModel propertyModel)
        {
                super(propertyModel.getName());
                setGraphic(Assets.use("database0"));
                this.propertyModel = propertyModel;
                setupListenerEvent();
        }

        public void openConnection()
        {
                if (openFlag)
                        return;

                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                dataSource = new PooledDataSource(propertyModel.toConnectionConfig());
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
                        if (db instanceof VDBDatabaseNode vdb)
                                vdb.closeDatabase();
                });

                databases.clear();
                getChildren().clear();
                VFXDialogHelper.runWith(dataSource::close);
                VDBNodeStatus.getInstance().removeConnection(this);

                openFlag = false;
        }

        private void editConnection()
        {
                if (openFlag) {
                        if (VFXDialogHelper.ask("编辑需要关闭当前连接，是否关闭？")) {
                                closeConnection();
                                new ConnectionDialog(propertyModel).showAndWait();
                        }
                } else {
                        new ConnectionDialog(propertyModel).showAndWait();
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
        public void onSelectedEvent(VDBNode node)
        {
                VDBNodeStatus.getInstance().selectedConnection(this);
        }

        private void setupListenerEvent()
        {
                setMouseDoubleClickEvent(event -> openConnection());
        }

        private void setupDatabases(List<String> databaseNames)
        {
                for (String name : databaseNames)
                        databases.add(new VDBDatabaseNode(this, driver, name));
                getChildren().addAll(databases);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isOpen()
        {
                return openFlag;
        }
}
