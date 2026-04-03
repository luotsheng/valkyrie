package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.app.Application;
import com.changhong.opendb.driver.ColumnMetaData;
import com.changhong.opendb.driver.Row;
import com.changhong.opendb.driver.ShittyMutableDataGrid;
import com.changhong.opendb.ui.widgets.EditingTableCell;
import com.changhong.opendb.ui.widgets.VFX;
import com.changhong.opendb.ui.widgets.VSeparator;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

import static com.changhong.opendb.utils.StringUtils.strempty;
import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class MutableDataGridViewPane extends BorderPane
{
        private final TabPane tabPane = new TabPane();
        private final Tab dataGridTab = new Tab();
        private final TableView<Row> tableView = VFX.newTableView();
        private final ToolBar toolBar = new ToolBar();
        private final VBox vContainer;

        private final Button plus = VFX.newIconButton("新增数据", "plus");
        private final Button minus = VFX.newIconButton("删除选中行", "minus");
        private final Button check = VFX.newIconButton("应用更改", "check");
        private final Button cross = VFX.newIconButton("取消更改", "cross");
        private final Button reload = VFX.newIconButton("刷新", "reload");

        private ShittyMutableDataGrid grid;
        private TablePosition<?, ?> start;

        public MutableDataGridViewPane()
        {
                setupTableView();

                toolBar.setStyle("-fx-spacing: 2px;");
                toolBar.getItems().addAll(
                        plus, minus,
                        new VSeparator(),
                        check, cross, reload);

                vContainer = new VBox(tableView, toolBar);
                VBox.setVgrow(tableView, Priority.ALWAYS);
                dataGridTab.setContent(vContainer);

                setCenter(tabPane);

                updateCheckCross();
                setupToolButtonAction();
        }

        private void setToolButtonStatus(boolean addable, boolean editable)
        {
                /* 如果可新增，说明在表数据页面 */
                if (addable) {
                        tableView.setEditable(true);
                        plus.setDisable(false);
                        minus.setDisable(false);
                        return;
                }

                /* 可编辑但不可新增，说明是查询页 */
                if (editable) {
                        tableView.setEditable(true);
                        minus.setDisable(false);
                        return;
                }

                /* 执行例如关联查询，SHOW 之类的语句不可用 */
                tableView.setEditable(false);
                plus.setDisable(true);
                minus.setDisable(true);
        }

        private void updateCheckCross()
        {
                boolean disable = (grid == null ||
                        !grid.isEmptyUpdateBuffer());

                check.setDisable(disable);
                cross.setDisable(disable);
        }

        private void setupToolButtonAction()
        {
                plus.setOnAction(event -> {
                        grid.addEmptyRow();
                        tableView.getItems().setAll(grid.getRows());
                        tableView.refresh();
                });

                check.setOnAction(event -> applyCheck());
                cross.setOnAction(event -> applyCross());

                reload.setOnAction(event -> reloadAndBlinkTable());
        }

        private void applyCheck()
        {
                grid.flushUpdateBuffer();
                updateCheckCross();
                render(grid);
        }

        private void applyCross()
        {
                grid.clearUpdateBuffer();
                updateCheckCross();
                render(grid);
        }

        private void reloadAndBlinkTable()
        {
                grid.refresh();

                render(grid);

                FadeTransition ft = new FadeTransition(Duration.millis(200), tableView);
                ft.setFromValue(0.5);
                ft.setToValue(1.0);
                ft.setCycleCount(1);
                ft.setAutoReverse(true);

                ft.setOnFinished(event -> tableView.setOpacity(1.0));

                ft.play();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void setupTableView()
        {
                tableView.getStylesheets().add("vfx-table-view");
                tableView.setEditable(true);
                tableView.getSelectionModel().setCellSelectionEnabled(true);
                tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                tableView.setOnMousePressed(event -> {
                        start = getTablePosition(event);
                });

                tableView.setOnMouseDragged(event -> {
                       var cur = getTablePosition(event);

                       if (start != null && cur != null) {
                               tableView.getSelectionModel().clearSelection();
                               tableView.getSelectionModel().selectRange(
                                       start.getRow(), (TableColumn) start.getTableColumn(),
                                       cur.getRow(), cur.getTableColumn()
                               );
                       }
                });

                tableView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        if ((event.isControlDown() || event.isShortcutDown())
                                && event.getCode() == KeyCode.C)
                                copyTableViewSelectedCell();
                });
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private void copyTableViewSelectedCell()
        {
                ObservableList<TablePosition> cells =
                        tableView.getSelectionModel().getSelectedCells();

                if (cells == null || cells.isEmpty())
                        return;

                int start = cells.getFirst().getRow();
                int rows = start + Math.toIntExact(cells.stream().map(TablePosition::getRow).distinct().count());

                FilteredList<TablePosition> filtered = new FilteredList<>(cells, predicate -> true);

                StringBuilder builder = new StringBuilder();

                for (int i = start; i < rows; i++) {
                        int ROW = i;
                        filtered.setPredicate(cell -> cell.getRow() == ROW);

                        filtered.forEach(cell -> {
                                List<String> tableRow = (List<String>) cell.getTableView()
                                        .getItems().get(ROW);
                                String text = tableRow.get(cell.getColumn());

                                if (!strempty(text))
                                        builder.append(text);

                                builder.append("\t");
                        });

                        builder.deleteCharAt(builder.length() - 1);
                        builder.append("\n");
                }

                builder.deleteCharAt(builder.length() - 1);

                Application.copyToClipboard(builder.toString());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private TablePosition getTablePosition(MouseEvent event)
        {
                var pick = event.getPickResult();
                Node node = pick.getIntersectedNode();

                while (node != null && !(node instanceof TableCell<?,?>))
                        node = node.getParent();

                if (node instanceof TableCell cell && !cell.isEmpty()) {
                        return new TablePosition<>(
                                tableView,
                                cell.getIndex(),
                                cell.getTableColumn()
                        );
                }

                return null;
        }

        public void selectResultSetFirst()
        {
                select(dataGridTab);
        }

        public void select(Tab tab)
        {
                if (tabPane.getSelectionModel().getSelectedItem() != tab)
                        tabPane.getSelectionModel().select(tab);
        }

        public void addTab(Tab tab)
        {
                if (!tabPane.getTabs().contains(tab))
                        tabPane.getTabs().addLast(tab);
        }

        /**
         * 当有数据被编辑时触发（不论是否修改）
         */
        private void commit(ModifyCell cell)
        {
                if (cell.isUnmodified())
                        return;

                grid.addUpdateRow(cell.getColumnIndex(), cell.getRowIndex(), cell.getNewValue());
        }

        public void render(ShittyMutableDataGrid grid)
        {
                if (this.grid != grid) {
                        this.grid = grid;
                        this.grid.setUpdateListener(r -> updateCheckCross());
                }

                tableView.getColumns().clear();
                tableView.getItems().clear();

                if (!tabPane.getTabs().contains(dataGridTab))
                        tabPane.getTabs().addFirst(dataGridTab);

                setToolButtonStatus(grid.isAddable(), grid.isEditable());

                dataGridTab.setText(strfmt("查询结果集 (%d条)", grid.getRows().size()));

                for (int i = 0; i < grid.getColumns().size(); i++) {
                        int index = i;

                        ColumnMetaData columnMetaData = grid.getColumns().get(i);
                        StringBuilder labelBuilder = new StringBuilder(columnMetaData.getLabel());

                        if (grid.isEditable()) {
                                labelBuilder.append("\n# ")
                                        .append(columnMetaData.getType())
                                        .append('(')
                                        .append(columnMetaData.getLength())
                                        .append(')');

                                if (columnMetaData.isPrimary())
                                        labelBuilder.append(" ").append("PK");
                        }

                        String label = labelBuilder.toString();

                        TableColumn<Row, String> col =
                                new TableColumn<>(label);

                        col.setEditable(true);
                        col.setPrefWidth(calcColWidth(label, grid.getRows(), i));
                        col.setMaxWidth(1000);
                        col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));

                        col.setCellFactory(c -> new EditingTableCell<>(this::commit));

                        tableView.getColumns().add(col);
                }

                tableView.setItems(
                        FXCollections.observableArrayList(grid.getRows())
                );
        }

        public void setOnCloseRequest(EventHandler<Event> value)
        {
                dataGridTab.setOnCloseRequest(value);
        }

        private static int calcColWidth(String colText, List<Row> values, int index)
        {
                int V = 12, MAX = 200;
                int SCALE = 1;

                if (colText.matches(".*[\\u4e00-\\u9fa5].*"))
                        SCALE = 2;

                int CM = colText.length() * SCALE;
                int CW = CM * V;

                for (List<String> value : values) {
                        String cellValue = value.get(index);

                        if (cellValue == null || cellValue.isEmpty())
                                continue;

                        if (cellValue.length() > CM)
                                CM = cellValue.length();
                }

                int FW = Math.max(CM * V, 64);

                return Math.min(Math.max(CW, FW), MAX); /* px */
        }

}
