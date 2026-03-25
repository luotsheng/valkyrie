package com.changhong.openvdb;

import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class MainLayout extends SplitPane {

    private double ratio = 0.3;

    private final Navigator navigator;
    private final Workbench workbench;

    public MainLayout(Stage stage) {
        this.navigator = new Navigator();
        this.workbench = new Workbench();
        this.getItems().addAll(navigator, workbench);

        setDividerPositions(ratio);

        Divider divider = getDividers().get(0);

        Platform.runLater(() -> {
            lookupAll(".split-pane-divider").forEach(node -> {
                // 只监听分割条才触发事件，避免因窗口大小导致 ratio 记忆失效
                node.setOnMousePressed(e -> node.setUserData(Boolean.TRUE));
                node.setOnMouseReleased(e -> node.setUserData(Boolean.FALSE));
                // Pane 比例变化监听
                divider.positionProperty().addListener((obs, oldVal, newVal) -> {
                    if (node.getUserData() == Boolean.TRUE) {
                        ratio = Math.floor(newVal.doubleValue() * 100) / 100;
                        System.out.printf("ratio: %s\n", ratio);
                    }
                });

            });
        });

        navigator.widthProperty().addListener((obs, oldVal, newVal) -> setDividerPositions(ratio));
    }

}
