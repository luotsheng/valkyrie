package valkyrie.app.dialog.connection;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import valkyrie.app.model.ConnectionPropertyModel;
import valkyrie.app.pane.PropertyGridPane;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
class ConnectionGeneralPane extends PropertyGridPane
{
        private final ConnectionPropertyModel info;

        private final TextField name = new TextField();
        private final TextField host = new TextField();
        private final TextField port = new TextField();
        private final TextField username = new TextField();
        private final PasswordField password = new PasswordField();
        private final CheckBox savePassword = new CheckBox("保存密码");

        public ConnectionGeneralPane(ConnectionPropertyModel info)
        {
                super();
                this.info = info;
                bindModelProperty();
                setupPaneLayout();
        }

        private void bindModelProperty()
        {
                name.textProperty().bindBidirectional(info.nameProperty());
                host.textProperty().bindBidirectional(info.hostProperty());
                port.textProperty().bindBidirectional(info.portProperty());
                password.textProperty().bindBidirectional(info.passwordProperty());
                username.textProperty().bindBidirectional(info.usernameProperty());
                savePassword.selectedProperty().bindBidirectional(info.savePasswordProperty());
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
