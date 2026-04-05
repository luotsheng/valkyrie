package com.changhong.opendb.app.ui.pane;

import atlantafx.base.util.IntegerStringConverter;
import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.TableIndexMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.widgets.VfxStringEditingTableCell;
import com.changhong.opendb.app.ui.widgets.VfxCheckBoxTableCell;
import com.changhong.opendb.app.ui.widgets.VFX;
import javafx.collections.FXCollections;
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
        private final SQLExecutor executor;
        private final TableMetaData tableMetaData;
        private final List<ColumnMetaData> columnMetaDatas;
        private final List<TableIndexMetaData> indexes;
        private final TableView<ColumnMetaData> structureView = VFX.newTableView();
        private final TableView<TableIndexMetaData> indexView = VFX.newTableView();
        private final ToolBar toolBar = new ToolBar();
        private final TabPane tabPane = new TabPane();

        public TableDesignerTablePane(SQLExecutor executor,
                                      TableMetaData tableMetaData,
                                      List<ColumnMetaData> columnMetaDatas)
        {
                this.executor = executor;
                this.tableMetaData = tableMetaData;
                this.columnMetaDatas = columnMetaDatas;
                this.indexes = executor.getIndexes(tableMetaData);

                setupToolBar();
                setupStructureView();
                setupIndexView();

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

                toolBar.getItems().addAll(
                        save,
                        plus,
                        minus
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

        private void setupStructureView()
        {
                structureView.setEditable(true);
                structureView.getSelectionModel().setCellSelectionEnabled(true);
                structureView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                // 列
                TableColumn<ColumnMetaData, String> name = VFX.newEditableTableColumn("名称");
                TableColumn<ColumnMetaData, String> type = VFX.newEditableTableColumn("类型");
                TableColumn<ColumnMetaData, Integer> length = VFX.newEditableTableColumn("长度");
                TableColumn<ColumnMetaData, Integer> scale = VFX.newEditableTableColumn("小数位");
                TableColumn<ColumnMetaData, String> defaultValue = VFX.newEditableTableColumn("默认值");
                TableColumn<ColumnMetaData, Boolean> nullable = VFX.newEditableTableColumn("是否允许 NULL");
                TableColumn<ColumnMetaData, Boolean> primary = VFX.newEditableTableColumn("主键");
                TableColumn<ColumnMetaData, Boolean> autoIncrement = VFX.newEditableTableColumn("是否自增");
                TableColumn<ColumnMetaData, String> comment = VFX.newEditableTableColumn("注释");

                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                type.setCellValueFactory(new PropertyValueFactory<>("type"));
                length.setCellValueFactory(new PropertyValueFactory<>("length"));
                scale.setCellValueFactory(new PropertyValueFactory<>("scale"));
                defaultValue.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
                nullable.setCellValueFactory(new PropertyValueFactory<>("nullable"));
                primary.setCellValueFactory(new PropertyValueFactory<>("primary"));
                autoIncrement.setCellValueFactory(new PropertyValueFactory<>("autoIncrement"));
                comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

                name.setCellFactory(c -> new VfxStringEditingTableCell<>());
                type.setCellFactory(c -> new VfxStringEditingTableCell<>());
                defaultValue.setCellFactory(c -> new VfxStringEditingTableCell<>());
                length.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));
                scale.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));
                comment.setCellFactory(c -> new VfxStringEditingTableCell<>());

                nullable.setCellFactory(c -> new VfxCheckBoxTableCell<>());
                primary.setCellFactory(c -> new VfxCheckBoxTableCell<>());
                autoIncrement.setCellFactory(c -> new VfxCheckBoxTableCell<>());

                // 初始化宽度
                name.setPrefWidth(150);
                type.setPrefWidth(150);
                length.setPrefWidth(120);
                scale.setPrefWidth(80);
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

                structureView.getItems().addAll(FXCollections.observableArrayList(columnMetaDatas));
        }

        private void setupIndexView()
        {
                indexView.setEditable(true);
                indexView.getSelectionModel().setCellSelectionEnabled(true);
                indexView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                TableColumn<TableIndexMetaData, String> name = VFX.newEditableTableColumn("名称");
                TableColumn<TableIndexMetaData, String> columns = VFX.newEditableTableColumn("索引列");
                TableColumn<TableIndexMetaData, String> type = VFX.newEditableTableColumn("类型");
                TableColumn<TableIndexMetaData, Boolean> visible = VFX.newEditableTableColumn("是否可见");

                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                columns.setCellValueFactory(new PropertyValueFactory<>("columnsText"));
                type.setCellValueFactory(new PropertyValueFactory<>("type"));
                visible.setCellValueFactory(new PropertyValueFactory<>("visible"));

                name.setCellFactory(c -> new VfxStringEditingTableCell<>());
                columns.setCellFactory(c -> new VfxStringEditingTableCell<>());
                type.setCellFactory(c -> new VfxStringEditingTableCell<>());
                visible.setCellFactory(c -> new VfxCheckBoxTableCell<>());

                name.setPrefWidth(150);
                columns.setPrefWidth(200);
                type.setPrefWidth(100);
                visible.setPrefWidth(120);

                indexView.getColumns().addAll(name, columns, type, visible);
                indexView.getItems().addAll(FXCollections.observableArrayList(indexes));
        }
}
