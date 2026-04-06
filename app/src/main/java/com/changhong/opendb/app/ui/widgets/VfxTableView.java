package com.changhong.opendb.app.ui.widgets;


import javafx.animation.FadeTransition;
import javafx.scene.control.TableView;
import javafx.util.Duration;

/**
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
public class VfxTableView<S> extends TableView<S>
{
        public VfxTableView()
        {
                getStyleClass().add("vfx-table-view");
                setFixedCellSize(26);
        }

        public void blink()
        {
                FadeTransition ft = new FadeTransition(Duration.millis(200), this);
                ft.setFromValue(0.1);
                ft.setToValue(1.0);
                ft.setCycleCount(1);
                ft.setAutoReverse(true);

                ft.setOnFinished(event -> this.setOpacity(1.0));

                ft.play();
        }
}
