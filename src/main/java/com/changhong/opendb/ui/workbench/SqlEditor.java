package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.QueryResultSet;
import com.changhong.opendb.model.ODBNStatus;
import com.changhong.opendb.model.QueryInfo;
import com.changhong.opendb.resource.Assets;
import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.ui.widgets.ConfirmDialog;
import com.changhong.opendb.ui.widgets.SaveQueryScriptDialog;
import com.changhong.opendb.ui.widgets.VFX;
import com.changhong.opendb.ui.widgets.VSeparator;
import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.OS;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import lombok.Getter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
@SuppressWarnings("FieldCanBeLocal")
public class SqlEditor extends SplitPane
{
        @Getter
        private final Tab ownerTab;
        private final ToolBar toolBar;
        private final CodeArea codeArea;
        private final VirtualizedScrollPane<CodeArea> virtualizedScrollPane;
        private final BorderPane topBorderPane;
        private final ResultSetViewPane resultSetTableViewPane;

        @Getter
        private String name;
        @Getter
        private File sqlFile;
        private boolean saveFlag = true;
        private ComboBox<ODBNConnection> connectionComboBox;
        private ComboBox<ODBNDatabase> databaseComboBox;

        static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>\\b(" + String.join("|", SqlKeyWordDefine.KEYWORDS) + ")\\b)"
                        + "|(?<STRING>'([^'\\\\]|\\\\.)*')"
                        + "|(?<COMMENT>--[^\\n]*)"
                        + "|(?<NUMBER>\\b\\d+(?:\\.\\d+)?\\b)",
                Pattern.CASE_INSENSITIVE
        );

        public SqlEditor(String name, Tab ownerTab)
        {
                this(name, null, ownerTab);
        }

        public SqlEditor(QueryInfo queryInfo, Tab ownerTab)
        {
                this(queryInfo.getName(), queryInfo.getSqlFile(), ownerTab);
        }

        public SqlEditor(String name, File sqlFile, Tab ownerTab)
        {
                this.name = name;
                this.ownerTab = ownerTab;

                setOrientation(Orientation.VERTICAL);

                topBorderPane = new BorderPane();
                toolBar = new ToolBar();
                codeArea = new CodeArea();
                virtualizedScrollPane = new VirtualizedScrollPane<>(codeArea);
                resultSetTableViewPane = new ResultSetViewPane();

                topBorderPane.setTop(toolBar);
                topBorderPane.setCenter(virtualizedScrollPane);

                setSqlFile(sqlFile);
                setupPane();
                setupToolbar();
                setupCodeArea();
                setupResultSetCloseEvent();
                setOwnerTabName(name);

                getItems().addAll(topBorderPane);
        }

        private void setupPane()
        {
                addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                        if ((event.isControlDown() || event.isShortcutDown())
                                && event.getCode() == KeyCode.S) {
                                save();
                        }
                });
        }

        public void setupToolbar()
        {
                ODBNStatus instance = ODBNStatus.getInstance();
                ODBNConnection selectedConnection = instance.getSelectedConnection();

                Button run = VFX.newIconButton("运行已选择", "run0");
                run.setText("运行");
                run.setOnAction(event -> runSelectedScript());

                Button stop = VFX.newIconButton("停止当时运行", "stop");
                stop.setText("停止");
                stop.setDisable(true);

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
                        run,
                        stop);

        }

        public void setupCodeArea()
        {
                codeArea.setStyle("-fx-font-weight: normal;");

                if (OS.isMac())
                        codeArea.setStyle("-fx-font-family: 'Monaco'; -fx-font-size: 19px;");

                if (OS.isWindows())
                        codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 19px;");

                if (OS.isLinux())
                        codeArea.setStyle("-fx-font-family: 'DejaVu Sans Mono'; -fx-font-size: 19px;");

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

                codeArea.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (saveFlag) {
                                ownerTab.setText("* " + ownerTab.getText());
                                saveFlag = false;
                        }
                });
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
                configureConnectionComboBox(connection);
                connection.setPrefWidth(200);

                connection.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openConnection();

                        databaseComboBox.getItems().clear();
                        databaseComboBox.getItems().addAll(newVal.getDatabases());
                });

                return connection;
        }

        private ComboBox<ODBNDatabase> newDatabaseComboBox()
        {
                ComboBox<ODBNDatabase> database = new ComboBox<>();
                configureDatabaseComboBox(database);
                database.setPrefWidth(200);

                database.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openDatabase();
                });

                return database;
        }

        private void configureConnectionComboBox(ComboBox<ODBNConnection> comboBox)
        {
                comboBox.setButtonCell(new ListCell<>()
                {
                        @Override
                        protected void updateItem(ODBNConnection item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(Assets.use("chain"));
                        }
                });

                comboBox.setCellFactory(list -> new ListCell<>()
                {
                        @Override
                        protected void updateItem(ODBNConnection item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(Assets.use("chain"));
                        }
                });

                comboBox.setConverter(new StringConverter<>()
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
        }

        private void configureDatabaseComboBox(ComboBox<ODBNDatabase> comboBox)
        {
                comboBox.setButtonCell(new ListCell<>()
                {
                        @Override
                        protected void updateItem(ODBNDatabase item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(Assets.use("database1"));
                        }
                });


                comboBox.setCellFactory(list -> new ListCell<>()
                {
                        @Override
                        protected void updateItem(ODBNDatabase item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(Assets.use("database1"));
                        }
                });

                comboBox.setConverter(new StringConverter<>()
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

        public String getCodeAreaContent()
        {
                return codeArea.getText();
        }

        public ComboBox<ODBNConnection> copyConnectionComboBox()
        {
                ComboBox<ODBNConnection> dst = VFX.copyComboBox(connectionComboBox);
                configureConnectionComboBox(dst);
                return dst;
        }

        public ComboBox<ODBNDatabase> copyDatabaseComboBox()
        {
                ComboBox<ODBNDatabase> dst = VFX.copyComboBox(databaseComboBox);
                configureDatabaseComboBox(dst);
                return dst;
        }

        public void setSqlFile(File sqlFile)
        {
                if (sqlFile != null) {
                        this.sqlFile = sqlFile;
                        setOwnerTabName(sqlFile.getName());

                        StringBuilder builder = new StringBuilder();
                        char[] buf = new char[(int) sqlFile.length()];

                        try (FileReader rw = new FileReader(sqlFile)) {
                                int count = rw.read(buf);
                                builder.append(buf, 0, count);
                        } catch (Throwable e) {
                                Catcher.ithrow(e);
                        }

                        codeArea.clear();
                        codeArea.appendText(builder.toString().trim());
                        applyHighlighting(codeArea);
                }
        }

        private void setOwnerTabName(String name)
        {
                ODBNDatabase database = null;

                if (databaseComboBox != null)
                        database = databaseComboBox.getSelectionModel().getSelectedItem();

                String tail = database == null
                        ? ""
                        : "@" + database.getName();

                ownerTab.setText(name + tail);
        }

        private void save()
        {
                SaveQueryScriptDialog.showDialog(this);
        }

        public boolean isSave()
        {
                return saveFlag;
        }

        public void markSaveFlag()
        {
                saveFlag = true;

                var text = ownerTab.getText();
                if (text.startsWith("* "))
                        ownerTab.setText(text.substring(2));
        }

        public boolean sqlFileEquals(File file)
        {
                if (sqlFile == null || file == null)
                        return false;

                return sqlFile.getAbsolutePath().equals(file.getAbsolutePath());
        }

        public void close()
        {
                if (!saveFlag) {
                        if (ConfirmDialog.showDialog("%s 未保存，是否保存？", ownerTab.getText()))
                                save();
                }
        }
}

