package com.changhong.opendb.ui.widgets;

import com.changhong.opendb.ui.workbench.ModifyCell;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
public class EditingTableCell<S> extends TextFieldTableCell<S, String>
{
        /**
         * 文本编辑器
         */
        private TextField tf;

        private boolean isCommit = false;

        /**
         * 开始编辑前记录旧值
         */
        private String oldValue;

        public interface ModifyListener
        {
                void modify(ModifyCell cell);
        }

        private final ModifyListener modifyListener;

        public EditingTableCell(ModifyListener modifyListener)
        {
                super(new DefaultStringConverter());
                this.modifyListener = modifyListener;
        }

        @Override
        @SuppressWarnings("CssDeprecatedValue")
        public void updateItem(String item, boolean empty)
        {
                super.updateItem(item, empty);

                if (modifyListener != null && isCommit) {
                        int colIndex = getTableView().getColumns().indexOf(getTableColumn());
                        int rowIndex = getTableRow().getIndex();
                        modifyListener.modify(new ModifyCell(colIndex, rowIndex, oldValue, item));
                        isCommit = false;
                }

                if (empty) {
                        setText(null);
                        setStyle("");
                        return;
                }

                if (item == null) {
                        setText("(NULL)");
                        setStyle("-fx-text-fill: gray;");
                        return;
                }

                if (item.isEmpty()) {
                        setText("(EMPY)");
                        setStyle("-fx-text-fill: gray;");
                        return;
                }

                setText(item);
                setStyle("");
        }

        @Override
        public void startEdit()
        {
                oldValue = getItem();

                if (!isEmpty()) {
                        createTextField();

                        /*
                         * startEdit() 会在父类中创建一个相同的 TextField 对象，该 Field 对象
                         * 会在 UI 上将我们创建 tf 对象替换。所以会导致命名单元格有值，但编辑却为
                         * 空值的问题。
                         *
                         * 所以需要延迟在 createTextField() 之后调用。并在调用后清空 Graphic。
                         */
                        super.startEdit();

                        setText(null);
                        setGraphic(tf);

                        Platform.runLater(() -> {
                                tf.requestFocus();
                                tf.selectAll();
                        });
                }
        }

        @Override
        public void cancelEdit()
        {
                tf.setText(oldValue);
                setGraphic(null);
                updateItem(getItem(), false);
        }

        @Override
        public void commitEdit(String newValue)
        {
                isCommit = true;
                super.commitEdit(newValue);
        }

        private void createTextField()
        {
                tf = new TextField(getString());

                tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal)
                                commitEdit(getConverter().fromString(tf.getText()));
                });

                tf.setOnAction(event -> {
                        commitEdit(tf.getText());
                });
        }

        private String getString()
        {
                return getItem() == null ? "" : getConverter().toString(getItem());
        }
}
