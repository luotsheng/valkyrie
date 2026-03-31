package com.changhong.opendb.ui.widgets;

import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.ui.workbench.SqlEditor;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class SaveQueryScriptDialog extends DetailPane
{
        public SaveQueryScriptDialog(SqlEditor editor)
        {
                Label title = new Label("查询名称：");
                TextField textField = new TextField();
                textField.setPromptText("输入查询名称...");
                Label curName = new Label("当前名称：" + editor.getName());
                Label savePath = new Label("保存位置：");
                ComboBox<ODBNConnection> connectionComboBox = editor.copyConnectionComboBox();
                ComboBox<ODBNDatabase> databaseComboBox = editor.copyDatabaseComboBox();
                connectionComboBox.setMaxWidth(Double.MAX_VALUE);
                databaseComboBox.setMaxWidth(Double.MAX_VALUE);
                VBox topBox = new VBox(title, textField, curName, savePath, connectionComboBox, databaseComboBox);
                topBox.setSpacing(10);
                topBox.setPadding(new Insets(20, 10, 5, 10));

                Button ok = new Button("保存");
                Button cancel = new Button("取消");
                Region spacer = new Region();
                HBox bottomBox = new HBox(8, spacer, cancel, ok);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                bottomBox.setPadding(new Insets(5, 10, 10, 10));

                setTop(topBox);
                setBottom(bottomBox);
        }

        public static void showDialog(SqlEditor editor)
        {
                Stage stage = new Stage();

                SaveQueryScriptDialog dialog = new SaveQueryScriptDialog(editor);
                Scene scene = new Scene(dialog, 600, 300);

                stage.setScene(scene);
                stage.showAndWait();
        }

}
