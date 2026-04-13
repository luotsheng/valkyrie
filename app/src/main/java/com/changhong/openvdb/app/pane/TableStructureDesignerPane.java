package com.changhong.openvdb.app.pane;

import com.changhong.openvdb.driver.api.*;
import com.changhong.openvdb.driver.mysql.MySQL;
import com.changhong.openvdb.app.resource.Assets;
import com.changhong.openvdb.app.widgets.VFXIconButton;
import com.changhong.openvdb.app.widgets.VFXSeparator;
import com.changhong.openvdb.app.widgets.table.VFXTableColumnFactory;
import com.changhong.openvdb.app.widgets.table.VFXTableView;
import com.changhong.openvdb.app.widgets.table.cell.VFXCheckBoxTableCell;
import com.changhong.openvdb.app.widgets.table.cell.VFXComboBoxTableCell;
import com.changhong.openvdb.app.widgets.table.cell.VFXTextFieldTableCell;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
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
@SuppressWarnings("unchecked")
public class TableStructureDesignerPane extends BrowserPane
{
        private final Tab ownerTab;
        private final Session session;
        private final Driver driver;
        private final Table table;
        private final VFXTableView<Column> structureView = new VFXTableView<>();
        private final VFXTableView<Index> indexView = new VFXTableView<>();
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

        private List<Column> columnMetaDatas;
        private List<Index> indexes;

        private final Designer<Column> tableStructureDesigner;
        private final Designer<Index> indexColumnDesigner;

        /* 当前选中标签对应的设计接口 */
        private Designer<?> designer;

        public TableStructureDesignerPane(Tab ownerTab,
                                          Session session,
                                          Driver driver,
                                          Table table)
        {
                this.ownerTab = ownerTab;
                this.session = session;
                this.driver = driver;
                this.table = table;
                this.columnMetaDatas = driver.getColumns(session, table.getName());
                this.indexes = driver.getIndexes(session, table);

                this.tableStructureDesigner = new TableStructureDesigner(session, driver, table, "表结构");
                this.indexColumnDesigner = new IndexStructureDesigner(session, driver, table, "索引");

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
                saveButton.setOnAction(e -> onSave());
                plusButton.setOnAction(e -> onPlus());
                minusButton.setOnAction(e -> onMinus());
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

        private void onSave()
        {
                try {
                        designer.onSave();
                        applyReload();
                } catch (Exception e) {
                        VFXDialogHelper.alert(e);
                        applyReload();
                }
        }

        private void onPlus()
        {
                switch (designer) {
                        case TableStructureDesigner inst -> {
                                Column columnMetaData = new Column();
                                inst.onPlus(columnMetaData);
                                structureView.getItems().add(columnMetaData);
                                structureView.refresh();
                        }

                        case IndexStructureDesigner inst -> {
                                Index indexMetaData = new Index();
                                inst.onPlus(indexMetaData);
                                indexView.getItems().add(indexMetaData);
                                indexView.refresh();
                        }

                        default -> throw new UnsupportedOperationException("Unsupported designer object instance");
                }

        }

        private void onMinus()
        {
                ObservableList<?> items = switch (designer) {
                        case TableStructureDesigner ignored -> structureView.getSelectionModel().getSelectedItems();
                        case IndexStructureDesigner ignored -> indexView.getSelectionModel().getSelectedItems();
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
                                        case TableStructureDesigner inst -> inst.onMinus((Collection<Column>) items);
                                        case IndexStructureDesigner inst -> inst.onMinus((Collection<Index>) items);
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
                this.columnMetaDatas = driver.getColumns(session, table);
                tableStructureDesigner.onReload(columnMetaDatas);

                this.indexes = driver.getIndexes(session, table);
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

                VFXTableColumnFactory<Column> factory = new VFXTableColumnFactory<>();

                factory.setOnEditCommitEventListener(tableStructureDesigner::onCommitEdit);

                // 列
                TableColumn<Column, String> name = factory.createEditableColumn("名称", "name");
                TableColumn<Column, String> type = factory.createEditableColumn("类型", "type");
                TableColumn<Column, String> defaultValue = factory.createEditableColumn("默认值", "defaultValue");
                TableColumn<Column, Boolean> nullable = factory.createEditableColumn("是否允许NULL", "nullable");
                TableColumn<Column, Boolean> primary = factory.createEditableColumn("主键", "primary");
                TableColumn<Column, Boolean> autoIncrement = factory.createEditableColumn("是否自增", "autoIncrement");
                TableColumn<Column, String> comment = factory.createEditableColumn("注释", "comment");

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

                VFXTableColumnFactory<Index> factory = new VFXTableColumnFactory<>();

                factory.setOnEditCommitEventListener(indexColumnDesigner::onCommitEdit);

                TableColumn<Index, String> name = factory.createEditableColumn("名称", "name");
                TableColumn<Index, String> columns = factory.createEditableColumn("索引列", "columnsText");
                TableColumn<Index, String> type = factory.createEditableColumn("类型", "type");

                name.setCellFactory(c -> new VFXTextFieldTableCell<>());
                columns.setCellFactory(c -> new VFXTextFieldTableCell<>());
                type.setCellFactory(c -> new VFXComboBoxTableCell<>(driver.getIndexTypes()));

                name.setPrefWidth(150);
                columns.setPrefWidth(350);
                type.setPrefWidth(150);

                indexView.getColumns().addAll(name, columns, type);

                if (driver.getProductMetaData().getMajorVersion() >= MySQL.VERSION_8x) {
                        TableColumn<Index, Boolean> visible = factory.createEditableColumn("是否可见", "visible");
                        visible.setCellValueFactory(new PropertyValueFactory<>("visible"));
                        visible.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                        visible.setPrefWidth(120);
                        indexView.getColumns().addLast(visible);
                }
        }
}
