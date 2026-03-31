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
import javafx.application.Platform;
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
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
@SuppressWarnings("FieldCanBeLocal")
public class SqlEditor extends SplitPane
{
        private final ToolBar toolBar;
        private final CodeArea codeArea;
        private final VirtualizedScrollPane<CodeArea> virtualizedScrollPane;
        private final BorderPane topBorderPane;
        private final ResultSetViewPane resultSetTableViewPane;

        private ComboBox<ODBNConnection> connectionComboBox;
        private ComboBox<ODBNDatabase> databaseComboBox;

        static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>\\b(" + String.join("|", SqlKeyWordDefine.KEYWORDS) + ")\\b)"
                        + "|(?<STRING>'([^'\\\\]|\\\\.)*')"
                        + "|(?<COMMENT>--[^\\n]*)"
                        + "|(?<NUMBER>\\b\\d+(?:\\.\\d+)?\\b)",
                Pattern.CASE_INSENSITIVE
        );

        public SqlEditor()
        {
                setOrientation(Orientation.VERTICAL);

                topBorderPane = new BorderPane();
                toolBar = new ToolBar();
                codeArea = new CodeArea();
                virtualizedScrollPane = new VirtualizedScrollPane<>(codeArea);
                resultSetTableViewPane = new ResultSetViewPane();

                topBorderPane.setTop(toolBar);
                topBorderPane.setCenter(virtualizedScrollPane);

                setupToolbar();
                setupCodeArea();
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

        public void setupCodeArea()
        {
                codeArea.setStyle("""
                        -fx-font-family: "Monaco", "Consolas", monospace;
                        -fx-font-size: 19px;
                        -fx-font-weight: normal;
                        """);
                codeArea.getStyleClass().add("vfx-code-area");

                codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

                codeArea.setOnKeyPressed(event -> {
                        if ((event.isControlDown() || event.isShortcutDown())
                                && event.getCode() == KeyCode.R) {
                                runSelectedScript();
                                event.consume();
                        }
                });

                codeArea.multiPlainChanges()
                        .successionEnds(Duration.ofMillis(200))
                        .subscribe(ignore -> applyHighlighting(codeArea));
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
                        if (!newVal.isOpen())
                                newVal.openConnection();

                        databaseComboBox.getItems().clear();
                        databaseComboBox.getItems().addAll(newVal.getDatabases());
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
                        String sql = codeArea.getSelectedText();

                        if (sql == null || sql.isEmpty())
                                sql = codeArea.getText();

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

                        if (!getItems().contains(resultSetTableViewPane))
                                getItems().add(resultSetTableViewPane);
                } catch (Throwable e) {
                        Catcher.ithrow(e);
                }
        }

        private static void applyHighlighting(CodeArea area)
        {
                String text = area.getText();
                area.clearStyle(0, text.length());

                Matcher matcher = PATTERN.matcher(text);
                while (matcher.find()) {
                        if (matcher.group("KEYWORD") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "keyword");
                        } else if (matcher.group("STRING") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "string");
                        } else if (matcher.group("COMMENT") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "comment");
                        } else if (matcher.group("NUMBER") != null) {
                                area.setStyleClass(matcher.start(), matcher.end(), "number");
                        }
                }
        }
}

