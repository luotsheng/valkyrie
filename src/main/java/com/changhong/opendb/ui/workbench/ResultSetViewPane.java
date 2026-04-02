package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.driver.QueryResultSet;
import com.changhong.opendb.ui.widgets.VFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.util.List;

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
        private TableView<List<String>> tableView = VFX.newTableView();

        private TablePosition<?, ?> start;

        public ResultSetViewPane()
        {
                setupTableView();
                resultSetTab.setContent(tableView);
                setCenter(tabPane);
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

}
