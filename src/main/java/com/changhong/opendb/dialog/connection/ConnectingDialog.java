package com.changhong.opendb.dialog.connection;

import com.changhong.opendb.Users;
import com.changhong.opendb.model.ConnectionModel;
import com.changhong.opendb.utils.JSONUtils;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class ConnectingDialog extends Stage
{
        private TabPane tabPane;
        private HBox buttonBar;
        private final ConnectionModel model = new ConnectionModel("mysql");

        private static final int WW = 650;
        private static final int WH = 500;

        public ConnectingDialog()
        {
                setupTabPane();
                setupButtonBar();
                setupScene();
        }

        private void setupTabPane()
        {
                tabPane = new TabPane();

                Tab generalTab = new Tab("常规属性");
                generalTab.setClosable(false);
                generalTab.setContent(new ConnectingGeneralPane(model));

                Tab advanceTab = new Tab("高级属性");
                advanceTab.setClosable(false);
                advanceTab.setContent(new ConnectingAdvancePane(model));

                tabPane.getTabs().addAll(generalTab, advanceTab);
        }

        private void setupButtonBar()
        {
                Button test = new Button("测试链接");
                test.setOnAction(event -> testConnection());

                Button save = new Button("保存");
                save.setOnAction(event -> saveConnection());

                Button cancel = new Button("取消");
                cancel.setOnAction(event -> close());

                buttonBar = new HBox(10, test, save, cancel);
                buttonBar.setAlignment(Pos.CENTER_RIGHT);
                buttonBar.setPadding(new Insets(10, 10, 10, 10));
        }

        private void setupScene()
        {
                setTitle("新增连接");

                VBox vbox = new VBox(10, tabPane, buttonBar);
                VBox.setVgrow(tabPane, Priority.ALWAYS);

                Scene scene = new Scene(vbox, WW, WH);
                setScene(scene);
        }

        public void testConnection()
        {

        }

        private void saveConnection()
        {
                String content = JSONUtils.toJSONString(model, SerializationFeature.INDENT_OUTPUT);
                Users.saveConnection(model.getName(), content);
                close();
        }
}
