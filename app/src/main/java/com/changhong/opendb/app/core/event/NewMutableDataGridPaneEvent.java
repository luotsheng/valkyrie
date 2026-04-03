package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.driver.TableMetaData;
import lombok.AllArgsConstructor;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
@AllArgsConstructor
public class NewMutableDataGridPaneEvent extends Event
{
        public SQLExecutor sqlExecutor;
        public String database;
        public TableMetaData info;
}
