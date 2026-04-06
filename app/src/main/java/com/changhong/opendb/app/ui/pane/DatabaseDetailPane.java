package com.changhong.opendb.app.ui.pane;

import com.changhong.opendb.app.core.event.EventBus;
import com.changhong.opendb.app.core.event.NewMutableDataGridPaneEvent;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.app.ui.widgets.*;
import com.changhong.opendb.app.ui.widgets.VFXDateTableCell;
import com.changhong.opendb.app.utils.Catcher;
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
        private final TableView<TableMetaData> tableView;
        private final ToolBar toolBar;

        private final ODBNDatabase database;

        private TableColumn<TableMetaData, String> name;
        private TableColumn<TableMetaData, Date> createTime;
        private TableColumn<TableMetaData, Date> updateTime;
        private TableColumn<TableMetaData, String> engine;
        private TableColumn<TableMetaData, Float> size;
        private TableColumn<TableMetaData, String> rows;
        private TableColumn<TableMetaData, String> comment;

        private ObservableList<TableMetaData> obs;

        public DatabaseDetailPane(ODBNDatabase database)
        {
                this.database = database;

                tableView = new VFXTableView<>();
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
                        new VFXSeparator(),
                        spacer,
                        searchBox);
        }

        private void deleteTable()
        {
                TableMetaData table = tableView.getSelectionModel().getSelectedItem();

                if (ConfirmDialog.showCheckDialog("确认删除：%s？", table.getName())) {
                        Catcher.tryCall(() -> database.drop(table));
                        database.refreshTableNode();
                }
        }

        @SuppressWarnings("unchecked")
        private void initializeColumn()
        {
                // 列
                name = new VFXTableColumn<>("名称");
                createTime = new VFXTableColumn<>("创建时间");
                updateTime = new VFXTableColumn<>("更新时间");
                engine = new VFXTableColumn<>("存储引擎");
                size = new VFXTableColumn<>("表大小");
                rows = new VFXTableColumn<>("数据条数");
                comment = new VFXTableColumn<>("注释");

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
                        TableRow<TableMetaData> row = new TableRow<>();

                        row.setOnMouseClicked(e -> {

                                if (e.getClickCount() == 2 && !row.isEmpty()) {
                                        TableMetaData data = row.getItem();
                                        EventBus.publish(new NewMutableDataGridPaneEvent(database.getSqlExecutor(), database.getName(), data));
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

                createTime.setCellFactory(col -> new VFXDateTableCell<>());
                updateTime.setCellFactory(col -> new VFXDateTableCell<>());
        }

        public void update(List<TableMetaData> tables)
        {
                if (obs == null) {
                        obs = FXCollections.observableArrayList();
                        tableView.setItems(obs);
                }

                obs.setAll(tables);
                tableView.refresh();
        }
}
