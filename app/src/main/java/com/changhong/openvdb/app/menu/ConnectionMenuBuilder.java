package com.changhong.openvdb.app.menu;

import com.changhong.openvdb.app.dialog.connection.JdbcCreateConnectionDialog;
import com.changhong.openvdb.driver.api.DbType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import javax.naming.Context;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class ConnectionMenuBuilder
{
        public static Menu buildMenu() {
                Menu newConnectionMenu = new Menu("新建连接");

                MenuItem mysqlItem = new MenuItem(DbType.mysql.getAlias());
                mysqlItem.setOnAction(e -> openConnectionDialog(DbType.mysql));

                MenuItem postgreSQLItem = new MenuItem(DbType.dm.getAlias());
                postgreSQLItem.setOnAction(e -> openConnectionDialog(DbType.dm));

                newConnectionMenu.getItems().addAll(mysqlItem, postgreSQLItem);

                return newConnectionMenu;
        }

        public static ContextMenu buildContextMenu() {
                ContextMenu contextMenu = new ContextMenu();
                contextMenu.getItems().addAll(buildMenu().getItems());
                return contextMenu;
        }

        @SuppressWarnings("unused")
        private static void openConnectionDialog(DbType dbType) {
                new JdbcCreateConnectionDialog(dbType).showAndWait();
        }
}
