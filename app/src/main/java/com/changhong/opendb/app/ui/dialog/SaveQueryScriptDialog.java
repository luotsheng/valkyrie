package com.changhong.opendb.app.ui.dialog;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.RefreshQueryNodeEvent;
import com.changhong.opendb.app.repository.QueryScriptRepository;
import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.app.ui.pane.DetailPane;
import com.changhong.opendb.app.ui.workbench.SqlEditor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class SaveQueryScriptDialog extends DetailPane
{
        private final Stage stage;
        private final SqlEditor sqlEditor;
        private final TextField textField;
        private final ComboBox<ODBNConnection> connectionComboBox;
        private final ComboBox<ODBNDatabase> databaseComboBox;

        public SaveQueryScriptDialog(Stage stage, SqlEditor sqlEditor)
        {
                this.stage = stage;
                this.sqlEditor = sqlEditor;

                Label title = new Label("查询名称：");
                textField = new TextField();
                textField.setPromptText("输入查询名称...");
                Label curName = new Label("当前名称：" + sqlEditor.getName());
                Label savePath = new Label("保存位置：");
                connectionComboBox = sqlEditor.copyConnectionComboBox();
                databaseComboBox = sqlEditor.copyDatabaseComboBox();
                connectionComboBox.setMaxWidth(Double.MAX_VALUE);
                databaseComboBox.setMaxWidth(Double.MAX_VALUE);
                VBox topBox = new VBox(title, textField, curName, savePath, connectionComboBox, databaseComboBox);
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

                setupComboBox();
                setTop(topBox);
                setBottom(bottomBox);
        }

        private void setupComboBox()
        {
                connectionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                return;
                        databaseComboBox.getItems().clear();
                        databaseComboBox.getItems().addAll(newVal.getDatabases());
                });
        }

        private void save()
        {
                File sqlFile = sqlEditor.getSqlFile();

                if (sqlFile == null) {
                        ODBNConnection connection = connectionComboBox.getSelectionModel()
                                .getSelectedItem();

                        ODBNDatabase database = databaseComboBox.getSelectionModel()
                                .getSelectedItem();

                        sqlFile = QueryScriptRepository.saveQueryScript(
                                connection.getName(),
                                database.getName(),
                                textField.getText(),
                                sqlEditor.getCodeAreaContent()
                        );

                        sqlEditor.setSqlFile(sqlFile);
                } else {
                        QueryScriptRepository.saveQueryScript(sqlFile, sqlEditor.getCodeAreaContent());
                }

                EventBus.publish(new RefreshQueryNodeEvent());
                sqlEditor.markSaveFlag();

                cancel();
        }

        private void cancel()
        {
                stage.close();
        }

        public static void showDialog(SqlEditor sqlEditor)
        {
                Stage stage = new Stage();

                SaveQueryScriptDialog dialog = new SaveQueryScriptDialog(stage, sqlEditor);

                if (sqlEditor.getSqlFile() == null) {
                        Scene scene = new Scene(dialog, 600, 300);
                        stage.setScene(scene);
                        stage.showAndWait();
                        return;
                }

                dialog.save();
        }

}
