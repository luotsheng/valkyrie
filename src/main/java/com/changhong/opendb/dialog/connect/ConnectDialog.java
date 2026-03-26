package com.changhong.opendb.dialog.connect;

import com.changhong.opendb.model.ConnectionModel;
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
public class ConnectDialog extends Stage
{
        private TabPane tabPane;
        private HBox buttonBar;
        private final ConnectionModel model = new ConnectionModel("mysql");

        private static final int WW = 650;
        private static final int WH = 500;

        public ConnectDialog()
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
                generalTab.setContent(new ConnectGeneralPane(model));

                Tab advanceTab = new Tab("高级属性");
                advanceTab.setClosable(false);
                advanceTab.setContent(new ConnectAdvancePane(model));

                tabPane.getTabs().addAll(generalTab, advanceTab);
        }

        private void setupButtonBar()
        {
                Button btnTest = new Button("测试链接");
                Button btnSave = new Button("保存");
                Button btnCancel = new Button("取消");

                buttonBar = new HBox(10, btnTest, btnSave, btnCancel);
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
}
