package com.changhong.opendb.navigator.node;

import javafx.scene.control.TreeItem;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */

public abstract class ODBNode extends TreeItem<String>
{
        public ODBNode(String name)
        {
                super(name);
        }

        public abstract String getName();
}
