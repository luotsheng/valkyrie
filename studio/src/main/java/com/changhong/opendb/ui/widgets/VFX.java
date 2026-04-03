package com.changhong.opendb.ui.widgets;

import com.changhong.opendb.resource.Assets;
import javafx.scene.control.*;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public class VFX
{
        public static <S> TableView<S> newTableView()
        {
                TableView<S> table = new TableView<>();
                table.getStyleClass().add("vfx-table-view");
                table.setFixedCellSize(26);
                return table;
        }

        public static <S, T> TableColumn<S, T> newTableColumn(String name)
        {
                return new TableColumn<>(name);
        }

        public static Button newIconButton(String tip, String icon)
        {
                Button button = new Button();

                button.getStyleClass().add("vfx-icon-button");
                button.setTooltip(new Tooltip(tip));
                button.setGraphic(Assets.use(icon));

                return button;
        }

        public static <T> ComboBox<T> copyComboBox(ComboBox<T> src)
        {
                ComboBox<T> dst = new ComboBox<>();
                dst.getItems().addAll(src.getItems());
                dst.getSelectionModel().select(
                        src.getSelectionModel().getSelectedIndex());
                return dst;
        }
}
