package com.changhong.openvdb.app.core.event;

import com.changhong.openvdb.app.ui.navigator.node.VDBConnectionNode;
import com.changhong.openvdb.core.model.ScriptFile;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenQueryScriptEvent extends Event
{
        public VDBConnectionNode connection;
        public ScriptFile scriptFile;

        public OpenQueryScriptEvent(VDBConnectionNode connection)
        {
                this.connection = connection;
        }

        public OpenQueryScriptEvent(ScriptFile scriptFile)
        {
                this.scriptFile = scriptFile;
        }
}
