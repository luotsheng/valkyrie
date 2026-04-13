package com.changhong.openvdb.app.event;

import com.changhong.openvdb.app.event.bus.Event;
import com.changhong.openvdb.app.navigator.node.VDBConnectionNode;
import com.changhong.openvdb.core.model.ScriptFile;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class OpenScriptEditorEvent extends Event
{
        public final VDBConnectionNode connection;
        public final ScriptFile scriptFile;

        public OpenScriptEditorEvent(VDBConnectionNode connection)
        {
                this(connection, null);
        }

        public OpenScriptEditorEvent(ScriptFile scriptFile)
        {
                this(null, scriptFile);
        }

        public OpenScriptEditorEvent(VDBConnectionNode connection, ScriptFile scriptFile)
        {
                this.connection = connection;
                this.scriptFile = scriptFile;
        }
}
