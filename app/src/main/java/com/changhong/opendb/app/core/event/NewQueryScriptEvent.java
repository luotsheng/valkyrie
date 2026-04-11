package com.changhong.opendb.app.core.event;

import com.changhong.opendb.app.model.ConnectionProperty;
import com.changhong.opendb.app.model.QueryInfo;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class NewQueryScriptEvent extends Event
{
        public ConnectionProperty connectionInfo;
        public QueryInfo queryInfo;

        public NewQueryScriptEvent(ConnectionProperty info)
        {
                this.connectionInfo = info;
        }

        public NewQueryScriptEvent(QueryInfo info)
        {
                this.queryInfo = info;
        }
}
