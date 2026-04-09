package com.changhong.opendb.app.ui.widgets.table;


import javafx.animation.FadeTransition;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableView;
import javafx.util.Duration;

/**
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
public class VFXTableView<S> extends TableView<S>
{
        public VFXTableView()
        {
                getStyleClass().add("vfx-table-view");
                setFixedCellSize(26);
        }

        public void addItemListener(ListChangeListener<? super S> listener)
        {
                getItems().addListener(listener);
        }

        public void playFlash()
        {
                FadeTransition ft = new FadeTransition(Duration.millis(600), this);
                ft.setFromValue(0.1);
                ft.setToValue(1.0);
                ft.setCycleCount(1);
                ft.setAutoReverse(true);

                ft.setOnFinished(event -> this.setOpacity(1.0));

                ft.play();
        }

        @Override
        public void refresh()
        {
                super.refresh();
        }
}
