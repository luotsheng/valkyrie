package valkyrie.app.widgets;

import valkyrie.app.assets.Assets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * 图标按钮
 *
 * @author Luo Tiansheng
 * @since 2026/4/9
 */
public class VkIconButton extends Button
{
        public VkIconButton(String tip, String icon)
        {
                this(tip, null, icon);
        }

        public VkIconButton(String tip, String text, String icon)
        {
                getStyleClass().add("vk-icon-button");
                setText(text);
                setTooltip(new Tooltip(tip));
                setGraphic(Assets.use(icon));
        }
}
