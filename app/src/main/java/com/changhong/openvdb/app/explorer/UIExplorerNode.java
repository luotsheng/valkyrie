package com.changhong.openvdb.app.explorer;

import com.changhong.openvdb.app.assets.Assets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */

public abstract class UIExplorerNode extends TreeItem<String>
{
        /**
         *  节点名称
         */
        @Getter
        protected final String name;

        /**
         * 节点菜单
         * -- SETTER --
         *  设置菜单

         */
        @Setter
        @Getter
        protected ContextMenu contextMenu;

        private Node oldGraphic;

        public interface MouseDoubleClickEvent
        {
                void call(MouseEvent event);
        }

        public interface SelectedEvent
        {
                void call(UIExplorerNode node);
        }

        @Setter
        private MouseDoubleClickEvent mouseDoubleClickEvent;
        @Setter
        private SelectedEvent selectedEvent;

        public UIExplorerNode(String name)
        {
                super(name);
                this.name = name;
                this.contextMenu = registerContextMenu();
        }

        public abstract ImageView getIcon();

        protected void setLoadingIndicator()
        {
                oldGraphic = getGraphic();
                setGraphic(Assets.newProgressIndicator());
        }

        protected void removeLoadingIndicator()
        {
                setGraphic(oldGraphic);
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
         * 触发鼠标双击事件
         */
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                if (mouseDoubleClickEvent != null)
                        mouseDoubleClickEvent.call(event);
        }

        /**
         * 触发节点选中事件
         */
        public void onSelectedEvent(UIExplorerNode node)
        {
                if (selectedEvent != null)
                        selectedEvent.call(node);
        }

}
