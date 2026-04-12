package com.changhong.opendb.app.ui.workbench;

import com.changhong.driver.api.DataGrid;
import com.changhong.driver.api.Driver;
import com.changhong.driver.api.Session;
import com.changhong.driver.api.sql.SQL;
import com.changhong.opendb.app.model.ODBNStatus;
import com.changhong.opendb.app.model.QueryInfo;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.dialog.SaveQueryScriptDialog;
import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.app.ui.pane.DataGridViewPane;
import com.changhong.opendb.app.ui.pane.SqlMessagePane;
import com.changhong.opendb.app.ui.widgets.VFXCodeArea;
import com.changhong.opendb.app.ui.widgets.VFXComboBox;
import com.changhong.opendb.app.ui.widgets.VFXIconButton;
import com.changhong.opendb.app.ui.widgets.VFXSeparator;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialogHelper;
import com.changhong.exception.Causes;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import lombok.Getter;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.time.Duration;

import static com.changhong.string.StringStaticize.strwfmt;

/**
 * SQL 脚本编辑器
 *
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class SqlEditor extends SplitPane
{
        private static final Logger LOG = LoggerFactory.getLogger(SqlEditor.class);

        static final int QUERY_RESULT_SET_FIRST = 0;
        static final int QUERY_MESSAGE_LOG_FIRST = 1;

        @Getter
        private final Tab ownerTab;
        private final ToolBar toolBar;
        private final VFXCodeArea codeArea;
        private final VirtualizedScrollPane<CodeArea> virtualizedScrollPane;
        private final BorderPane topBorderPane;
        private final DataGridViewPane dataGridViewPane;
        private final Tab sqlMessageTab;
        private final SqlMessagePane sqlMessagePane;

        @Getter
        private String name;
        @Getter
        private File sqlFile;
        private Node oldGraphic;
        private QueryInfo queryInfo;
        private Driver driver = null;
        private long currentTaskId = System.currentTimeMillis();
        private boolean saveFlag = true;
        private VFXComboBox<ODBNConnection> connectionComboBox;
        private VFXComboBox<ODBNDatabase> databaseComboBox;

        private static int numberCount = 0;

        private Button run;
        private Button stop;
        private Button beautify;

        public SqlEditor(QueryInfo queryInfo, Tab ownerTab)
        {
                this.queryInfo = queryInfo;

                this.name = queryInfo != null
                        ? queryInfo.getName()
                        : strwfmt("查询脚本_%s.sql@[ N/A ]", (numberCount++));

                this.ownerTab = ownerTab;

                setOrientation(Orientation.VERTICAL);

                topBorderPane = new BorderPane();
                toolBar = new ToolBar();
                codeArea = new VFXCodeArea();
                virtualizedScrollPane = new VirtualizedScrollPane<>(codeArea);
                dataGridViewPane = new DataGridViewPane(false);
                sqlMessagePane = new SqlMessagePane();
                virtualizedScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

                // 绑定日志标签
                sqlMessageTab = new Tab("日志");
                sqlMessageTab.setContent(sqlMessagePane);

                topBorderPane.setTop(toolBar);
                topBorderPane.setCenter(virtualizedScrollPane);

                if (queryInfo != null)
                        setSqlFile(queryInfo.getSqlFile());

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

        private static ODBNConnection getSelectionConnection(ODBNStatus instance, QueryInfo info)
        {
                return info != null ? info.getConnection() : instance.getSelectedConnection();
        }

        private void setupToolbar()
        {
                ODBNStatus instance = ODBNStatus.getInstance();
                ODBNConnection selectedConnection = getSelectionConnection(instance, queryInfo);

                run = new VFXIconButton("运行已选择", "run0");
                run.setText("运行");
                run.setOnAction(event -> runTask());

                stop = new VFXIconButton("停止当时运行", "stop");
                stop.setText("停止");
                stop.setDisable(true);
                stop.setOnAction(event -> stopTask());

                beautify = new VFXIconButton("美化 SQL", "beautify");
                beautify.setText("美化 SQL");
                beautify.setOnAction(event -> beautifySQL());

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
                        new VFXSeparator(),
                        run,
                        stop,
                        new VFXSeparator(),
                        beautify);

        }

        public void setupCodeArea()
        {
                codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500))
                                .subscribe(ignored -> autoSave());

                codeArea.setOnKeyPressed(event -> {
                        if ((event.isControlDown() || event.isShortcutDown())
                                && event.getCode() == KeyCode.R) {
                                runTask();
                                event.consume();
                        }
                });

                codeArea.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (saveFlag && sqlFile == null) {
                                ownerTab.setText("* " + ownerTab.getText());
                                saveFlag = false;
                        }
                });
        }

        private void setupResultSetCloseEvent()
        {
                dataGridViewPane.setOnClosedListener(() ->
                        getItems().remove(dataGridViewPane));
        }

        private VFXComboBox<ODBNConnection> newConnectionComboBox()
        {
                VFXComboBox<ODBNConnection> connection = new VFXComboBox<>();
                configureConnectionComboBox(connection);
                connection.setPrefWidth(200);

                /* 选中连接时打开连接 */
                connection.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openConnection();
                });

                return connection;
        }

        private VFXComboBox<ODBNDatabase> newDatabaseComboBox()
        {
                VFXComboBox<ODBNDatabase> database = new VFXComboBox<>();
                configureDatabaseComboBox(database);
                database.setPrefWidth(200);

                database.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openDatabase();
                });

                return database;
        }

        private void configureConnectionComboBox(VFXComboBox<ODBNConnection> comboBox)
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

        private void configureDatabaseComboBox(VFXComboBox<ODBNDatabase> comboBox)
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

        /** 开始执行 true，结束执行 false */
        private void updateButtonForExecuting(boolean value)
        {
                run.setDisable(value);
                stop.setDisable(!value);
        }

        private void setLoadingIndicator()
        {
                oldGraphic = ownerTab.getGraphic();
                ownerTab.setGraphic(Assets.newProgressIndicator());
        }

        private void removeLoadingIndicator()
        {
                ownerTab.setGraphic(oldGraphic);
        }

        private void showResultSetTableViewPane(int flag)
        {
                dataGridViewPane.addTab(sqlMessageTab);

                switch (flag) {
                        case QUERY_RESULT_SET_FIRST -> dataGridViewPane.selectResultSetFirst();
                        case QUERY_MESSAGE_LOG_FIRST -> dataGridViewPane.select(sqlMessageTab);
                }

                if (!getItems().contains(dataGridViewPane))
                        getItems().add(dataGridViewPane);
        }

        /**
         * 执行任务
         */
        private void runTask()
        {
                if (run.isDisable())
                        return;

                updateButtonForExecuting(true);
                setLoadingIndicator();

                new Thread(() -> {

                        try {

                                String scriptText = codeArea.getSelectedText();

                                if (scriptText == null || scriptText.isEmpty())
                                        scriptText = codeArea.getText();

                                ODBNConnection connection = connectionComboBox.getSelectionModel()
                                        .getSelectedItem();

                                ODBNDatabase database = databaseComboBox.getSelectionModel()
                                        .getSelectedItem();

                                driver = connection.getDriver();
                                currentTaskId = System.currentTimeMillis();

                                Session session = database.getSession();
                                SQL sql = new  SQL(scriptText);

                                DataGrid grid = driver.execute(currentTaskId, session, sql);

                                if (grid != null) {
                                        Platform.runLater(() -> {
                                                dataGridViewPane.render(grid);
                                                showResultSetTableViewPane(QUERY_RESULT_SET_FIRST);
                                        });
                                } else {
                                        Platform.runLater(() -> showResultSetTableViewPane(QUERY_MESSAGE_LOG_FIRST));
                                }

                        } catch (Throwable e) {

                                Platform.runLater(() -> {
                                        sqlMessagePane.appendError(Causes.message(e));
                                        showResultSetTableViewPane(QUERY_MESSAGE_LOG_FIRST);
                                        LOG.error("run task error", e);
                                });

                        } finally {

                                Platform.runLater(() -> {
                                        updateButtonForExecuting(false);
                                        removeLoadingIndicator();
                                });

                        }

                }).start();
        }

        private void stopTask()
        {
                if (driver != null)
                        driver.cancel(currentTaskId);
        }

        private void beautifySQL()
        {
                codeArea.replaceText(SqlFormatter.format(codeArea.getText()));
                codeArea.applyHighlighting(); /* 强制刷新高亮 */
        }

        public String getCodeAreaContent()
        {
                return codeArea.getText();
        }

        public VFXComboBox<ODBNConnection> copyConnectionComboBox()
        {
                VFXComboBox<ODBNConnection> dst = connectionComboBox.copyComboBox();
                configureConnectionComboBox(dst);
                return dst;
        }

        public VFXComboBox<ODBNDatabase> copyDatabaseComboBox()
        {
                VFXComboBox<ODBNDatabase> dst = databaseComboBox.copyComboBox();
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
                                VFXDialogHelper.alert(e);
                        }

                        codeArea.clear();
                        codeArea.appendText(builder.toString().trim());
                        codeArea.applyHighlighting();
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

        private void autoSave()
        {
                if (sqlFile != null)
                        SaveQueryScriptDialog.showDialog(this);
        }

        private void save()
        {
                SaveQueryScriptDialog.showDialog(this);
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
                        if (VFXDialogHelper.ask("%s 未保存，是否保存？", ownerTab.getText()))
                                save();
                }
        }
}

