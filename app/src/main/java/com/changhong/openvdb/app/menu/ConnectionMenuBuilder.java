package com.changhong.openvdb.app.menu;

import com.changhong.openvdb.app.dialog.connection.CreateOrEditConnectionDialog;
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

                MenuItem mysqlItem = new MenuItem("MySQL");
                mysqlItem.setOnAction(e -> openConnectionDialog("MySQL"));

                MenuItem postgreSQLItem = new MenuItem("PostgreSQL");
                postgreSQLItem.setOnAction(e -> openConnectionDialog("PostgreSQL"));

                newConnectionMenu.getItems().addAll(mysqlItem, postgreSQLItem);

                return newConnectionMenu;
        }

        @SuppressWarnings("unused")
        private static void openConnectionDialog(String dbType) {
                new CreateOrEditConnectionDialog(null).showAndWait();
        }
}
