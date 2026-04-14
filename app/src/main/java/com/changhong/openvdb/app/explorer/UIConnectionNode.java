package com.changhong.openvdb.app.explorer;

import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.dialog.connection.CreateOrEditConnectionDialog;
import com.changhong.openvdb.app.model.ConnectionPropertyModel;
import com.changhong.openvdb.app.model.UINodeGlobalStatus;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
import com.changhong.openvdb.core.repository.ConnectionRepository;
import com.changhong.openvdb.driver.api.ConnectionConfig;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.DriverType;
import com.changhong.openvdb.driver.api.PooledDataSource;
import com.changhong.openvdb.driver.dm.DMDriver;
import com.changhong.openvdb.driver.mysql.MySQLDriver;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class UIConnectionNode extends UIExplorerNode
{
        @Getter
        private final ConnectionPropertyModel propertyModel;

        private boolean openFlag = false;
        private PooledDataSource dataSource;

        @Getter
        private Driver driver;

        @Getter
        private DriverType driverType;

        // Menu Items
        private MenuItem openOrCloseMenuItem;
        private MenuItem editMenuItem;
        private MenuItem deleteMenuItem;

        @Getter
        private final List<UICatalogNode> catalogs = new ArrayList<>();

        @Setter
        @Getter
        private UICatalogNode selectedDatabase;

        @Setter
        private DeleteRequestListener deleteRequestListener;

        public interface DeleteRequestListener {
                void onDeleteRequest(UIConnectionNode node);
        }

        public UIConnectionNode(ConnectionPropertyModel propertyModel)
        {
                super(propertyModel.getName());

                this.driverType = DriverType.toDriverType(propertyModel.getType());

                ImageView graphic = switch (this.driverType) {
                        case MYSQL -> Assets.use("mysql");
                        case DM -> Assets.use("dm2");
                };

                setGraphic(graphic);
                this.propertyModel = propertyModel;
                setupListenerEvent();
        }

        private void createDriver()
        {
                ConnectionConfig config = propertyModel.toConnectionConfig();
                dataSource = new PooledDataSource(config);

                driver = switch (config.getType()) {
                        case MYSQL -> new MySQLDriver(dataSource);
                        case DM -> new DMDriver(dataSource);
                };
        }

        public void openConnection()
        {
                if (openFlag)
                        return;

                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                createDriver();
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
                        if (db instanceof UICatalogNode dbNode)
                                dbNode.closeDatabase();
                });

                catalogs.clear();
                getChildren().clear();
                VFXDialogHelper.runWith(dataSource::close);
                UINodeGlobalStatus.getInstance().removeConnection(this);
                selectedDatabase = null;

                openFlag = false;
        }

        private void editConnection()
        {
                if (openFlag) {
                        if (VFXDialogHelper.ask("编辑需要关闭当前连接，是否关闭？")) {
                                closeConnection();
                                new CreateOrEditConnectionDialog(propertyModel).showAndWait();
                        }
                } else {
                        new CreateOrEditConnectionDialog(propertyModel).showAndWait();
                }
        }

        private void deleteConnection()
        {
                if (VFXDialogHelper.askDangerous("确定要删除“%s”吗？", getName())) {
                        deleteRequestListener.onDeleteRequest(this);
                        ConnectionRepository.deleteConnection(getName());
                }
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu contextMenu = new ContextMenu();

                openOrCloseMenuItem = new MenuItem();

                editMenuItem = new MenuItem("编辑连接");
                editMenuItem.setOnAction(event -> editConnection());

                deleteMenuItem = new MenuItem("删除链接");
                deleteMenuItem.setOnAction(event -> deleteConnection());

                contextMenu.getItems().addAll(
                        openOrCloseMenuItem,
                        new SeparatorMenuItem(),
                        editMenuItem,
                        new SeparatorMenuItem(),
                        deleteMenuItem
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
        public void onSelectedEvent(UIExplorerNode node)
        {
                UINodeGlobalStatus.getInstance().selectedConnection(this);
        }

        private void setupListenerEvent()
        {
                setMouseDoubleClickEvent(event -> openConnection());
        }

        private void setupDatabases(List<String> databaseNames)
        {
                for (String name : databaseNames)
                        catalogs.add(new UICatalogNode(this, driver, name));
                getChildren().addAll(catalogs);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isOpen()
        {
                return openFlag;
        }
}
