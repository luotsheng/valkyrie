package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;

import java.util.List;

import static com.changhong.string.StringUtils.strwfmt;

/**
 * 打开设计表面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenDesignTablePaneEvent extends Event
{
        public final SQLExecutor executor;
        public final String connectionName;
        public final TableMetaData table;
        public final List<ColumnMetaData> columns;

        public OpenDesignTablePaneEvent(SQLExecutor executor,
                                        String connectionName,
                                        TableMetaData table,
                                        List<ColumnMetaData> columns)
        {
                this.executor = executor;
                this.connectionName = connectionName;
                this.table = table;
                this.columns = columns;
        }

        public String id()
        {
                return strwfmt("%s@%s(%s)",
                        table.getName(),
                        table.getDatabase(),
                        connectionName);
        }

}
