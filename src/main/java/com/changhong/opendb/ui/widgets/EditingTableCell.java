package com.changhong.opendb.ui.widgets;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.util.Objects;

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

        /**
         * 开始编辑前记录旧值
         */
        private String oldValue;

        public EditingTableCell()
        {
                super(new DefaultStringConverter());
        }

        @Override
        @SuppressWarnings("CssDeprecatedValue")
        public void updateItem(String item, boolean empty)
        {
                System.out.println("updateItem");
                System.out.printf("  旧值：%s\n", oldValue);
                System.out.printf("  新值：%s\n", item);

                super.updateItem(item, empty);

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
                System.out.printf("startEdit: %s\n", oldValue);

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
                System.out.println("cancelEdit");
                tf.setText(oldValue);
                setGraphic(null);
                updateItem(getItem(), false);
        }

        @Override
        public void commitEdit(String newValue)
        {
                System.out.printf("commitEdit: %s\n", newValue);
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
