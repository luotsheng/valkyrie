package com.changhong.opendb.ui.pane;

import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.NewQueryResultSetPaneEvent;
import com.changhong.opendb.driver.TableMetadata;
import com.changhong.opendb.resource.Assets;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.ui.widgets.*;
import com.changhong.opendb.ui.widgets.DateCell;
import com.changhong.opendb.utils.Catcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Date;
import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class DatabaseDetailPane extends DetailPane
{
        private final TableView<TableMetadata> tableView;
        private final ToolBar toolBar;

        private final ODBNDatabase database;

        private TableColumn<TableMetadata, String> name;
        private TableColumn<TableMetadata, Date> createTime;
        private TableColumn<TableMetadata, Date> updateTime;
        private TableColumn<TableMetadata, String> engine;
        private TableColumn<TableMetadata, Float> size;
        private TableColumn<TableMetadata, String> rows;
        private TableColumn<TableMetadata, String> comment;

        private ObservableList<TableMetadata> obs;

        public DatabaseDetailPane(ODBNDatabase database)
        {
                this.database = database;

                tableView = VFX.newTableView();
                toolBar = new ToolBar();

                // setup
                setupToolBar();
                initializeColumn();
                setupCellFactory();

                setTop(toolBar);
                setCenter(tableView);
        }

        private void setupToolBar()
        {
                toolBar.setOrientation(Orientation.HORIZONTAL);

                Button modifyTable = VFX.newIconButton("编辑表", "modify");
                Button newTable = VFX.newIconButton("创建表", "plus");
                Button delTable = VFX.newIconButton("删除表", "minus");
                delTable.setOnAction(event -> deleteTable());

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                TextField search = new TextField();
                search.setPromptText("搜索...");
                search.setPrefWidth(300);

                HBox searchBox = new HBox(5, Assets.use("search"), search);
                searchBox.setAlignment(Pos.CENTER_LEFT);

                toolBar.getItems().addAll(
                        modifyTable,
                        newTable,
                        delTable,
                        new VSeparator(),
                        spacer,
                        searchBox);
        }

        private void deleteTable()
        {
                TableMetadata table = tableView.getSelectionModel().getSelectedItem();

                if (ConfirmDialog.showCheckDialog("确认删除：%s？", table.getName())) {
                        Catcher.tryCall(() -> database.drop(table));
                        database.refreshTableNode();
                }
        }

        @SuppressWarnings("unchecked")
        private void initializeColumn()
        {
                // 列
                name = VFX.newTableColumn("名称");
                createTime = VFX.newTableColumn("创建时间");
                updateTime = VFX.newTableColumn("更新时间");
                engine = VFX.newTableColumn("存储引擎");
                size = VFX.newTableColumn("表大小");
                rows = VFX.newTableColumn("数据条数");
                comment = VFX.newTableColumn("注释");

                // 属性配置
                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                createTime.setCellValueFactory(new PropertyValueFactory<>("createTime"));
                updateTime.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
                engine.setCellValueFactory(new PropertyValueFactory<>("engine"));
                size.setCellValueFactory(new PropertyValueFactory<>("size"));
                rows.setCellValueFactory(new PropertyValueFactory<>("rows"));
                comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

                // 初始化宽度
                name.setPrefWidth(450);
                createTime.setPrefWidth(230);
                updateTime.setPrefWidth(230);
                engine.setPrefWidth(120);
                size.setPrefWidth(100);
                rows.setPrefWidth(100);
                comment.setPrefWidth(600);

                // 绑定列
                tableView.getColumns().addAll(
                        name,
                        createTime,
                        updateTime,
                        engine,
                        size,
                        rows,
                        comment
                );

                tableView.setRowFactory(tv -> {
                        TableRow<TableMetadata> row = new TableRow<>();

                        row.setOnMouseClicked(e -> {

                                if (e.getClickCount() == 2 && !row.isEmpty()) {
                                        TableMetadata data = row.getItem();
                                        EventBus.publish(new NewQueryResultSetPaneEvent(database.getSqlExecutor(), database.getName(), data));
                                }

                        });

                        return row;
                });

                tableView.getColumns().forEach(col -> col.setReorderable(false));
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
                                        setGraphic(Assets.use("table"));
                                }
                        }
                });

                size.setCellFactory(col -> new TableCell<>()
                {
                        @Override
                        protected void updateItem(Float item, boolean empty)
                        {
                                super.updateItem(item, empty);

                                if (item != null) {
                                        String unit = "K";
                                        double value = item;

                                        if (item > (1024 * 1024)) {
                                                value = (item / 1024 / 1024);
                                                unit = "G";
                                        } else if (item > 1024) {
                                                value = (item / 1024);
                                                unit = "M";
                                        }

                                        setText(((int) value) + unit);
                                }
                        }
                });

                createTime.setCellFactory(col -> new DateCell<>());
                updateTime.setCellFactory(col -> new DateCell<>());
        }

        public void update(List<TableMetadata> tables)
        {
                if (obs == null) {
                        obs = FXCollections.observableArrayList();
                        tableView.setItems(obs);
                }

                obs.setAll(tables);
                tableView.refresh();
        }
}
