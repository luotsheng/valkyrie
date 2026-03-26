package com.changhong.opendb.dialog.connection;

import com.changhong.opendb.model.ConnectionModel;
import com.changhong.opendb.widgets.PropertyGridPane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
class ConnectingAdvancePane extends PropertyGridPane
{
        private final ConnectionModel model;

        private final TextField jdbcUrl = new TextField("jdbc:mysql://");
        private final ComboBox<String> timezone = new ComboBox<>();
        private final CheckBox useSSL = new CheckBox("使用 SSL");

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

        public ConnectingAdvancePane(ConnectionModel model)
        {
                super();
                this.model = model;
                bindModelProperty();
                setupTimezone();
                setupPaneLayout();
        }

        private void bindModelProperty()
        {
                jdbcUrl.textProperty().bindBidirectional(model.jdbcUrlProperty());
                timezone.valueProperty().bindBidirectional(model.timezoneProperty());
                useSSL.selectedProperty().bindBidirectional(model.useSSLProperty());
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
        }
}
