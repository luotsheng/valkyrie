package com.changhong.opendb.dialog.connect;

import com.changhong.opendb.model.ConnectionModel;
import com.changhong.opendb.widgets.PropertyGridPane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
class ConnectGeneralPane extends PropertyGridPane
{
        private final ConnectionModel model;

        private final TextField name = new TextField();
        private final TextField host = new TextField();
        private final TextField port = new TextField();
        private final TextField username = new TextField();
        private final PasswordField password = new PasswordField();
        private final CheckBox savePassword = new CheckBox("保存密码");

        public ConnectGeneralPane(ConnectionModel model)
        {
                super();
                this.model = model;
                bindModelProperty();
                setupPaneLayout();
        }

        private void bindModelProperty()
        {
                name.textProperty().bindBidirectional(model.nameProperty());
                host.textProperty().bindBidirectional(model.hostProperty());
                port.textProperty().bindBidirectional(model.portProperty());
                username.textProperty().bindBidirectional(model.usernameProperty());
        }

        private void setupPaneLayout()
        {
                addRow("连接名称：", name);
                addRow(null, new Label()); /* separator */
                addRow("主机地址：", host);
                addRow("端口号：", port);
                addRow("用户名：", username);
                addRow("密码：", password);
                addRow(null, new Label()); /* separator */
                addRow(null, savePassword);
        }

}
