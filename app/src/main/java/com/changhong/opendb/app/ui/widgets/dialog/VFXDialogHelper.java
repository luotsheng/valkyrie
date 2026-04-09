package com.changhong.opendb.app.ui.widgets.dialog;

import com.changhong.opendb.app.VFXApplication;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.workbench.VFXCopyableLabel;
import com.changhong.opendb.app.utils.Causes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.changhong.string.StringStaticize.strwfmt;

/**
 * Dialog
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class VFXDialogHelper
{
        public interface DialogCallback {
                void apply() throws Throwable;
        }

        public static void runWith(DialogCallback callback)
        {
                try {
                        callback.apply();
                } catch (Throwable e) {
                        error(e);
                }
        }

        /**
         * 普通询问提示框
         */
        public static boolean ask(String fmt, Object... args)
        {
                return showDialog(fmt, args);
        }

        /**
         * 危险操作提示框
         */
        public static boolean askDangerous(String fmt, Object... args)
        {
                return showCheckDialog(fmt, args);
        }

        /**
         * 警告提示框
         */
        public static void warn(String fmt, Object... args)
        {
                showDialog(fmt, args);
        }

        /**
         * 异常提示框
         */
        public static void error(Throwable e)
        {
                warn(Causes.message(e));
        }

        private static void openDialog(Stage stage, String message, Node... children)
        {
                Toolkit.getDefaultToolkit().beep();

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle(VFXApplication.TITLE);

                VFXCopyableLabel label = new VFXCopyableLabel(message);

                HBox hbox = new HBox(children);
                hbox.setPadding(new Insets(10));
                hbox.setAlignment(Pos.CENTER_RIGHT);
                hbox.setSpacing(10);

                BorderPane root = new BorderPane();
                root.setCenter(label);
                root.setBottom(hbox);

                Dimension dimension = new Dimension(message);
                Scene scene = new Scene(root, dimension.width, dimension.height);

                stage.setScene(scene);
                stage.showAndWait();
        }

        private static boolean showCheckDialog(String fmt, Object... args)
        {
                Stage stage = new Stage();

                AtomicBoolean flag = new AtomicBoolean(true);

                CheckBox checkBox = new CheckBox();
                checkBox.setText("我晓得操作没法恢复！");
                checkBox.setGraphic(Assets.use("warning@2x"));

                javafx.scene.control.Button ok = new javafx.scene.control.Button("确认");
                ok.setDisable(true);
                ok.setDefaultButton(true);
                ok.setOnAction(e -> {
                        flag.set(true);
                        stage.close();
                });

                javafx.scene.control.Button cancel = new javafx.scene.control.Button("取消");
                cancel.setDefaultButton(true);
                cancel.setOnAction(e -> {
                        flag.set(false);
                        stage.close();
                });

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        ok.setDisable(!newVal);
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                openDialog(stage, strwfmt(fmt, args), checkBox, spacer, ok, cancel);

                return flag.get();
        }

        private static boolean showDialog(String fmt, Object... args)
        {
                Stage stage = new Stage();

                AtomicBoolean flag = new AtomicBoolean(true);

                javafx.scene.control.Button ok = new javafx.scene.control.Button("确认");
                ok.setDefaultButton(true);
                ok.setOnAction(e -> {
                        flag.set(true);
                        stage.close();
                });

                javafx.scene.control.Button cancel = new Button("取消");
                cancel.setDefaultButton(true);
                cancel.setOnAction(e -> {
                        flag.set(false);
                        stage.close();
                });

                openDialog(stage, strwfmt(fmt, args), ok, cancel);

                return flag.get();
        }

}
