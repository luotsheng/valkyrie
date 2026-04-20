package com.changhong.openvdb.app.event.workbench;

import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.explorer.UIConnectionNode;

/**
 * 连接打开成功通知
 *
 * @author Luo Tiansheng
 * @since 2026/4/20
 */
public class ConnectionOpenedNotifyEvent extends Event
{
        public final UIConnectionNode connection;

        public ConnectionOpenedNotifyEvent(UIConnectionNode connection)
        {
                this.connection = connection;
        }
}
