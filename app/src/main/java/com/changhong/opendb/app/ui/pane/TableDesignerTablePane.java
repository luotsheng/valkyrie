package com.changhong.opendb.app.ui.pane;

import atlantafx.base.util.IntegerStringConverter;
import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.MySQL;
import com.changhong.opendb.app.driver.TableIndexMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.widgets.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
@SuppressWarnings({"FieldCanBeLocal", "unchecked"})
public class TableDesignerTablePane extends DetailPane
{
        private final Tab ownerTab;
        private final SQLExecutor executor;
        private final TableMetaData tableMetaData;
        private final VFXTableView<ColumnMetaData> structureView = new VFXTableView<>();
        private final VFXTableView<TableIndexMetaData> indexView = new VFXTableView<>();
        private final ToolBar toolBar = new ToolBar();
        private final TabPane tabPane = new TabPane();

        private Node oldGraphic;
        private final ProgressIndicator progressIndicator = Assets.newProgressIndicator();

        private List<ColumnMetaData> columnMetaDatas;
        private List<TableIndexMetaData> indexes;

        private Button reload;

        public TableDesignerTablePane(Tab ownerTab,
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
                Button save = VFX.newIconButton("保存", "storage");
                save.setOnAction(e -> applySave());

                Button plus = VFX.newIconButton("新增行", "plus");
                plus.setOnAction(e -> applyPlus());

                Button minus = VFX.newIconButton("删除行", "minus");
                minus.setOnAction(e -> applyMinus());

                reload = VFX.newIconButton("刷新", "reload");
                reload.setOnAction(e -> applyReload());

                toolBar.getItems().addAll(
                        save,
                        new VFXSeparator(),
                        plus,
                        minus,
                        new VFXSeparator(),
                        reload
                );
        }

        private void applySave()
        {

        }

        private void applyPlus()
        {
                structureView.getItems().add(new ColumnMetaData());
                structureView.refresh();
        }

        private void applyMinus()
        {
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
                beginReload();
                new Thread(() -> {
                        try {
                                this.columnMetaDatas = executor.getColumns(tableMetaData);
                                this.indexes = executor.getIndexes(tableMetaData);
                                structureView.getItems().setAll(FXCollections.observableArrayList(columnMetaDatas));
                                structureView.blink();
                                indexView.getItems().setAll(FXCollections.observableArrayList(indexes));
                                indexView.blink();
                        } finally {
                                endReload();
                        }
                }).start();
        }

        private void setupStructureView()
        {
                structureView.setEditable(true);
                structureView.getSelectionModel().setCellSelectionEnabled(true);
                structureView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                // 列
                TableColumn<ColumnMetaData, String> name = new VFXTableColumn<>("名称", true);
                TableColumn<ColumnMetaData, String> type = new VFXTableColumn<>("类型", true);
                TableColumn<ColumnMetaData, Integer> length = new VFXTableColumn<>("长度", true);
                TableColumn<ColumnMetaData, Integer> scale = new VFXTableColumn<>("小数位", true);
                TableColumn<ColumnMetaData, String> defaultValue = new VFXTableColumn<>("默认值", true);
                TableColumn<ColumnMetaData, Boolean> nullable = new VFXTableColumn<>("是否允许NULL", true);
                TableColumn<ColumnMetaData, Boolean> primary = new VFXTableColumn<>("主键", true);
                TableColumn<ColumnMetaData, Boolean> autoIncrement = new VFXTableColumn<>("是否自增", true);
                TableColumn<ColumnMetaData, String> comment = new VFXTableColumn<>("注释", true);

                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                type.setCellValueFactory(new PropertyValueFactory<>("type"));
                length.setCellValueFactory(new PropertyValueFactory<>("length"));
                scale.setCellValueFactory(new PropertyValueFactory<>("scale"));
                defaultValue.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
                nullable.setCellValueFactory(new PropertyValueFactory<>("nullable"));
                primary.setCellValueFactory(new PropertyValueFactory<>("primary"));
                autoIncrement.setCellValueFactory(new PropertyValueFactory<>("autoIncrement"));
                comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

                name.setCellFactory(c -> new VFXStringEditingTableCell<>());
                type.setCellFactory(c -> new VFXStringEditingTableCell<>());
                defaultValue.setCellFactory(c -> new VFXStringEditingTableCell<>());
                length.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));
                scale.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));
                comment.setCellFactory(c -> new VFXStringEditingTableCell<>());
                nullable.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                primary.setCellFactory(c -> new VFXCheckBoxTableCell<>());
                autoIncrement.setCellFactory(c -> new VFXCheckBoxTableCell<>());

                // 初始化宽度
                name.setPrefWidth(150);
                type.setPrefWidth(150);
                length.setPrefWidth(120);
                scale.setPrefWidth(100);
                defaultValue.setPrefWidth(200);
                nullable.setPrefWidth(120);
                primary.setPrefWidth(50);
                autoIncrement.setPrefWidth(100);
                comment.setPrefWidth(280);

                structureView.getColumns().addAll(
                        name,
                        type,
                        length,
                        scale,
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

                name.setCellFactory(c -> new VFXStringEditingTableCell<>());
                columns.setCellFactory(c -> new VFXStringEditingTableCell<>());
                type.setCellFactory(c -> new VFXStringEditingTableCell<>());

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
