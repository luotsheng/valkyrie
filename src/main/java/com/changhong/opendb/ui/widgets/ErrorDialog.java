package com.changhong.opendb.ui.widgets;

import com.changhong.opendb.core.event.Event;
import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.EventListener;
import com.changhong.opendb.core.event.CauseEvent;
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

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class ErrorDialog
{
        @SuppressWarnings("unused")
        private static ErrorListener listener = null;

        static class ErrorListener implements EventListener
        {
                ErrorListener()
                {
                        EventBus.subscribe(CauseEvent.class, this);
                }

                @Override
                public void onEvent(Event event)
                {
                        showDialog(((CauseEvent) event).message);
                }
        }

        public static void showDialog(String message) {
                Toolkit.getDefaultToolkit().beep();

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("数据库管理工具");

                Label label = new Label(message);
                label.setWrapText(true);

                Button ok = new Button("确认");
                ok.setDefaultButton(true);
                ok.setOnAction(e -> stage.close());

                HBox hbox = new HBox(ok);
                hbox.setPadding(new Insets(10));
                hbox.setAlignment(Pos.CENTER_RIGHT);

                BorderPane root = new BorderPane();
                root.setCenter(label);
                root.setBottom(hbox);

                Scene scene = new Scene(root, 350, 150);

                stage.setScene(scene);
                stage.showAndWait();
        }

        public static void initializeListener()
        {
                if (listener == null)
                         listener = new ErrorListener();
        }

}
