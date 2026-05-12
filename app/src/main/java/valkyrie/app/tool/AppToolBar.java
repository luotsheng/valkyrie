package valkyrie.app.tool;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import valkyrie.app.event.bus.EventBus;
import valkyrie.app.event.workbench.OpenScriptEditorPaneEvent;
import valkyrie.app.explorer.UIConnectionNode;
import valkyrie.app.menu.ConnectionMenuBuilder;
import valkyrie.app.model.UIExplorerStatus;
import valkyrie.app.widgets.VkIconButton;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class AppToolBar extends ToolBar
{
        public AppToolBar()
        {
                Button newConnectionButton = new VkIconButton(null, "新建连接", "chain");
                ContextMenu contextMenu = ConnectionMenuBuilder.buildContextMenu();
                newConnectionButton.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                                contextMenu.show(newConnectionButton,
                                        event.getScreenX(),
                                        event.getScreenY());
                        }
                });

                Button newQueryButton = new VkIconButton("查询", "sql");
                newQueryButton.setText("新建查询");
                newQueryButton.setOnAction(event -> newScriptEditor());

                getItems().addAll(
                        newConnectionButton,
                        newQueryButton
                );
        }

        private void newScriptEditor()
        {
                UIExplorerStatus instance = UIExplorerStatus.getInstance();
                UIConnectionNode selectedConnection = instance.getSelectedConnection();
                EventBus.publish(new OpenScriptEditorPaneEvent(null, selectedConnection));
        }
}
