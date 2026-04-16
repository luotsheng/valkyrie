package com.changhong.openvdb.app.workbench;

import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.dialog.SaveScriptDialog;
import com.changhong.openvdb.app.event.RefreshQueryNodeEvent;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.explorer.UICatalogNode;
import com.changhong.openvdb.app.explorer.UIConnectionNode;
import com.changhong.openvdb.app.model.UINodeGlobalStatus;
import com.changhong.openvdb.app.pane.DataGridViewPane;
import com.changhong.openvdb.app.pane.SqlMessagePane;
import com.changhong.openvdb.app.widgets.*;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
import com.changhong.openvdb.core.model.ScriptFile;
import com.changhong.openvdb.core.repository.ScriptFileRepository;
import com.changhong.openvdb.driver.api.DataGrid;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.sql.SQL;
import com.changhong.utils.exception.Causes;
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
import java.util.ArrayList;
import java.util.List;

import static com.changhong.utils.string.StaticLibrary.fmt;
import static com.changhong.utils.string.StaticLibrary.strempty;

/**
 * SQL 脚本编辑器
 *
 * @author Luo Tiansheng
 * @since 2026/3/29
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ScriptEditor extends SplitPane
{
        private static final Logger LOG = LoggerFactory.getLogger(ScriptEditor.class);

        static final int QUERY_RESULT_SET_FIRST = 0;
        static final int QUERY_MESSAGE_LOG_FIRST = 1;

        @Getter
        private final Tab owner;
        private final ToolBar toolBar;
        private final VFXCodeArea codeArea;
        private final VirtualizedScrollPane<CodeArea> virtualizedScrollPane;
        private final BorderPane topBorderPane;
        private final DataGridViewPane dataGridViewPane;
        private final Tab sqlMessageTab;
        private final SqlMessagePane sqlMessagePane;

        @Getter
        private String name;
        private Node oldGraphic;
        @Getter
        private ScriptFile scriptFile;
        private Driver driver = null;
        private long currentTaskId = System.currentTimeMillis();
        private boolean saveFlag = true;
        private VFXComboBox<UIConnectionNode> connectionComboBox;
        private VFXComboBox<UICatalogNode> catalogComboBox;

        private static int numberCount = 0;

        private Button run;
        private Button stop;
        private Button beautify;

        private MenuItem beautifySelectedSQLItem;

        public ScriptEditor(UIConnectionNode conn, ScriptFile scriptFile, Tab owner)
        {
                this.scriptFile = scriptFile;

                this.name = scriptFile != null
                        ? scriptFile.getName()
                        : fmt("查询脚本_%s.sql@[ N/A ]", (numberCount++));

                this.owner = owner;

                setOrientation(Orientation.VERTICAL);

                topBorderPane = new BorderPane();
                toolBar = new ToolBar();
                codeArea = createCodeArea();
                virtualizedScrollPane = new VirtualizedScrollPane<>(codeArea);
                dataGridViewPane = new DataGridViewPane(false);
                sqlMessagePane = new SqlMessagePane();
                virtualizedScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

                // 绑定日志标签
                sqlMessageTab = new Tab("日志");
                sqlMessageTab.setContent(sqlMessagePane);

                topBorderPane.setTop(toolBar);
                topBorderPane.setCenter(virtualizedScrollPane);

                if (scriptFile != null)
                        setScriptFile(scriptFile);

                setupPane();
                setupToolbar(conn);
                setupCodeArea();
                setupResultSetCloseEvent();
                setOwnerTabName(name);

                getItems().addAll(topBorderPane);
        }

        private VFXCodeArea createCodeArea()
        {
                VFXCodeAreaConfig config = new VFXCodeAreaConfig();

                config.contextMenuHandler = (contextMenu) -> {
                        beautifySelectedSQLItem = new MenuItem("美化当前选中 SQL");
                        beautifySelectedSQLItem.setOnAction(event -> beautifySQL());

                        List<MenuItem> copyPasteItems = new ArrayList<>(contextMenu.getItems());

                        contextMenu.getItems().clear();
                        contextMenu.getItems().addAll(
                                beautifySelectedSQLItem,
                                new SeparatorMenuItem()
                        );

                        copyPasteItems.forEach(contextMenu.getItems()::add);
                };

                config.showingMenuListener = ((event, contextMenu) -> {
                        contextMenu.getItems().forEach(contextMenuItem -> {
                                if (contextMenuItem == beautifySelectedSQLItem)
                                        contextMenuItem.setDisable(strempty(codeArea.getSelectedText()));
                        });
                });

                return new VFXCodeArea(config);
        }

        private void setupPane()
        {
                addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                        if ((event.isShortcutDown())
                                && event.getCode() == KeyCode.S) {
                                save();
                        }
                });
        }

        private void setupToolbar(UIConnectionNode initConnection)
        {
                UINodeGlobalStatus instance = UINodeGlobalStatus.getInstance();
                UIConnectionNode selectedConnection = initConnection != null
                        ? initConnection
                        : instance.getSelectedConnection();

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
                catalogComboBox = newCatalogComboBox();

                // setup combo
                connectionComboBox.getItems().addAll(instance.getConnections());

                if (selectedConnection != null) {
                        connectionComboBox.getSelectionModel().select(selectedConnection);
                        catalogComboBox.getItems().addAll(selectedConnection.getCatalogs());
                        catalogComboBox.getSelectionModel().select(selectedConnection.getSelectedDatabase());
                }

                toolBar.getItems().addAll(
                        connectionComboBox,
                        catalogComboBox,
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
                        if ((event.isShortcutDown())
                                && event.getCode() == KeyCode.R) {
                                runTask();
                                event.consume();
                        }
                });

                codeArea.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (saveFlag && scriptFile == null) {
                                owner.setText("* " + owner.getText());
                                saveFlag = false;
                        }
                });
        }

        private void setupResultSetCloseEvent()
        {
                dataGridViewPane.setOnClosedListener(() ->
                        getItems().remove(dataGridViewPane));
        }

        private VFXComboBox<UIConnectionNode> newConnectionComboBox()
        {
                VFXComboBox<UIConnectionNode> connection = new VFXComboBox<>();
                configureConnectionComboBox(connection);
                connection.setPrefWidth(200);

                /* 选中连接时打开连接 */
                connection.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openConnection();
                });

                return connection;
        }

        private VFXComboBox<UICatalogNode> newCatalogComboBox()
        {
                VFXComboBox<UICatalogNode> catalog = new VFXComboBox<>();
                configureDatabaseComboBox(catalog);
                catalog.setPrefWidth(200);

                catalog.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isOpen())
                                newVal.openDatabase();
                });

                return catalog;
        }

        private void configureConnectionComboBox(VFXComboBox<UIConnectionNode> comboBox)
        {
                comboBox.setButtonCell(new ListCell<>()
                {
                        @Override
                        protected void updateItem(UIConnectionNode item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(item.getIcon());
                        }
                });

                comboBox.setCellFactory(list -> new ListCell<>()
                {
                        @Override
                        protected void updateItem(UIConnectionNode item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (empty || item == null)
                                        return;

                                setText(item.getName());
                                setGraphic(item.getIcon());
                        }
                });

                comboBox.setConverter(new StringConverter<>()
                {
                        @Override
                        public String toString(UIConnectionNode connection)
                        {
                                return connection == null ? null : connection.getName();
                        }

                        @Override
                        public UIConnectionNode fromString(String s)
                        {
                                return null;
                        }
                });
        }

        private void configureDatabaseComboBox(VFXComboBox<UICatalogNode> comboBox)
        {
                comboBox.setButtonCell(new ListCell<>()
                {
                        @Override
                        protected void updateItem(UICatalogNode item, boolean empty)
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
                        protected void updateItem(UICatalogNode item, boolean empty)
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
                        public String toString(UICatalogNode catalog)
                        {
                                return catalog == null ? null : catalog.getName();
                        }

                        @Override
                        public UICatalogNode fromString(String s)
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
                oldGraphic = owner.getGraphic();
                owner.setGraphic(Assets.newProgressIndicator());
        }

        private void removeLoadingIndicator()
        {
                owner.setGraphic(oldGraphic);
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

                                UIConnectionNode connection = connectionComboBox.getSelectionModel()
                                        .getSelectedItem();

                                UICatalogNode catalog = catalogComboBox.getSelectionModel()
                                        .getSelectedItem();

                                driver = connection.getDriver();
                                currentTaskId = System.currentTimeMillis();

                                Session session = catalog.getSession();
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
                String selected = codeArea.getSelectedText();

                if (selected != null && !selected.isEmpty()) {
                        int start = codeArea.getSelection().getStart();
                        int end = codeArea.getSelection().getEnd();

                        String formatted = SqlFormatter.format(selected);
                        codeArea.replaceText(start, end, formatted);
                } else {
                        codeArea.replaceText(SqlFormatter.format(codeArea.getText()));
                }

                codeArea.applyHighlightAll();
        }

        public String getCodeAreaContent()
        {
                return codeArea.getText();
        }

        public VFXComboBox<UIConnectionNode> copyConnectionComboBox()
        {
                VFXComboBox<UIConnectionNode> dst = connectionComboBox.copyComboBox();
                configureConnectionComboBox(dst);
                return dst;
        }

        public VFXComboBox<UICatalogNode> copyDatabaseComboBox()
        {
                VFXComboBox<UICatalogNode> dst = catalogComboBox.copyComboBox();
                configureDatabaseComboBox(dst);
                return dst;
        }

        public void setScriptFile(ScriptFile file)
        {
                if (file != null) {
                        this.scriptFile = file;
                        setOwnerTabName(file.getName());

                        StringBuilder builder = new StringBuilder();
                        char[] buf = new char[(int) file.length()];

                        try (FileReader rw = new FileReader(file)) {
                                int count = rw.read(buf);
                                builder.append(buf, 0, count);
                        } catch (Throwable e) {
                                VFXDialogHelper.alert(e);
                        }

                        codeArea.clear();
                        codeArea.appendText(builder.toString().trim());
                        codeArea.applyHighlightAll();
                }
        }

        private void setOwnerTabName(String name)
        {
                UICatalogNode catalog = null;

                if (catalogComboBox != null)
                        catalog = catalogComboBox.getSelectionModel().getSelectedItem();

                String tail = catalog == null
                        ? ""
                        : "@" + catalog.getName();

                owner.setText(name + tail);
        }

        private void autoSave()
        {
                if (scriptFile == null)
                        return;

                save();
        }

        private void save()
        {
                if (scriptFile == null) {
                        String saveScriptName = SaveScriptDialog.showDialog(this);

                        if (saveScriptName == null)
                                return;

                        UIConnectionNode connection = connectionComboBox.getSelectionModel()
                                .getSelectedItem();

                        UICatalogNode catalog = catalogComboBox.getSelectionModel()
                                .getSelectedItem();

                        ScriptFile newScriptFile = ScriptFileRepository.save(
                                connection.getName(),
                                catalog.getName(),
                                null,
                                saveScriptName,
                                getCodeAreaContent());

                        setScriptFile(newScriptFile);
                } else {
                        ScriptFileRepository.save(scriptFile, getCodeAreaContent());
                }

                EventBus.publish(new RefreshQueryNodeEvent());
                markSaveFlag();
        }

        public void markSaveFlag()
        {
                saveFlag = true;

                var text = owner.getText();
                if (text.startsWith("* "))
                        owner.setText(text.substring(2));
        }

        public boolean sqlFileEquals(File file)
        {
                if (scriptFile == null || file == null)
                        return false;

                return scriptFile.getAbsolutePath().equals(file.getAbsolutePath());
        }

        public void close()
        {
                if (!saveFlag) {
                        if (VFXDialogHelper.ask("%s 未保存，是否保存？", owner.getText()))
                                save();
                }
        }
}

