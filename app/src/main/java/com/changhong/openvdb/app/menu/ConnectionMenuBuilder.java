package com.changhong.openvdb.app.menu;

import com.changhong.openvdb.app.dialog.connection.CreateOrEditConnectionDialog;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.DriverType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class ConnectionMenuBuilder
{
        public static Menu buildNewConnectionMenu() {
                Menu newConnectionMenu = new Menu("新建连接");

                MenuItem mysqlItem = new MenuItem(DriverType.MYSQL.getAlias());
                mysqlItem.setOnAction(e -> openConnectionDialog(DriverType.DM));

                MenuItem postgreSQLItem = new MenuItem(DriverType.DM.getAlias());
                postgreSQLItem.setOnAction(e -> openConnectionDialog(DriverType.DM));

                newConnectionMenu.getItems().addAll(mysqlItem, postgreSQLItem);

                return newConnectionMenu;
        }

        @SuppressWarnings("unused")
        private static void openConnectionDialog(DriverType driverType) {
                new CreateOrEditConnectionDialog(driverType).showAndWait();
        }
}
