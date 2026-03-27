package com.changhong.opendb.ui.dialog.connection;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.RefreshConnectionEvent;
import com.changhong.opendb.driver.datasource.MySQLDataSourceProvider;
import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.model.ConnectionInfo;
import com.changhong.opendb.repository.ConnectionRepository;
import com.changhong.opendb.utils.JSONUtils;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class ConnectionDialog extends Stage
{
        private TabPane tabPane;
        private HBox buttonBar;
        private final ConnectionInfo info = new ConnectionInfo("mysql");
        private final Label status = new Label();

        private static final int WW = 650;
        private static final int WH = 500;

        public ConnectionDialog()
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
                generalTab.setContent(new ConnectionGeneralPane(info));

                Tab advanceTab = new Tab("高级属性");
                advanceTab.setClosable(false);
                advanceTab.setContent(new ConnectionAdvancedPane(info));

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

                HBox statusBar = new HBox(status);
                statusBar.setPadding(new Insets(20, 0, 0, 20));
                statusBar.setAlignment(Pos.BOTTOM_LEFT);

                VBox vbox = new VBox(10, tabPane, statusBar, buttonBar);
                VBox.setVgrow(tabPane, Priority.ALWAYS);

                Scene scene = new Scene(vbox, WW, WH);
                setScene(scene);
        }

        @SuppressWarnings({
                "unused"
        })
        public void testConnection()
        {
                try (DataSourceProvider dataSource = new MySQLDataSourceProvider(info);
                     Connection connection = dataSource.getConnection()) {
                        status.setText("Connected successfully...");
                        status.setStyle("-fx-text-fill: #28a745;");
                } catch (Exception e) {
                        status.setText(e.getCause().getMessage());
                        status.setStyle("-fx-text-fill: #b8312b;");
                }
        }

        private void saveConnection()
        {
                String content = JSONUtils.toJSONString(info, SerializationFeature.INDENT_OUTPUT);
                ConnectionRepository.saveConnection(info.getName(), content);

                close();

                EventBus.publish(new RefreshConnectionEvent());
        }
}
