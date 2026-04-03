package com.changhong.opendb.ui.pane;

import com.changhong.opendb.driver.ColumnMetaData;
import com.changhong.opendb.driver.TableMetaData;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
@SuppressWarnings("FieldCanBeLocal")
public class DesignTablePane extends DetailPane
{
        private final TableMetaData tableMetaData;

        private final List<ColumnMetaData> columnMetaDatas;

        public DesignTablePane(TableMetaData tableMetaData, List<ColumnMetaData> columnMetaDatas)
        {
                this.tableMetaData = tableMetaData;
                this.columnMetaDatas = columnMetaDatas;
        }
}
