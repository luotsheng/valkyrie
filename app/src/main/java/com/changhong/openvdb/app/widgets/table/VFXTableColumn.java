package com.changhong.openvdb.app.widgets.table;

import javafx.scene.control.TableColumn;

/**
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
public class VFXTableColumn<S, T> extends TableColumn<S, T>
{
        public VFXTableColumn(String name)
        {
                this(name, false);
        }

        public VFXTableColumn(String name, boolean editable)
        {
                super(name);
                setEditable(editable);
        }
}
