package com.changhong.openvdb.app.widgets;

import com.changhong.openvdb.app.assets.Assets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * 图标按钮
 *
 * @author Luo Tiansheng
 * @since 2026/4/9
 */
public class VFXIconButton extends Button
{
        public VFXIconButton(String tip, String icon)
        {
                getStyleClass().add("vfx-icon-button");
                setTooltip(new Tooltip(tip));
                setGraphic(Assets.use(icon));
        }
}
