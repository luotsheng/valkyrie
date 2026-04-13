package com.changhong.openvdb.app.ui.dialog;

import com.changhong.openvdb.app.VFXApplication;
import com.changhong.openvdb.app.core.event.EventBus;
import com.changhong.openvdb.app.core.event.RefreshQueryNodeEvent;
import com.changhong.openvdb.app.ui.navigator.node.VDBConnectionNode;
import com.changhong.openvdb.app.ui.navigator.node.VDBDatabaseNode;
import com.changhong.openvdb.app.ui.pane.BrowserPane;
import com.changhong.openvdb.app.ui.workbench.ScriptEditor;
import com.changhong.openvdb.core.model.ScriptFile;
import com.changhong.openvdb.core.repository.ScriptFileRepository;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import com.changhong.openvdb.app.ui.widgets.VFXComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class SaveQueryScriptDialog extends BrowserPane
{
        private final Stage stage;
        private final ScriptEditor scriptEditor;
        private final TextField textField;
        private final VFXComboBox<VDBConnectionNode> connectionComboBox;
        private final VFXComboBox<VDBDatabaseNode> databaseComboBox;

        public SaveQueryScriptDialog(Stage stage, ScriptEditor scriptEditor)
        {
                this.stage = stage;
                this.scriptEditor = scriptEditor;

                Label title = new Label("查询名称：");
                textField = new TextField();
                textField.setPromptText("输入查询名称...");
                Label curName = new Label("当前名称：" + scriptEditor.getName());
                Label savePath = new Label("保存位置：");
                connectionComboBox = scriptEditor.copyConnectionComboBox();
                databaseComboBox = scriptEditor.copyDatabaseComboBox();
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
                ScriptFile scriptFile = scriptEditor.getScriptFile();

                if (scriptFile == null) {
                        VDBConnectionNode connection = connectionComboBox.getSelectionModel()
                                .getSelectedItem();

                        VDBDatabaseNode database = databaseComboBox.getSelectionModel()
                                .getSelectedItem();

                        scriptFile = ScriptFileRepository.save(
                                connection.getName(),
                                database.getName(),
                                null,
                                textField.getText(),
                                scriptEditor.getCodeAreaContent());

                        scriptEditor.setScriptFile(scriptFile);
                } else {
                        ScriptFileRepository.save(scriptFile, scriptEditor.getCodeAreaContent());
                }

                EventBus.publish(new RefreshQueryNodeEvent());
                scriptEditor.markSaveFlag();

                cancel();
        }

        private void cancel()
        {
                stage.close();
        }

        public static void showDialog(ScriptEditor scriptEditor)
        {
                Stage stage = VFXApplication.createByPrimaryStage();

                SaveQueryScriptDialog dialog = new SaveQueryScriptDialog(stage, scriptEditor);

                if (scriptEditor.getScriptFile() == null) {
                        Scene scene = new Scene(dialog, 600, 300);
                        stage.setScene(scene);
                        stage.showAndWait();
                        return;
                }

                dialog.save();
        }

}
