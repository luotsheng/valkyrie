package com.changhong.openvdb.app.explorer;

import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.dialog.connection.JdbcCreateConnectionDialog;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.workbench.ConnectionOpenedNotifyEvent;
import com.changhong.openvdb.app.model.ConnectionPropertyModel;
import com.changhong.openvdb.app.model.UIExplorerStatus;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
import com.changhong.openvdb.core.repository.ConnectionRepository;
import com.changhong.openvdb.driver.api.ConnectionConfig;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.DbType;
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

import static com.changhong.utils.io.IOUtils.printf;

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
        private boolean cancelFlag = false;
        private Thread connecetThread = null;
        private PooledDataSource dataSource;

        @Getter
        private Driver driver;

        @Getter
        private DbType dbType;

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

                this.dbType = DbType.of(propertyModel.getType());

                setGraphic(getIcon());
                this.propertyModel = propertyModel;
                setupListenerEvent();
        }

        @Override
        public ImageView getIcon()
        {
                return switch (this.dbType) {
                        case mysql -> Assets.use("mysql");
                        case dm -> Assets.use("dm2");
                };
        }

        private void createDriver()
        {
                ConnectionConfig config = propertyModel.toConnectionConfig();
                dataSource = new PooledDataSource(config);

                driver = switch (config.getType()) {
                        case mysql -> new MySQLDriver(dataSource);
                        case dm -> new DMDriver(dataSource);
                };
        }

        public void openConnection()
        {
                if (openFlag)
                        return;

                cancelFlag = false;

                setLoadingIndicator();

                connecetThread = new Thread(() -> {
                        try {
                                createDriver();
                                setupDatabases(driver.getCatalogs());
                                setExpanded(true);
                                openFlag = true;
                                UIExplorerStatus.getInstance().selectedConnection(this);
                                EventBus.publish(new ConnectionOpenedNotifyEvent(this));
                        } catch (Throwable e) {
                                if (!cancelFlag)
                                        Platform.runLater(() -> VFXDialogHelper.alert(e));
                        } finally {
                                connecetThread = null;
                                if (!cancelFlag)
                                        Platform.runLater(this::removeLoadingIndicator);
                        }
                });

                connecetThread.start();
        }

        public void closeConnection()
        {
                /* cancel */
                if (connecetThread != null) {
                        cancelFlag = true;
                        connecetThread.interrupt();
                        connecetThread = null;
                        removeLoadingIndicator();
                        return;
                }

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
                UIExplorerStatus.getInstance().unselectConnection(this);
                selectedDatabase = null;

                openFlag = false;
        }

        private void editConnection()
        {
                if (openFlag) {
                        if (VFXDialogHelper.ask("编辑需要关闭当前连接，是否关闭？")) {
                                closeConnection();
                                new JdbcCreateConnectionDialog(propertyModel).showAndWait();
                        }
                } else {
                        new JdbcCreateConnectionDialog(propertyModel).showAndWait();
                }
        }

        private void deleteConnection()
        {
                if (VFXDialogHelper.askDangerous("确定要删除“%s”吗？", getName())) {
                        deleteRequestListener.onDeleteRequest(this);
                        ConnectionRepository.deleteConnection(getName());
                        UIExplorerStatus.getInstance().removeConnection(this);
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
                if (openFlag || connecetThread != null) {
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
                if (openFlag)
                        UIExplorerStatus.getInstance().selectedConnection(this);
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
