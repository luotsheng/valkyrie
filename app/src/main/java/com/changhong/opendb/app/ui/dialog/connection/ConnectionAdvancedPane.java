package com.changhong.opendb.app.ui.dialog.connection;

import com.changhong.opendb.app.model.ConnectionProperty;
import com.changhong.opendb.app.ui.pane.PropertyGridPane;
import javafx.scene.control.CheckBox;
import com.changhong.opendb.app.ui.widgets.VFXComboBox;
import javafx.scene.control.TextField;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
class ConnectionAdvancedPane extends PropertyGridPane
{
        private final ConnectionProperty info;

        private final TextField jdbcUrl = new TextField("jdbc:mysql://");
        private final VFXComboBox<String> timezone = new VFXComboBox<>();
        private final CheckBox useSSL = new CheckBox("使用 SSL");
        private final CheckBox tinyint1isBit = new CheckBox("TINYINT 转布尔类型");

        private static final String[] TIMEZONES = new String[]{
                "UTC",
                "Asia/Shanghai",
                "Asia/Hong_Kong",
                "Asia/Singapore",
                "Asia/Seoul",
                "Asia/Bangkok",
                "Asia/Dubai",
                "Europe/London",
                "Europe/Berlin",
                "Europe/Paris",
                "America/New_York",
                "America/Los_Angeles",
                "America/Chicago",
                "Australia/Sydney",
                "Asia/Tokyo",
        };

        public ConnectionAdvancedPane(ConnectionProperty info)
        {
                super();
                this.info = info;
                bindModelProperty();
                setupTimezone();
                setupPaneLayout();
        }

        private void bindModelProperty()
        {
                jdbcUrl.textProperty().bindBidirectional(info.jdbcUrlProperty());
                timezone.valueProperty().bindBidirectional(info.timezoneProperty());
                useSSL.selectedProperty().bindBidirectional(info.useSSLProperty());
                tinyint1isBit.selectedProperty().bindBidirectional(info.tinyint1isBitProperty());
        }

        private void setupTimezone()
        {
                timezone.setMaxWidth(Double.MAX_VALUE);
                timezone.getItems().addAll(TIMEZONES);
                timezone.getSelectionModel().select(1);
        }

        private void setupPaneLayout()
        {
                addRow("JDBC URL：", jdbcUrl);
                addRow("时区：", timezone);
                addRow(null, useSSL);
                addRow(null, tinyint1isBit);
        }
}
