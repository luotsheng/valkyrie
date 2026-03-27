package com.changhong.opendb.ui.navigator.node;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */

public abstract class ODBNode extends TreeItem<String>
{
        /**
         *  节点名称
         */
        @Getter
        protected final String name;

        /**
         * 节点菜单
         */
        private final ContextMenu contextMenu;

        public ODBNode(String name)
        {
                super(name);
                this.name = name;
                this.contextMenu = registerContextMenu();
        }

        /**
         * 子类实现，注册节点专属菜单
         */
        protected ContextMenu registerContextMenu()
        {
                return null;
        }

        /**
         * 显示菜单
         */
        public void showContextMenu(Node node, double x, double y)
        {
                if (contextMenu == null)
                        return;

                contextMenu.show(node, x, y);
        }

        /**
         * 鼠标双击事件
         */
        public abstract void onMouseDoubleClickEvent(MouseEvent event);

        /**
         * 节点选中事件
         */
        public abstract void onSelectedEvent();

}
