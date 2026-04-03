package com.changhong.opendb.core.event;

import com.changhong.opendb.driver.executor.SQLExecutor;
import com.changhong.opendb.driver.TableMetaData;
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
