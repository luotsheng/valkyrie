package com.changhong.openvdb.app.pane;

import com.changhong.openvdb.app.navigator.node.UITableNode;
import com.changhong.openvdb.app.widgets.VFXIconButton;
import com.changhong.openvdb.app.widgets.VFXSeparator;
import com.changhong.openvdb.driver.api.Table;
import com.changhong.openvdb.app.event.bus.EventBus;
import com.changhong.openvdb.app.event.OpenDataGridPaneEvent;
import com.changhong.openvdb.app.assets.Assets;
import com.changhong.openvdb.app.navigator.node.UICatalogNode;
import com.changhong.openvdb.app.widgets.table.VFXTableColumn;
import com.changhong.openvdb.app.widgets.table.VFXTableView;
import com.changhong.openvdb.app.widgets.table.cell.VFXDateTableCell;
import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;
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
 * Catalog 预览面板
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@SuppressWarnings("FieldCanBeLocal")
public class CatalogBrowserPane extends BrowserPane
{
        private final TableView<Table> tableView;
        private final ToolBar toolBar;

        private final UICatalogNode database;

        private TableColumn<Table, String> name;
        private TableColumn<Table, Date> createTime;
        private TableColumn<Table, Date> updateTime;
        private TableColumn<Table, String> engine;
        private TableColumn<Table, Float> size;
        private TableColumn<Table, String> rows;
        private TableColumn<Table, String> comment;

        private ObservableList<Table> obs;

        public CatalogBrowserPane(UICatalogNode database)
        {
                this.database = database;

                tableView = new VFXTableView<>();
                toolBar = new ToolBar();

                // setup
                setupToolBar();
                initializeColumn();
                setupCellFactory();
                setupTableView();

                setTop(toolBar);
                setCenter(tableView);
        }

        private void setupToolBar()
        {
                toolBar.setOrientation(Orientation.HORIZONTAL);

                Button modifyTable = new VFXIconButton("编辑表", "modify");
                Button newTable = new VFXIconButton("创建表", "plus");
                Button delTable = new VFXIconButton("删除表", "minus");
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

        private void setupTableView()
        {
                tableView.setRowFactory(tv -> {
                        TableRow<Table> r = new TableRow<>();

                        r.itemProperty().addListener((obs, oldItem, table) -> {
                                if (table == null) {
                                        r.setContextMenu(null);
                                        return;
                                }

                                UITableNode uiTableNode = database.getUITableNode(table.getName());
                                if (uiTableNode != null)
                                        r.setContextMenu(uiTableNode.getContextMenu());
                        });

                        return r;
                });
        }

        private void deleteTable()
        {
                Table table = tableView.getSelectionModel().getSelectedItem();

                if (VFXDialogHelper.askDangerous("确认删除：%s？", table.getName())) {
                        VFXDialogHelper.runWith(() -> database.dropTable(table));
                        database.reloadTableNode();
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
                        TableRow<Table> row = new TableRow<>();

                        row.setOnMouseClicked(e -> {

                                if (e.getClickCount() == 2 && !row.isEmpty()) {
                                        Table data = row.getItem();
                                        EventBus.publish(new OpenDataGridPaneEvent(database.getSession(),
                                                database.getDriver(),
                                                database.getName(),
                                                data,
                                                database.getConnection().getName()));
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

        public void update(List<Table> tables)
        {
                if (obs == null) {
                        obs = FXCollections.observableArrayList();
                        tableView.setItems(obs);
                }

                obs.setAll(tables);
                tableView.refresh();
        }
}
