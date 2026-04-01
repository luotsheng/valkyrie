package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.app.Application;
import com.changhong.opendb.core.event.EventBus;
import com.changhong.opendb.core.event.NewQueryResultSetPaneEvent;
import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.TableInfo;
import com.changhong.opendb.resource.Assets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNTable extends ODBNode
{
        private final JdbcTemplate jdbcTemplate;
        private final ODBNDatabase database;
        private final TableInfo table;

        public ODBNTable(JdbcTemplate jdbcTemplate,
                         ODBNDatabase database,
                         TableInfo table)
        {
                super(table.getName());
                this.database = database;
                setGraphic(Assets.use("table"));
                this.jdbcTemplate = jdbcTemplate;
                this.table = table;
        }

        @Override
        public void onSelectedEvent()
        {
                database.onSelected();
        }

        @Override
        protected ContextMenu registerContextMenu()
        {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem copyTableNameItem = new MenuItem("复制表名");
                copyTableNameItem.setOnAction(event -> {
                        Application.copyToClipboard(getName());
                });

                contextMenu.getItems().addAll(copyTableNameItem);

                return contextMenu;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                EventBus.publish(new NewQueryResultSetPaneEvent(jdbcTemplate, database.getName(), table));
        }
}