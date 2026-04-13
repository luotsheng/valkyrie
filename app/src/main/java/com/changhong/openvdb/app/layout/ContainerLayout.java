package com.changhong.openvdb.app.layout;

import com.changhong.openvdb.app.navigator.Navigator;
import com.changhong.openvdb.app.workbench.Workbench;
import javafx.application.Platform;
import javafx.scene.control.SplitPane;

import static com.changhong.utils.collection.Lists.beg;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class ContainerLayout extends SplitPane
{
        private double ratio = 0.2f;

        public ContainerLayout()
        {
                Navigator navigator = new Navigator();
                Workbench workbench = new Workbench();

                getItems().addAll(navigator, workbench);
                setDividerPositions(ratio);

                SplitPane.Divider divider = beg(getDividers());

                Platform.runLater(() -> lookupAll(".split-pane-divider").forEach(node -> {

                        // 只监听分割条才触发事件，避免因窗口大小导致 ratio 记忆失效
                        node.setOnMousePressed(e -> node.setUserData(Boolean.TRUE));
                        node.setOnMouseReleased(e -> node.setUserData(Boolean.FALSE));

                        // Pane 比例变化监听
                        divider.positionProperty().addListener((obs, oldVal, newVal) -> {
                                if (node.getUserData() == Boolean.TRUE)
                                        ratio = newVal.doubleValue();
                        });

                }));

                navigator.widthProperty().addListener((obs, oldVal, newVal) -> setDividerPositions(ratio));
        }

}
