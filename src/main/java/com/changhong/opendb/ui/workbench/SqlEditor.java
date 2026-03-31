package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.QueryResultSet;
import com.changhong.opendb.model.ODBNStatus;
import com.changhong.opendb.resource.ResourceManager;
import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.ui.widgets.VFX;
import com.changhong.opendb.ui.widgets.VSeparator;
import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.OS;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;

/**
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
@SuppressWarnings("FieldCanBeLocal")
public class SqlEditor extends SplitPane
{
        private final ToolBar toolBar;
        private final TextArea textArea;
        private final BorderPane topBorderPane;
        private final ResultSetViewPane resultSetTableViewPane;

        private ComboBox<ODBNConnection> connectionComboBox;
        private ComboBox<ODBNDatabase> databaseComboBox;

        public SqlEditor()
        {
                setOrientation(Orientation.VERTICAL);

                topBorderPane = new BorderPane();
                toolBar = new ToolBar();
                textArea = new TextArea();
                resultSetTableViewPane = new ResultSetViewPane();

                topBorderPane.setTop(toolBar);
                topBorderPane.setCenter(textArea);

                setupToolbar();
                setupTextArea();
                setupResultSetCloseEvent();

                getItems().addAll(topBorderPane);
        }

        public void setupToolbar()
        {
                ODBNStatus instance = ODBNStatus.getInstance();
                ODBNConnection selectedConnection = instance.getSelectedConnection();

                Button run = VFX.newIconButton("运行 SQL", "run0");
                run.setText("运行");
                run.setOnAction(event -> runSelectedScript());

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
                if (OS.isMac()) {
                        textArea.setFont(Font.font("Monaco", 19));
                } else {
                        textArea.setFont(Font.font("Consolas", 19));
                }

                textArea.setOnKeyPressed(event -> {
                        if (event.isControlDown() && event.getCode() == KeyCode.R) {
                                System.out.println("Ctrl+R");
                                runSelectedScript();
                                event.consume();
                        }
                });

                textArea.setText("select * from tra_schedule_from_ai;");
        }

        private void setupResultSetCloseEvent()
        {
                resultSetTableViewPane.setOnCloseRequest(event -> {
                        getItems().remove(resultSetTableViewPane);
                });
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

                connection.setButtonCell(new ListCell<>()
                {
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

                connection.setCellFactory(list -> new ListCell<>()
                {
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

                database.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openDatabase();
                });

                database.setButtonCell(new ListCell<>()
                {
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


                database.setCellFactory(list -> new ListCell<>()
                {
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

        private void runSelectedScript()
        {
                try {
                        String sql = textArea.getSelectedText();

                        if (sql == null || sql.isEmpty())
                                sql = textArea.getText();

                        Statements statements = CCJSqlParserUtil.parseStatements(sql);

                        int size = statements.size();
                        String[] sqlArr = new String[size];

                        for (int i = 0; i < size; i++)
                                sqlArr[i] = statements.get(i).toString() + ';';

                        ODBNConnection connection = connectionComboBox.getSelectionModel()
                                .getSelectedItem();

                        ODBNDatabase database = databaseComboBox.getSelectionModel()
                                .getSelectedItem();

                        JdbcTemplate jdbcTemplate = connection.getJdbcTemplate();
                        QueryResultSet qrs = jdbcTemplate.select(database.getName(), sqlArr);

                        resultSetTableViewPane.refresh(qrs);

                        getItems().remove(resultSetTableViewPane);
                        getItems().add(resultSetTableViewPane);
                } catch (Throwable e) {
                        Catcher.ithrow(e);
                }
        }

}
