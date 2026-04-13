package com.changhong.openvdb.app.ui.widgets.table;

import com.changhong.bean.BeanUtils;
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
                void onCommit(S oldVal, S newVal);
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
                 * 类反射对象
                 */
                private UClass uClass;
                /**
                 * 字段反射对象
                 */
                private UField field;
        }

        public VFXTableColumnFactory()
        {
        }

        @SuppressWarnings("unchecked")
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
                                        propertyCellValueRef.uClass = uClass;
                                        propertyCellValueRef.field = uClass.getDeclaredField(propertyCellValueRef.property);
                                        Assert.notNull(propertyCellValueRef.field);
                                }

                                var oldVal = (S) propertyCellValueRef.uClass.newInstance();
                                BeanUtils.directCopy(rowValue, oldVal);

                                propertyCellValueRef.field.set(rowValue, event.getNewValue());

                                onEditCommitEventListener.onCommit(oldVal, rowValue);
                        }
                });

                c.setCellValueFactory(new PropertyValueFactory<>(property));

                return c;
        }
}
