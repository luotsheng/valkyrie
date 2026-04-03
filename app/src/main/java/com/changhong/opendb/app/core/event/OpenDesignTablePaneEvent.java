package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.TableMetaData;

import java.util.List;

import static com.changhong.opendb.app.utils.StringUtils.strfmt;

/**
 * 打开设计表面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenDesignTablePaneEvent extends Event
{
        public final String connectionName;
        public final TableMetaData table;
        public final List<ColumnMetaData> columns;

        public OpenDesignTablePaneEvent(String connectionName,
                                        TableMetaData table,
                                        List<ColumnMetaData> columns)
        {
                this.connectionName = connectionName;
                this.table = table;
                this.columns = columns;
        }

        public String id()
        {
                return strfmt("%s@%s(%s)",
                        table.getName(),
                        table.getDatabase(),
                        connectionName);
        }

}
