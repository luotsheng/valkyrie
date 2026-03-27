package com.changhong.opendb.ui.navigator.node;

import com.changhong.opendb.driver.Table;
import com.changhong.opendb.driver.datasource.DataSourceProvider;
import com.changhong.opendb.resource.ResourceManager;
import javafx.scene.input.MouseEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
@SuppressWarnings("FieldCanBeLocal")
public class ODBNTable extends ODBNode
{
        private final DataSourceProvider dataSource;
        private final Table table;

        public ODBNTable(DataSourceProvider dataSource, Table table)
        {
                super(table.getName());
                setGraphic(ResourceManager.use("table"));
                this.dataSource = dataSource;
                this.table = table;
        }

        @Override
        public void onSelectedEvent()
        {
                /* DO NOTHING */
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                /* DO NOTHING */
        }
}
