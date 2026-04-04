package com.changhong.opendb.app.ui.pane;

import atlantafx.base.util.IntegerStringConverter;
import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.ui.widgets.VStringEditingTableCell;
import com.changhong.opendb.app.ui.widgets.VCheckBoxTableCell;
import com.changhong.opendb.app.ui.widgets.VFX;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
@SuppressWarnings({"FieldCanBeLocal", "unchecked"})
public class DesignTablePane extends DetailPane
{
        private final TableMetaData tableMetaData;
        private final List<ColumnMetaData> columnMetaDatas;
        private final TableView<ColumnMetaData> tableView = VFX.newTableView();
        private final ToolBar toolBar = new ToolBar();

        public DesignTablePane(TableMetaData tableMetaData, List<ColumnMetaData> columnMetaDatas)
        {
                this.tableMetaData = tableMetaData;
                this.columnMetaDatas = columnMetaDatas;

                setupToolBar();
                setupTableView();

                setTop(toolBar);
                setCenter(tableView);
        }

        private void setupToolBar()
        {
                Button save = VFX.newIconButton("保存", "save");
                Button plus = VFX.newIconButton("新增行", "plus");
                Button minus = VFX.newIconButton("删除行", "minus");

                toolBar.getItems().addAll(
                        save,
                        plus,
                        minus
                );
        }

        private void setupTableView()
        {
                tableView.setEditable(true);
                tableView.getSelectionModel().setCellSelectionEnabled(true);
                tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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

                name.setCellFactory(c -> new VStringEditingTableCell<>());
                type.setCellFactory(c -> new VStringEditingTableCell<>());
                defaultValue.setCellFactory(c -> new VStringEditingTableCell<>());
                length.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));
                scale.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));
                comment.setCellFactory(c -> new VStringEditingTableCell<>());

                nullable.setCellFactory(c -> new VCheckBoxTableCell<>());
                primary.setCellFactory(c -> new VCheckBoxTableCell<>());
                autoIncrement.setCellFactory(c -> new VCheckBoxTableCell<>());

                // 初始化宽度
                name.setPrefWidth(150);
                type.setPrefWidth(100);
                length.setPrefWidth(100);
                scale.setPrefWidth(100);
                defaultValue.setPrefWidth(200);
                nullable.setPrefWidth(120);
                primary.setPrefWidth(100);
                autoIncrement.setPrefWidth(100);
                comment.setPrefWidth(200);

                tableView.getColumns().addAll(
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

                tableView.getItems().addAll(FXCollections.observableArrayList(columnMetaDatas));
        }
}
