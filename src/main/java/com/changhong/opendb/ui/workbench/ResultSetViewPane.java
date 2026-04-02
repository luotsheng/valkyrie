package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.app.Application;
import com.changhong.opendb.driver.QueryResultSet;
import com.changhong.opendb.ui.widgets.VFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

import static com.changhong.opendb.utils.StringUtils.strempty;
import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ResultSetViewPane extends BorderPane
{
        private final TabPane tabPane = new TabPane();
        private final Tab resultSetTab = new Tab();
        private final TableView<List<String>> tableView = VFX.newTableView();
        private final ToolBar toolBar = new ToolBar();
        private final VBox vContainer;

        private final Button plus = VFX.newIconButton("新增数据", "plus");
        private final Button minus = VFX.newIconButton("删除选中行", "minus");
        private final Button check = VFX.newIconButton("应用更改", "check");
        private final Button cross = VFX.newIconButton("取消更改", "cross");
        private final Button reload = VFX.newIconButton("刷新", "reload");

        private TablePosition<?, ?> start;

        public ResultSetViewPane()
        {
                this(true);
        }

        public ResultSetViewPane(boolean isEnableToolBar)
        {
                setupTableView();
                setupToolBar();

                vContainer = new VBox(tableView, toolBar);
                VBox.setVgrow(tableView, Priority.ALWAYS);
                resultSetTab.setContent(vContainer);

                setCenter(tabPane);
        }

        private void setupToolBar()
        {
                minus.setDisable(true);
                check.setDisable(true);
                cross.setDisable(true);
                toolBar.setStyle("-fx-spacing: 2px;");
                toolBar.getItems().addAll(plus, minus, check, cross, reload);
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
                select(resultSetTab);
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

        public void refresh(QueryResultSet qrs)
        {
                tableView.getColumns().clear();
                tableView.getItems().clear();

                if (!tabPane.getTabs().contains(resultSetTab))
                        tabPane.getTabs().addFirst(resultSetTab);

                resultSetTab.setText(strfmt("查询结果集 (%d条)", qrs.getRows().size()));

                for (int i = 0; i < qrs.getColumns().size(); i++) {
                        int index = i;

                        String colText = qrs.getColumns().get(i);

                        TableColumn<List<String>, String> col =
                                new TableColumn<>(colText);

                        col.setPrefWidth(calcColWidth(colText, qrs.getRows(), i));
                        col.setMaxWidth(1000);
                        col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));
                        col.setCellFactory(TextFieldTableCell.forTableColumn());

                        tableView.getColumns().add(col);
                }

                tableView.setItems(
                        FXCollections.observableArrayList(qrs.getRows())
                );
        }

        public void setOnCloseRequest(EventHandler<Event> value)
        {
                resultSetTab.setOnCloseRequest(value);
        }

        private static int calcColWidth(String colText, List<List<String>> values, int index)
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

        public void setToolBarDisable(boolean flag)
        {
                plus.setDisable(flag);
                minus.setDisable(flag);
                check.setDisable(flag);
                cross.setDisable(flag);
                reload.setDisable(flag);
        }

}
