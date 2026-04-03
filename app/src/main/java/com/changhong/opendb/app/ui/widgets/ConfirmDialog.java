package com.changhong.opendb.app.ui.widgets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.changhong.opendb.app.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class ConfirmDialog
{
        private static void openDialog(Stage stage, String message, Node... children)
        {
                Toolkit.getDefaultToolkit().beep();

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("数据库管理工具");

                Label label = new Label(message);
                label.setWrapText(true);

                HBox hbox = new HBox(children);
                hbox.setPadding(new Insets(10));
                hbox.setAlignment(Pos.CENTER_RIGHT);
                hbox.setSpacing(10);

                BorderPane root = new BorderPane();
                root.setCenter(label);
                root.setBottom(hbox);

                Scene scene = new Scene(root, 350, 150);

                stage.setScene(scene);
                stage.showAndWait();
        }

        public static boolean showCheckDialog(String fmt, Object... args)
        {
                Stage stage = new Stage();

                AtomicBoolean flag = new AtomicBoolean(true);

                CheckBox checkBox = new CheckBox();
                checkBox.setText("⚠ 我了解此操作无法恢复！");

                Button ok = new Button("确认");
                ok.setDisable(true);
                ok.setDefaultButton(true);
                ok.setOnAction(e -> {
                        flag.set(true);
                        stage.close();
                });

                Button cancel = new Button("取消");
                cancel.setDefaultButton(true);
                cancel.setOnAction(e -> {
                        flag.set(false);
                        stage.close();
                });

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        ok.setDisable(!newVal);
                });

                openDialog(stage, strfmt(fmt, args), checkBox, ok, cancel);

                return flag.get();
        }

        public static boolean showDialog(String fmt, Object... args)
        {
                Stage stage = new Stage();

                AtomicBoolean flag = new AtomicBoolean(true);

                Button ok = new Button("确认");
                ok.setDefaultButton(true);
                ok.setOnAction(e -> {
                        flag.set(true);
                        stage.close();
                });

                Button cancel = new Button("取消");
                cancel.setDefaultButton(true);
                cancel.setOnAction(e -> {
                        flag.set(false);
                        stage.close();
                });

                openDialog(stage, strfmt(fmt, args), ok, cancel);

                return flag.get();
        }
}
