package com.changhong.opendb.app.ui.widgets.table;

import com.changhong.reflect.UClass;
import com.changhong.reflect.UField;
import com.changhong.utils.Assert;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Setter;

/**
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
public class VFXTableColumnFactory<S>
{
        public interface EditCommitEventListener<S>
        {
                void onCommit(S value);
        }

        @Setter
        private EditCommitEventListener<S> onEditCommitEventListener;

        static class PropertyCellValueRef
        {
                /**
                 * 属性名称
                 */
                private String property;
                /**
                 * 字段反射对象
                 */
                private UField field;
        }

        public VFXTableColumnFactory()
        {
        }

        public <T> VFXTableColumn<S, T> createEditableColumn(String name, String property)
        {
                VFXTableColumn<S, T> c = new VFXTableColumn<>(name, true);

                PropertyCellValueRef ref = new PropertyCellValueRef();
                ref.property = property;

                c.setUserData(ref);

                c.setOnEditCommit(event -> {
                        if (onEditCommitEventListener != null) {
                                var rowValue = event.getRowValue();
                                var col = event.getTableColumn();

                                PropertyCellValueRef propertyCellValueRef = (PropertyCellValueRef) col.getUserData();

                                if (propertyCellValueRef.field == null) {
                                        UClass uClass = new UClass(rowValue.getClass());
                                        propertyCellValueRef.field = uClass.getDeclaredField(propertyCellValueRef.property);
                                        Assert.notNull(propertyCellValueRef.field);
                                }

                                propertyCellValueRef.field.set(rowValue, event.getNewValue());

                                onEditCommitEventListener.onCommit(rowValue);
                        }
                });

                c.setCellValueFactory(new PropertyValueFactory<>(property));

                return c;
        }
}
