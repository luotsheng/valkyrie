package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.model.ConnectionInfo;
import com.changhong.opendb.app.model.QueryInfo;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class NewQueryScriptEvent extends Event
{
        public ConnectionInfo connectionInfo;
        public QueryInfo queryInfo;

        public NewQueryScriptEvent(ConnectionInfo info)
        {
                this.connectionInfo = info;
        }

        public NewQueryScriptEvent(QueryInfo info)
        {
                this.queryInfo = info;
        }
}
