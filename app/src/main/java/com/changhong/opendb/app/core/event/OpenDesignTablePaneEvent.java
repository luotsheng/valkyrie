package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;

import static com.changhong.string.StringStaticize.strwfmt;

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

        public OpenDesignTablePaneEvent(SQLExecutor executor,
                                        String connectionName,
                                        TableMetaData table)
        {
                this.executor = executor;
                this.connectionName = connectionName;
                this.table = table;
        }

        public String id()
        {
                return strwfmt("%s@%s(%s)",
                        table.getName(),
                        table.getDatabase(),
                        connectionName);
        }

}
