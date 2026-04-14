package com.changhong.openvdb.app.event;

import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;

import static com.changhong.utils.string.StaticLibrary.strfmt;

/**
 * 打开设计表面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenTableDesignerPaneEvent extends Event
{
        public final String connectionName;
        public final Session session;
        public final Driver driver;
        public final Table table;

        public OpenTableDesignerPaneEvent(String connectionName,
                                          Session session,
                                          Driver driver,
                                          Table table)
        {
                this.connectionName = connectionName;
                this.session = session;
                this.driver = driver;
                this.table = table;
        }

        public String id()
        {
                return strfmt("%s@%s(%s)",
                        table.getName(),
                        session.catalog(),
                        connectionName);
        }

}
