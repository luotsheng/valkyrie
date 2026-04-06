package com.changhong.opendb.app.ui.widgets;

import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * @author Luo Tiansheng
 * @since 2026/4/4
 */
public class VFXCheckBoxTableCell<S> extends CheckBoxTableCell<S, Boolean>
{
        private final CheckBox checkBox = new CheckBox();

        @Override
        public void updateItem(Boolean item, boolean empty)
        {
                super.updateItem(item, empty);

                if (empty) {
                        setGraphic(null);
                        return;
                }

                checkBox.setSelected(item != null ? item : false);
                setGraphic(checkBox);
        }
}
