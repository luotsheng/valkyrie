package com.changhong.openvdb.app.core.event;

import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;

import static com.changhong.string.StringStaticize.strwfmt;

/**
 * 打开设计表面板事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenDesignTablePaneEvent extends Event
{
        public final String connectionName;
        public final Session session;
        public final Driver driver;
        public final Table table;

        public OpenDesignTablePaneEvent(String connectionName,
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
                return strwfmt("%s@%s(%s)",
                        table.getName(),
                        session.catalog(),
                        connectionName);
        }

}
