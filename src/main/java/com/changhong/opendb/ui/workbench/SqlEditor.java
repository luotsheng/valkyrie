package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.driver.SQL;
import com.changhong.opendb.driver.executor.SQLExecutor;
import com.changhong.opendb.driver.MutableDataGrid;
import com.changhong.opendb.model.ODBNStatus;
import com.changhong.opendb.model.QueryInfo;
import com.changhong.opendb.resource.Assets;
import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.ui.pane.MutableDataGridViewPane;
import com.changhong.opendb.ui.pane.SqlMessagePane;
import com.changhong.opendb.ui.widgets.*;
import com.changhong.opendb.utils.Catcher;
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

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
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
        private final VCodeArea codeArea;
        private final VirtualizedScrollPane<CodeArea> virtualizedScrollPane;
        private final BorderPane topBorderPane;
        private final MutableDataGridViewPane mutableDataGridViewPane;
        private final Tab sqlMessageTab;
        private final SqlMessagePane sqlMessagePane;

        @Getter
        private String name;
        @Getter
        private File sqlFile;
        private Node oldGraphic;
        private QueryInfo queryInfo;
        private SQLExecutor sqlExecutor = null;
        private long currentTaskId = System.currentTimeMillis();
        private boolean saveFlag = true;
        private ComboBox<ODBNConnection> connectionComboBox;
        private ComboBox<ODBNDatabase> databaseComboBox;

        private static int numberCount = 0;

        private Button run;
        private Button stop;
        private Button beautify;


        public SqlEditor(QueryInfo queryInfo, Tab ownerTab)
        {
                this.queryInfo = queryInfo;

                this.name = queryInfo != null
                        ? queryInfo.getName()
                        : strfmt("查询脚本_%s.sql@[ N/A ]", (numberCount++));

                this.ownerTab = ownerTab;

                setOrientation(Orientation.VERTICAL);

                topBorderPane = new BorderPane();
                toolBar = new ToolBar();
                codeArea = new VCodeArea();
                virtualizedScrollPane = new VirtualizedScrollPane<>(codeArea);
                mutableDataGridViewPane = new MutableDataGridViewPane();
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

                run = VFX.newIconButton("运行已选择", "run0");
                run.setText("运行");
                run.setOnAction(event -> runTask());

                stop = VFX.newIconButton("停止当时运行", "stop");
                stop.setText("停止");
                stop.setDisable(true);
                stop.setOnAction(event -> stopTask());

                beautify = VFX.newIconButton("美化 SQL", "beautify");
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
                        new VSeparator(),
                        run,
                        stop,
                        new VSeparator(),
                        beautify);

        }

        public void setupCodeArea()
        {
                codeArea.setOnKeyPressed(event -> {
                        if ((event.isControlDown() || event.isShortcutDown())
                                && event.getCode() == KeyCode.R) {
                                runTask();
                                event.consume();
                        }
                });

                codeArea.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (saveFlag) {
                                ownerTab.setText("* " + ownerTab.getText());
                                saveFlag = false;
                        }
                });
        }

        private void setupResultSetCloseEvent()
        {
                mutableDataGridViewPane.setOnCloseRequest(event -> {
                        getItems().remove(mutableDataGridViewPane);
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
                mutableDataGridViewPane.addTab(sqlMessageTab);

                switch (flag) {
                        case QUERY_RESULT_SET_FIRST -> mutableDataGridViewPane.selectResultSetFirst();
                        case QUERY_MESSAGE_LOG_FIRST -> mutableDataGridViewPane.select(sqlMessageTab);
                }

                if (!getItems().contains(mutableDataGridViewPane))
                        getItems().add(mutableDataGridViewPane);
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

                                sqlExecutor = connection.getSqlExecutor();
                                currentTaskId = System.currentTimeMillis();

                                String db = database.getName();
                                SQL sql = new  SQL(currentTaskId, db, scriptText);

                                MutableDataGrid grid = sqlExecutor.execute(sql, (info, status) -> {
                                        Platform.runLater(() -> {
                                                switch (status) {
                                                        case OK -> sqlMessagePane.appendInfo(info);
                                                        case SKIP -> sqlMessagePane.appendSkip(info);
                                                        case ERROR -> sqlMessagePane.appendError(info);
                                                }
                                        });
                                });

                                if (grid != null) {
                                        Platform.runLater(() -> {
                                                mutableDataGridViewPane.render(grid);
                                                showResultSetTableViewPane(QUERY_RESULT_SET_FIRST);
                                        });
                                } else {
                                        Platform.runLater(() -> showResultSetTableViewPane(QUERY_MESSAGE_LOG_FIRST));
                                }

                        } catch (Throwable e) {

                                Platform.runLater(() -> {
                                        sqlMessagePane.appendError(e.getCause().getMessage());
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
                if (sqlExecutor != null)
                        sqlExecutor.cancel(currentTaskId);
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

