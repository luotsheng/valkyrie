package com.changhong.opendb.app.ui.dialog;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.RefreshQueryNodeEvent;
import com.changhong.opendb.app.model.QueryInfo;
import com.changhong.opendb.app.repository.QueryScriptRepository;
import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.app.ui.pane.DetailPane;
import com.changhong.opendb.app.ui.widgets.VFXComboBox;
import com.changhong.opendb.app.ui.workbench.SqlEditor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class RenameQueryScriptDialog extends DetailPane
{
        private final Stage stage;
        private final QueryInfo queryInfo;
        private final TextField textField;

        public RenameQueryScriptDialog(Stage stage, QueryInfo queryInfo)
        {
                this.stage = stage;
                this.queryInfo = queryInfo;

                Label title = new Label("查询名称：");
                textField = new TextField(queryInfo.getName());
                textField.setPromptText("输入查询名称...");
                VBox topBox = new VBox(title, textField);
                topBox.setSpacing(10);
                topBox.setPadding(new Insets(20, 10, 5, 10));

                Button ok = new Button("保存");
                ok.setOnAction(e -> save());
                Button cancel = new Button("取消");
                cancel.setOnAction(e -> cancel());
                Region spacer = new Region();
                HBox bottomBox = new HBox(8, spacer, cancel, ok);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                bottomBox.setPadding(new Insets(5, 10, 10, 10));

                setTop(topBox);
                setBottom(bottomBox);
        }

        private void save()
        {
                File sqlFile = queryInfo.getSqlFile();

                sqlFile = QueryScriptRepository.renameQueryScript(
                        sqlFile, textField.getText()
                );

                queryInfo.setSqlFile(sqlFile);

                EventBus.publish(new RefreshQueryNodeEvent());

                cancel();
        }

        private void cancel()
        {
                stage.close();
        }

        public static void showDialog(QueryInfo queryInfo)
        {
                Stage stage = new Stage();

                RenameQueryScriptDialog dialog = new RenameQueryScriptDialog(stage, queryInfo);

                Scene scene = new Scene(dialog, 400, 150);
                stage.setScene(scene);
                stage.showAndWait();

                dialog.save();
        }

}
