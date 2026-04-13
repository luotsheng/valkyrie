package com.changhong.openvdb.app.ui.pane;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class PropertyGridPane extends GridPane
{
        private int row = 0;

        public PropertyGridPane()
        {
                setHgap(10);
                setVgap(10);
                setPadding(new Insets(10));

                ColumnConstraints col1 = new ColumnConstraints();
                col1.setHgrow(Priority.NEVER);
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setHgrow(Priority.ALWAYS);

                getColumnConstraints().addAll(col1, col2);
        }

        protected void addRow(String text, Node node) {
                row++;

                int col = 0;

                if (text != null)
                        add(new Label(text), (col++), row);

                add(node, col, row);
        }
}
