package com.changhong.openvdb.app.ui.widgets.table.cell;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;

import java.util.Collection;

/**
 * @author Luo Tiansheng
 * @since 2026/4/4
 */
public class VFXComboBoxTableCell<S, T> extends TableCell<S, T>
{
        private final ComboBox<T> comboBox;

        public VFXComboBoxTableCell(Collection<T> items)
        {
                comboBox = new ComboBox<>(FXCollections.observableArrayList(items));

                comboBox.setOnAction(e -> {
                        T newVal = comboBox.getSelectionModel().getSelectedItem();
                        startEdit();
                        commitEdit(newVal);
                });

                comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null) {
                                commitEdit(newVal);
                        }
                });

                widthProperty().addListener((obs, oldVal, newVal) -> {
                        comboBox.setPrefWidth(newVal.doubleValue());
                });
        }

        @Override
        protected void updateItem(T item, boolean empty)
        {
                super.updateItem(item, empty);

                if (empty) {
                        setGraphic(null);
                }
                else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                }
        }
}
