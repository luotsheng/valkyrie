package com.changhong.openvdb.app.widgets;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 * @author Luo Tiansheng
 * @since 2026/4/9
 */
public class VFXComboBox<T> extends ComboBox<T>
{
        public VFXComboBox()
        {
        }

        public VFXComboBox(ObservableList<T> items)
        {
                super(items);
        }

        public VFXComboBox<T> copyComboBox()
        {
                VFXComboBox<T> dst = new VFXComboBox<>();

                dst.getItems().addAll(this.getItems());
                dst.getSelectionModel().select(
                        this.getSelectionModel().getSelectedIndex());

                return dst;
        }
}
