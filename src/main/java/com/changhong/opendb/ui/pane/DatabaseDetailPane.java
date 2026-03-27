package com.changhong.opendb.ui.pane;

import com.changhong.opendb.driver.Table;
import com.changhong.opendb.resource.ResourceManager;
import com.changhong.opendb.ui.widgets.DateCell;
import com.changhong.opendb.ui.widgets.TableViewFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.Date;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class DatabaseDetailPane extends BorderPane
{
        private final TableView<Table> table;

        private TableColumn<Table, String> name;
        private TableColumn<Table, Date> createTime;
        private TableColumn<Table, Date> updateTime;
        private TableColumn<Table, String> engine;
        private TableColumn<Table, String> rows;
        private TableColumn<Table, String> comment;

        public DatabaseDetailPane()
        {
                table = TableViewFactory.createTable();

                Platform.runLater(() -> table.getStyleClass().add("no-line-table"));

                // setup
                initializeColumn();
                setupCellFactory();

                setCenter(table);
        }

        @SuppressWarnings("unchecked")
        private void initializeColumn()
        {
                // 列
                name = TableViewFactory.createColumn("名称");
                createTime = TableViewFactory.createColumn("创建时间");
                updateTime = TableViewFactory.createColumn("更新时间");
                engine = TableViewFactory.createColumn("存储引擎");
                rows = TableViewFactory.createColumn("数据条数");
                comment = TableViewFactory.createColumn("注释");

                // 属性配置
                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                createTime.setCellValueFactory(new PropertyValueFactory<>("createTime"));
                updateTime.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
                engine.setCellValueFactory(new PropertyValueFactory<>("engine"));
                rows.setCellValueFactory(new PropertyValueFactory<>("rows"));
                comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

                // 初始化宽度
                name.setPrefWidth(450);
                createTime.setPrefWidth(230);
                updateTime.setPrefWidth(230);
                engine.setPrefWidth(120);
                rows.setPrefWidth(100);
                comment.setPrefWidth(600);

                // 绑定列
                table.getColumns().addAll(
                        name,
                        createTime,
                        updateTime,
                        engine,
                        rows,
                        comment
                );
        }

        private void setupCellFactory()
        {
                name.setCellFactory(col -> new TableCell<>()
                {
                        @Override
                        protected void updateItem(String item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (item != null) {
                                        setText(item);
                                        setGraphic(ResourceManager.use("table"));
                                }
                        }
                });

                createTime.setCellFactory(col -> new DateCell<>());
                updateTime.setCellFactory(col -> new DateCell<>());
        }

        public void update(List<Table> tables)
        {
                table.setItems(FXCollections.observableArrayList(tables));
        }
}
