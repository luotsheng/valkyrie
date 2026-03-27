package com.changhong.opendb.ui.menu;

import com.changhong.opendb.ui.dialog.connection.ConnectionDialog;
import com.changhong.opendb.utils.OS;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class AppMenuBar extends MenuBar
{
        public AppMenuBar()
        {
                if (OS.isMac()) {
                        setUseSystemMenuBar(true);
                        setMinHeight(0);
                        setMaxHeight(0);
                        setMouseTransparent(true);
                }

                // 文件菜单
                Menu fileMenu = new Menu("文件");

                Menu newConnectionMenu = new Menu("新建连接");
                MenuItem mysqlItem =  new MenuItem("MySQL");
                MenuItem postgreSQLItem =  new MenuItem("PostgreSQL");
                newConnectionMenu.getItems().addAll(
                        mysqlItem,
                        postgreSQLItem
                );

                /* bind event */
                mysqlItem.setOnAction(event -> new ConnectionDialog().showAndWait());

                MenuItem openItem = new MenuItem("打开");
                MenuItem exitItem = new MenuItem("退出");
                fileMenu.getItems().addAll(newConnectionMenu, openItem, new SeparatorMenuItem(), exitItem);

                // 编辑菜单
                Menu editMenu = new Menu("编辑");
                MenuItem copyItem = new MenuItem("复制");
                MenuItem pasteItem = new MenuItem("粘贴");
                editMenu.getItems().addAll(copyItem, pasteItem);

                // 帮助菜单
                Menu helpMenu = new Menu("帮助");
                MenuItem aboutItem = new MenuItem("关于");
                helpMenu.getItems().add(aboutItem);

                getMenus().addAll(fileMenu, editMenu, helpMenu);
        }
}
