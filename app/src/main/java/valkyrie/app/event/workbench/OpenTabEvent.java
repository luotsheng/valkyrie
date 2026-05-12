package valkyrie.app.event.workbench;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import valkyrie.app.event.bus.Event;

/**
 * 在工作区打开新的标签页
 *
 * @author Luo Tiansheng
 * @since 2026/4/14
 */
public abstract class OpenTabEvent extends Event
{
        private final Object owner;

        public OpenTabEvent(Object owner)
        {
                this.owner = owner;
        }

        /**
         * 所属对象（表示是谁打开的面板）
         */
        public Object owner()
        {
                return owner;
        }

        /**
         * Tab id
         */
        public abstract String tabId();

        /**
         * 创建对应面板
         */
        public abstract Node createPane(Tab tab);
}
