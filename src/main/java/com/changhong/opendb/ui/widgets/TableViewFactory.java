package com.changhong.opendb.ui.widgets;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public class TableViewFactory
{
        public static <S> TableView<S> createTable()
        {
                return new TableView<>();
        }

        public static <S, T> TableColumn<S, T> createColumn(String name)
        {
                return new TableColumn<>(name);
        }
}
