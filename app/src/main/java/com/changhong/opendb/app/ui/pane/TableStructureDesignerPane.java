package com.changhong.opendb.app.ui.pane;

import com.changhong.opendb.app.driver.*;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.widgets.VFXIconButton;
import com.changhong.opendb.app.ui.widgets.VFXSeparator;
import com.changhong.opendb.app.ui.widgets.table.VFXTableColumn;
import com.changhong.opendb.app.ui.widgets.table.VFXTableColumnFactory;
import com.changhong.opendb.app.ui.widgets.table.VFXTableView;
import com.changhong.opendb.app.ui.widgets.table.cell.VFXCheckBoxTableCell;
import com.changhong.opendb.app.ui.widgets.table.cell.VFXComboBoxTableCell;
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

import static com.changhong.io.IOUtils.printf;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
@SuppressWarnings("unchecked")
public class TableStructureDesignerPane extends DetailPane
{
        private final Tab ownerTab;
        private final SQLExecutor executor;
        private final TableMetaData tableMetaData;
        private final VFXTableView<ColumnMetaData> structureView = new VFXTableView<>();
        private final VFXTableView<TableIndexMetaData> indexView = new VFXTableView<>();
        private final ToolBar toolBar = new ToolBar();
        private final TabPane tabPane = new TabPane();
        private final Tab structureTab = new Tab("表结构");
        private final Tab indexTab = new Tab("索引");

        private final Button saveButton = new VFXIconButton("保存", "storage");
        private final Button plusButton = new VFXIconButton("新增行", "plus");
        private final Button minusButton = new VFXIconButton("删除行", "minus");
        private final Button reloadButton = new VFXIconButton("刷新", "reload");

        private Node oldGraphic;
        private final ProgressIndicator progressIndicator = Assets.newProgressIndicator();

        private List<ColumnMetaData> columnMetaDatas;
        private List<TableIndexMetaData> indexes;

        private final Designer<ColumnMetaData> tableStructureDesigner;
        private final Designer<TableIndexMetaData> indexColumnDesigner;

        /* 当前选中标签对应的设计接口 */
        private Designer<?> designer;

        public TableStructureDesignerPane(Tab ownerTab,
                                          SQLExecutor executor,
                                          TableMetaData tableMetaData)
        {
                this.ownerTab = ownerTab;
                this.executor = executor;
                this.tableMetaData = tableMetaData;
                this.columnMetaDatas = executor.getColumns(tableMetaData);
                this.indexes = executor.getIndexes(tableMetaData);

                this.tableStructureDesigner = new MySQLTableStructureDesigner(tableMetaData, executor, "表结构");
                this.indexColumnDesigner = new MySQLIndexStructureDesigner(tableMetaData, executor, "索引");

                setupToolBar();

                setupStructureView();
                setupIndexView();

                applyReload();

                structureTab.setClosable(false);
                structureTab.setGraphic(Assets.use("struct1"));

                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(structureView);
                structureTab.setContent(borderPane);

                indexTab.setClosable(false);
                indexTab.setContent(indexView);
                indexTab.setGraphic(Assets.use("index0"));

                setupTabPane();

                setTop(toolBar);
                setCenter(tabPane);
        }

        private void setupTabPane()
        {
                tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal == structureTab) {
                                designer = tableStructureDesigner;
                        } else if (newVal == indexTab) {
                                designer = indexColumnDesigner;
                        } else {
                                throw new UnsupportedOperationException("Unsupported tab");
                        }
                });

                tabPane.getTabs().addAll(
                        structureTab,
                        indexTab
                );
        }

        private void setupToolBar()
        {
                saveButton.setOnAction(e -> applySave());
                plusButton.setOnAction(e -> applyPlus());
                minusButton.setOnAction(e -> applyMinus());
                reloadButton.setOnAction(e -> applyReload());

                toolBar.getItems().addAll(
                        saveButton,
                        new VFXSeparator(),
                        plusButton,
                        minusButton,
                        new VFXSeparator(),
                        reloadButton
                );
        }

        private void applySave()
        {
                try {
                        designer.applySave();
                        applyReload();
                } catch (Exception e) {
                        VFXDialogHelper.alert(e);
                }
        }

        private void applyPlus()
        {
                switch (designer) {
                        case MySQLTableStructureDesigner inst -> {
                                ColumnMetaData columnMetaData = new ColumnMetaData();
                                inst.applyPlus(columnMetaData);
                                structureView.getItems().add(columnMetaData);
                                structureView.refresh();
                        }

                        case MySQLIndexStructureDesigner inst -> {

                        }

                        default -> throw new UnsupportedOperationException("Unsupported designer object instance");
                }

        }

        private void applyMinus()
        {
                ObservableList<?> items = switch (designer) {
                        case MySQLTableStructureDesigner ignored -> structureView.getSelectionModel().getSelectedItems();
                        case MySQLIndexStructureDesigner ignored -> indexView.getSelectionModel().getSelectedItems();
                        default -> FXCollections.emptyObservableList();
                };

                if (items.isEmpty())
                        return;

                if (!VFXDialogHelper.askDangerous("确认删除%d条数据？", items.size()))
                        return;

                beginReload();
                minusButton.setDisable(true);

                new Thread(() -> {
                        try {
                                switch (designer) {
                                        case MySQLTableStructureDesigner inst -> inst.applyMinus((Collection<ColumnMetaData>) items);
                                        case MySQLIndexStructureDesigner inst -> inst.applyMinus((Collection<TableIndexMetaData>) items);
                                        default -> throw new UnsupportedOperationException("Unsupported designer object instance");
                                }
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
                        reloadButton.setDisable(true);
                        oldGraphic = ownerTab.getGraphic();
                        ownerTab.setGraphic(progressIndicator);
                });
        }

        private void endReload()
        {
                Platform.runLater(() -> {
                        reloadButton.setDisable(false);
                        ownerTab.setGraphic(oldGraphic);
                });
        }

        private void applyReload()
        {
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
                tableStructureDesigner.onReload(columnMetaDatas);

                this.indexes = executor.getIndexes(tableMetaData);
                indexColumnDesigner.onReload(indexes);

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

                factory.setOnEditCommitEventListener(tableStructureDesigner::onCommitEdit);

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
                indexView.enableCellEdit();

                VFXTableColumnFactory<TableIndexMetaData> factory = new VFXTableColumnFactory<>();

                factory.setOnEditCommitEventListener(indexColumnDesigner::onCommitEdit);

                TableColumn<TableIndexMetaData, String> name = factory.createEditableColumn("名称", "name");
                TableColumn<TableIndexMetaData, String> columns = factory.createEditableColumn("索引列", "columnsText");
                TableColumn<TableIndexMetaData, String> type = factory.createEditableColumn("类型", "type");

                name.setCellFactory(c -> new VFXTextFieldTableCell<>());
                columns.setCellFactory(c -> new VFXTextFieldTableCell<>());
                type.setCellFactory(c -> new VFXComboBoxTableCell<>(executor.getIndexTypes()));

                name.setPrefWidth(150);
                columns.setPrefWidth(350);
                type.setPrefWidth(150);

                indexView.getColumns().addAll(name, columns, type);

                if (executor.getProductMetaData().getMajorVersion() >= MySQL.VERSION_8x) {
                        TableColumn<TableIndexMetaData, Boolean> visible = factory.createEditableColumn("是否可见", "visible");
                        visible.setCellValueFactory(new PropertyValueFactory<>("visible"));
                        visible.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                        visible.setPrefWidth(120);
                        indexView.getColumns().addLast(visible);
                }
        }
}
