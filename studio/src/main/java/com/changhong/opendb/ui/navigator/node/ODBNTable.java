package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.app.Application;
import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.NewMutableDataGridPaneEvent;
import com.changhong.opendb.core.event.OpenDesignTablePaneEvent;
import com.changhong.opendb.driver.ColumnMetaData;
import com.changhong.opendb.driver.executor.SQLExecutor;
import com.changhong.opendb.driver.TableMetaData;
import com.changhong.opendb.resource.Assets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNTable extends ODBNode
{
        private final SQLExecutor sqlExecutor;
        private final ODBNDatabase database;
        private final TableMetaData table;

        public ODBNTable(SQLExecutor sqlExecutor,
                         ODBNDatabase database,
                         TableMetaData table)
        {
                super(table.getName());
                this.database = database;
                setGraphic(Assets.use("table"));
                this.sqlExecutor = sqlExecutor;
                this.table = table;
        }

        @Override
        public void onSelectedEvent()
        {
                // database.onSelected();
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem copyTableNameItem = new MenuItem("复制表名");
                copyTableNameItem.setOnAction(event -> {
                        Application.copyToClipboard(getName());
                });

                MenuItem designTableItem = new MenuItem("设计表");
                designTableItem.setOnAction(event -> {
                        List<ColumnMetaData> columns = sqlExecutor.getColumns(table);
                        EventBus.publish(new OpenDesignTablePaneEvent(database.getConnection().getName(),
                                table,
                                columns));
                });

                contextMenu.getItems().addAll(
                        copyTableNameItem,
                        designTableItem
                );

                return contextMenu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                EventBus.publish(new NewMutableDataGridPaneEvent(sqlExecutor, database.getName(), table));
        }
}