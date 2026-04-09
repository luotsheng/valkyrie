package com.changhong.opendb.app.ui.widgets.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;

import static com.changhong.string.StringStaticize.strwfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
@SuppressWarnings("DuplicatedCode")
class ErrorDialog
{
        public static void showDialog(String fmt, Object... args) {
                Toolkit.getDefaultToolkit().beep();

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("数据库管理工具");

                Label label = new Label(strwfmt(fmt, args));
                label.setWrapText(true);

                Button ok = new Button("确认");
                ok.setDefaultButton(true);
                ok.setOnAction(e -> stage.close());

                HBox hbox = new HBox(ok);
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
}
