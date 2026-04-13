package com.changhong.openvdb.app.ui.dialog.connection;

import com.changhong.openvdb.app.model.ConnectionPropertyModel;
import com.changhong.openvdb.app.ui.pane.PropertyGridPane;
import javafx.scene.control.CheckBox;
import com.changhong.openvdb.app.ui.widgets.VFXComboBox;
import javafx.scene.control.TextField;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
class ConnectionAdvancedPane extends PropertyGridPane
{
        private final ConnectionPropertyModel propertyModel;

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

        public ConnectionAdvancedPane(ConnectionPropertyModel propertyModel)
        {
                super();
                this.propertyModel = propertyModel;
                bindModelProperty();
                setupTimezone();
                setupPaneLayout();
        }

        private void bindModelProperty()
        {
                jdbcUrl.textProperty().bindBidirectional(propertyModel.jdbcUrlProperty());
                timezone.valueProperty().bindBidirectional(propertyModel.timezoneProperty());
                useSSL.selectedProperty().bindBidirectional(propertyModel.useSSLProperty());
                tinyint1isBit.selectedProperty().bindBidirectional(propertyModel.tinyint1isBitProperty());
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
