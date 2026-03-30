package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.QueryResultSet;
import com.changhong.opendb.driver.TableInfo;
import com.changhong.opendb.ui.widgets.VFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ResultSetTableViewPane extends BorderPane
{
        private final JdbcTemplate jdbcTemplate;
        private final TableInfo tableInfo;
        private final String database;
        private final TableView<List<String>> tableView;

        private int start = 0;
        private int size = 500;

        public ResultSetTableViewPane(JdbcTemplate jdbcTemplate, String database, TableInfo tableInfo)
        {
                this.jdbcTemplate = jdbcTemplate;
                this.tableInfo = tableInfo;
                this.database = database;
                this.tableView = VFX.newTableView();

                setupTableView();
                setCenter(tableView);
        }

        private void setupTableView()
        {
                QueryResultSet rs = jdbcTemplate.selectByPage(database, tableInfo.getName(), start, size);

                for (int i = 0; i < rs.getColumns().size(); i++)
                {
                        int index = i;

                        String colText = rs.getColumns().get(i);

                        TableColumn<List<String>, String> col =
                                new TableColumn<>(colText);

                        col.setPrefWidth(calcColWidth(colText, rs.getRows(), i));
                        col.setMaxWidth(1000);
                        col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));

                        tableView.getColumns().add(col);
                }

                tableView.setItems(
                        FXCollections.observableArrayList(rs.getRows())
                );
        }

        private static int calcColWidth(String colText, List<List<String>> values, int index)
        {
                int V = 12, MAX = 200;
                int CM = colText.length();
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
