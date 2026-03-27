package com.changhong.opendb.navigator.node;

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

        public ODBNTable(DataSourceProvider dataSource, String name)
        {
                super(name);
                setGraphic(ResourceManager.use("table"));
                this.dataSource = dataSource;
        }

        @Override
        public void onMouseDoubleClickEvent(MouseEvent event)
        {
                /* DO NOTHING */
        }
}
