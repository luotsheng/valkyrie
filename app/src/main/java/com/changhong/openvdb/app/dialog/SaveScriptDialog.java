package com.changhong.openvdb.app.dialog;

import com.changhong.openvdb.app.Application;
import com.changhong.openvdb.app.navigator.node.UIConnectionNode;
import com.changhong.openvdb.app.navigator.node.UICatalogNode;
import com.changhong.openvdb.app.pane.BrowserPane;
import com.changhong.openvdb.app.widgets.VFXComboBox;
import com.changhong.openvdb.app.workbench.ScriptEditor;
import javafx.application.Platform;
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

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class SaveScriptDialog extends BrowserPane
{
        private final Stage stage;
        private final ScriptEditor scriptEditor;
        private final TextField textField;
        private final VFXComboBox<UIConnectionNode> connectionComboBox;
        private final VFXComboBox<UICatalogNode> catalogComboBox;

        private boolean isOk = false;

        public SaveScriptDialog(Stage stage, ScriptEditor scriptEditor)
        {
                this.stage = stage;
                this.scriptEditor = scriptEditor;

                Label title = new Label("查询名称：");
                textField = new TextField();
                textField.setPromptText("输入查询名称...");
                Label curName = new Label("当前名称：" + scriptEditor.getName());
                Label savePath = new Label("保存位置：");
                connectionComboBox = scriptEditor.copyConnectionComboBox();
                catalogComboBox = scriptEditor.copyDatabaseComboBox();
                connectionComboBox.setMaxWidth(Double.MAX_VALUE);
                catalogComboBox.setMaxWidth(Double.MAX_VALUE);
                VBox topBox = new VBox(title, textField, curName, savePath, connectionComboBox, catalogComboBox);
                topBox.setSpacing(10);
                topBox.setPadding(new Insets(20, 10, 5, 10));

                Button ok = new Button("保存");
                ok.setOnAction(e -> {
                        isOk = true;
                        cancel();
                });
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
                        catalogComboBox.getItems().clear();
                        catalogComboBox.getItems().addAll(newVal.getCatalogs());
                });
        }

        private void cancel()
        {
                stage.close();
        }

        /**
         * @return 返回用户输入的脚本名称， {@code null} 表示用户取消保存
         */
        public static String showDialog(ScriptEditor scriptEditor)
        {
                Stage stage = Application.createByPrimaryStage();

                SaveScriptDialog dialog = new SaveScriptDialog(stage, scriptEditor);

                Platform.runLater(() -> {});
                Scene scene = new Scene(dialog, 600, 300);
                stage.setScene(scene);
                stage.showAndWait();

                if (dialog.isOk)
                        return dialog.textField.getText();

                return null;
        }

}
