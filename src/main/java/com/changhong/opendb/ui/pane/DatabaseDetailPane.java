package com.changhong.opendb.ui.pane;

import com.changhong.opendb.driver.Table;
import com.changhong.opendb.resource.ResourceManager;
import com.changhong.opendb.ui.widgets.DateCell;
import com.changhong.opendb.ui.widgets.VFX;
import com.changhong.opendb.ui.widgets.VSeparator;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
public class DatabaseDetailPane extends BorderPane
{
        private final TableView<Table> table;
        private final ToolBar toolBar;

        private TableColumn<Table, String> name;
        private TableColumn<Table, Date> createTime;
        private TableColumn<Table, Date> updateTime;
        private TableColumn<Table, String> engine;
        private TableColumn<Table, Float> size;
        private TableColumn<Table, String> rows;
        private TableColumn<Table, String> comment;

        public DatabaseDetailPane()
        {
                table = VFX.newTableView();
                toolBar = new ToolBar();

                // setup
                setupToolBar();
                initializeColumn();
                setupCellFactory();

                setTop(toolBar);
                setCenter(table);
        }

        private void setupToolBar()
        {
                toolBar.setOrientation(Orientation.HORIZONTAL);

                Button modifyTable = VFX.newIconButton("编辑表", "modify");
                Button newTable = VFX.newIconButton("创建表", "plus");
                Button delTable = VFX.newIconButton("删除表", "minus");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                TextField search = new TextField();
                search.setPromptText("搜索...");
                search.setPrefWidth(300);

                HBox searchBox = new HBox(5, ResourceManager.use("search"), search);
                searchBox.setAlignment(Pos.CENTER_LEFT);

                toolBar.getItems().addAll(
                        modifyTable,
                        newTable,
                        delTable,
                        new VSeparator(),
                        spacer,
                        searchBox);
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
                table.getColumns().addAll(
                        name,
                        createTime,
                        updateTime,
                        engine,
                        size,
                        rows,
                        comment
                );

                table.getColumns().forEach(col -> col.setReorderable(false));
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

        public void update(List<Table> tables)
        {
                table.setItems(FXCollections.observableArrayList(tables));
        }
}
