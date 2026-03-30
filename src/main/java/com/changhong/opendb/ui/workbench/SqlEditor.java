package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.model.ODBNStatus;
import com.changhong.opendb.resource.ResourceManager;
import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.ui.widgets.VFX;
import com.changhong.opendb.ui.widgets.VSeparator;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

/**
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
@SuppressWarnings("FieldCanBeLocal")
public class SqlEditor extends BorderPane
{
        private final ToolBar toolBar;
        private final TextArea textArea;

        private ComboBox<ODBNConnection> connectionComboBox;
        private ComboBox<ODBNDatabase> databaseComboBox;

        public SqlEditor()
        {
                toolBar = new ToolBar();
                textArea = new TextArea();

                setupToolbar();
                setupTextArea();

                setTop(toolBar);
                setCenter(textArea);
        }

        public void setupToolbar()
        {
                ODBNStatus instance = ODBNStatus.getInstance();
                ODBNConnection selectedConnection = instance.getSelectedConnection();

                Button run = VFX.newIconButton("运行 SQL", "run0");
                run.setText("运行");

                connectionComboBox = newConnectionComboBox();
                databaseComboBox = newDatabaseComboBox();

                // setup combo
                connectionComboBox.getItems().addAll(instance.getConnections());

                if (selectedConnection != null) {
                        connectionComboBox.getSelectionModel().select(selectedConnection);
                        databaseComboBox.getItems().addAll(selectedConnection.getDatabases());
                        databaseComboBox.getSelectionModel().select(selectedConnection.getSelectedDatabase());
                }

                toolBar.getItems().addAll(
                        connectionComboBox,
                        databaseComboBox,
                        new VSeparator(),
                        run);

        }

        public void setupTextArea()
        {
                /* DO NOTHING */
        }

        private ComboBox<ODBNConnection> newConnectionComboBox()
        {
                ComboBox<ODBNConnection> connection = new ComboBox<>();
                connection.setPrefWidth(200);

                connection.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen()) {
                                newVal.openConnection();
                                databaseComboBox.getItems().addAll(newVal.getDatabases());
                        }
                });

                connection.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(ODBNConnection item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(ResourceManager.use("chain"));
                        }
                });

                connection.setCellFactory(list -> new ListCell<>() {
                        @Override
                        protected void updateItem(ODBNConnection item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(ResourceManager.use("chain"));
                        }
                });

                connection.setConverter(new StringConverter<>()
                {
                        @Override
                        public String toString(ODBNConnection connection)
                        {
                                return connection == null ? null : connection.getName();
                        }

                        @Override
                        public ODBNConnection fromString(String s)
                        {
                                return null;
                        }
                });

                return connection;
        }

        private ComboBox<ODBNDatabase> newDatabaseComboBox()
        {
                ComboBox<ODBNDatabase> database = new ComboBox<>();
                database.setPrefWidth(200);

                database.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(ODBNDatabase item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(ResourceManager.use("database1"));
                        }
                });


                database.setCellFactory(list -> new ListCell<>() {
                        @Override
                        protected void updateItem(ODBNDatabase item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(ResourceManager.use("database1"));
                        }
                });

                database.setConverter(new StringConverter<>()
                {
                        @Override
                        public String toString(ODBNDatabase database)
                        {
                                return database == null ? null : database.getName();
                        }

                        @Override
                        public ODBNDatabase fromString(String s)
                        {
                                return null;
                        }
                });

                return database;
        }

}
