package com.changhong.openvdb.app.core.event;

import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;
import lombok.AllArgsConstructor;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
@AllArgsConstructor
public class OpenDataGridPaneEvent extends Event
{
        public Session session;
        public Driver driver;
        public String databaseName;
        public Table table;
        public String connectionName;
}
