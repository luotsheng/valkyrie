package com.changhong.opendb.ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

/**
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
public class SqlEditor extends BorderPane
{
        private final ToolBar toolBar;
        private final TextArea textArea;

        public SqlEditor()
        {
                toolBar = new ToolBar();
                textArea = new TextArea();

                setupToolbar();
                setupTextArea();

                setTop(toolBar);
                setCenter(textArea);
        }

        public void setupToolbar()
        {
                Button run = VFX.newIconButton("运行 SQL", "run0");
                run.setText("运行");

                ComboBox<String> connection = new ComboBox<>();
                connection.setPrefWidth(200);

                ComboBox<String> database = new ComboBox<>();
                database.setPrefWidth(200);

                toolBar.getItems().addAll(
                        connection,
                        database,
                        new VSeparator(),
                        run);

        }

        public void setupTextArea()
        {
        }

}
