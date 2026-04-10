package com.changhong.opendb.app.ui.pane;

import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.MySQL;
import com.changhong.opendb.app.driver.TableIndexMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.widgets.VFXIconButton;
import com.changhong.opendb.app.ui.widgets.VFXSeparator;
import com.changhong.opendb.app.ui.widgets.table.VFXTableColumn;
import com.changhong.opendb.app.ui.widgets.table.VFXTableColumnFactory;
import com.changhong.opendb.app.ui.widgets.table.VFXTableView;
import com.changhong.opendb.app.ui.widgets.table.cell.VFXCheckBoxTableCell;
import com.changhong.opendb.app.ui.widgets.table.cell.VFXTextFieldTableCell;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialogHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.*;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
@SuppressWarnings({"FieldCanBeLocal", "unchecked", "ExtractMethodRecommender"})
public class TableStructureDesignerPane extends DetailPane
{
        private final Tab ownerTab;
        private final SQLExecutor executor;
        private final TableMetaData tableMetaData;
        private final VFXTableView<ColumnMetaData> structureView = new VFXTableView<>();
        private final VFXTableView<TableIndexMetaData> indexView = new VFXTableView<>();
        private final ToolBar toolBar = new ToolBar();
        private final TabPane tabPane = new TabPane();

        private final Button saveButton = new VFXIconButton("保存", "storage");
        private final Button plusButton = new VFXIconButton("新增行", "plus");
        private final Button minusButton = new VFXIconButton("删除行", "minus");

        private Node oldGraphic;
        private final ProgressIndicator progressIndicator = Assets.newProgressIndicator();

        private List<ColumnMetaData> columnMetaDatas;
        private List<TableIndexMetaData> indexes;

        private final Set<ColumnMetaData> columnMetaDataUpdateBuffer = new HashSet<>();
        private final Set<ColumnMetaData> primaryUpdateBuffer = new LinkedHashSet<>();
        private final Map<Integer, TableIndexMetaData> tableIndexMetaDataUpdateBuffer = new HashMap<>();

        private Button reload;

        public TableStructureDesignerPane(Tab ownerTab,
                                          SQLExecutor executor,
                                          TableMetaData tableMetaData)
        {
                this.ownerTab = ownerTab;
                this.executor = executor;
                this.tableMetaData = tableMetaData;
                this.columnMetaDatas = executor.getColumns(tableMetaData);
                this.indexes = executor.getIndexes(tableMetaData);

                setupToolBar();

                setupStructureView();
                setupIndexView();

                applyReload();

                Tab tableStruct = new Tab("表结构");
                tableStruct.setClosable(false);
                tableStruct.setGraphic(Assets.use("struct1"));

                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(structureView);
                tableStruct.setContent(borderPane);

                Tab indexStruct = new Tab("索引");
                indexStruct.setClosable(false);
                indexStruct.setContent(indexView);
                indexStruct.setGraphic(Assets.use("index0"));

                tabPane.getTabs().addAll(
                        tableStruct,
                        indexStruct
                );

                setTop(toolBar);
                setCenter(tabPane);
        }

        private void setupToolBar()
        {
                saveButton.setOnAction(e -> applySave());
                plusButton.setOnAction(e -> applyPlus());
                minusButton.setOnAction(e -> applyMinus());

                reload = new VFXIconButton("刷新", "reload");
                reload.setOnAction(e -> applyReload());

                toolBar.getItems().addAll(
                        saveButton,
                        new VFXSeparator(),
                        plusButton,
                        minusButton,
                        new VFXSeparator(),
                        reload
                );
        }

        private void applySave()
        {
                try {
                        executor.alterChange(tableMetaData, columnMetaDataUpdateBuffer);
                        executor.alterPrimaryKey(tableMetaData, primaryUpdateBuffer);
                        primaryUpdateBuffer.clear();
                        columnMetaDataUpdateBuffer.clear();
                        applyReload();
                } catch (Exception e) {
                        VFXDialogHelper.alert(e);
                }
        }

        private void applyPlus()
        {
                structureView.getItems().add(new ColumnMetaData());
                structureView.refresh();
        }

        private void applyMinus()
        {
                ObservableList<ColumnMetaData> items = structureView.getSelectionModel().getSelectedItems();

                if (items.isEmpty())
                        return;

                if (!VFXDialogHelper.askDangerous("确认删除%d条数据？", items.size()))
                        return;

                beginReload();
                minusButton.setDisable(true);

                new Thread(() -> {
                        try {
                                executor.deleteColumns(tableMetaData, items);
                                doReload();
                        } catch (Exception e) {
                                VFXDialogHelper.alert(e);
                        } finally {
                                Platform.runLater(() -> {
                                        endReload();
                                        minusButton.setDisable(false);
                                });
                        }
                }).start();
        }

        private void beginReload()
        {
                Platform.runLater(() -> {
                        reload.setDisable(true);
                        oldGraphic = ownerTab.getGraphic();
                        ownerTab.setGraphic(progressIndicator);
                });
        }

        private void endReload()
        {
                Platform.runLater(() -> {
                        reload.setDisable(false);
                        ownerTab.setGraphic(oldGraphic);
                });
        }

        private void applyReload()
        {
                columnMetaDataUpdateBuffer.clear();

                beginReload();
                new Thread(() -> {
                        try {
                                doReload();
                        } finally {
                                endReload();
                        }
                }).start();
        }

        private void doReload()
        {
                this.columnMetaDatas = executor.getColumns(tableMetaData);
                this.indexes = executor.getIndexes(tableMetaData);
                Platform.runLater(() -> {
                        structureView.getItems().setAll(FXCollections.observableArrayList(columnMetaDatas));
                        indexView.getItems().setAll(FXCollections.observableArrayList(indexes));

                        structureView.refresh();
                        structureView.playFlash();

                        indexView.refresh();
                        indexView.playFlash();
                });
        }

        private void setupStructureView()
        {
                structureView.setEditable(true);
                structureView.getSelectionModel().setCellSelectionEnabled(true);
                structureView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                VFXTableColumnFactory<ColumnMetaData> factory = new VFXTableColumnFactory<>();

                factory.setOnEditCommitEventListener((oldVal, newVal) -> {
                        /* 检测到主键变动 */
                        if (oldVal.isPrimary() != newVal.isPrimary()) {

                                /* 如果主键列表为空初始化，如果表没有主键那就一直初始化（无伤大雅） */
                                if (primaryUpdateBuffer.isEmpty()) {
                                        for (ColumnMetaData columnMetaData : columnMetaDatas)
                                                if (columnMetaData.isPrimary())
                                                        primaryUpdateBuffer.add(columnMetaData);
                                }

                                if (newVal.isPrimary()) {
                                        primaryUpdateBuffer.add(newVal);
                                } else {
                                        primaryUpdateBuffer.remove(newVal);
                                }

                                return;
                        }

                        /* 变更记录 */
                        columnMetaDataUpdateBuffer.add(newVal);
                });

                // 列
                TableColumn<ColumnMetaData, String> name = factory.createEditableColumn("名称", "name");
                TableColumn<ColumnMetaData, String> type = factory.createEditableColumn("类型", "type");
                TableColumn<ColumnMetaData, String> defaultValue = factory.createEditableColumn("默认值", "defaultValue");
                TableColumn<ColumnMetaData, Boolean> nullable = factory.createEditableColumn("是否允许NULL", "nullable");
                TableColumn<ColumnMetaData, Boolean> primary = factory.createEditableColumn("主键", "primary");
                TableColumn<ColumnMetaData, Boolean> autoIncrement = factory.createEditableColumn("是否自增", "autoIncrement");
                TableColumn<ColumnMetaData, String> comment = factory.createEditableColumn("注释", "comment");

                name.setCellFactory(c -> new VFXTextFieldTableCell<>());
                type.setCellFactory(c -> new VFXTextFieldTableCell<>());
                defaultValue.setCellFactory(c -> new VFXTextFieldTableCell<>());
                comment.setCellFactory(c -> new VFXTextFieldTableCell<>());
                nullable.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                primary.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                autoIncrement.setCellFactory(c -> new VFXCheckBoxTableCell<>());

                // 初始化宽度
                name.setPrefWidth(150);
                type.setPrefWidth(150);
                defaultValue.setPrefWidth(200);
                nullable.setPrefWidth(120);
                primary.setPrefWidth(50);
                autoIncrement.setPrefWidth(100);
                comment.setPrefWidth(280);

                structureView.getColumns().addAll(
                        name,
                        type,
                        nullable,
                        primary,
                        autoIncrement,
                        defaultValue,
                        comment
                );
        }

        private void setupIndexView()
        {
                indexView.setEditable(true);
                indexView.getSelectionModel().setCellSelectionEnabled(true);
                indexView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                TableColumn<TableIndexMetaData, String> name = new VFXTableColumn<>("名称", true);
                TableColumn<TableIndexMetaData, String> columns = new VFXTableColumn<>("索引列", true);
                TableColumn<TableIndexMetaData, String> type = new VFXTableColumn<>("类型", true);

                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                columns.setCellValueFactory(new PropertyValueFactory<>("columnsText"));
                type.setCellValueFactory(new PropertyValueFactory<>("type"));

                name.setCellFactory(c -> new VFXTextFieldTableCell<>());
                columns.setCellFactory(c -> new VFXTextFieldTableCell<>());
                type.setCellFactory(c -> new VFXTextFieldTableCell<>());

                name.setPrefWidth(150);
                columns.setPrefWidth(200);
                type.setPrefWidth(100);

                indexView.getColumns().addAll(name, columns, type);

                if (executor.getProductMetaData().getMajorVersion() >= MySQL.VERSION_8x) {
                        TableColumn<TableIndexMetaData, Boolean> visible = new VFXTableColumn<>("是否可见", true);
                        visible.setCellValueFactory(new PropertyValueFactory<>("visible"));
                        visible.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                        visible.setPrefWidth(120);
                        indexView.getColumns().addLast(visible);
                }
        }
}
