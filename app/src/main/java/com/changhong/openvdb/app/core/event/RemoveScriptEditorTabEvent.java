package com.changhong.openvdb.app.core.event;

import java.io.File;

/**
 * 移除脚本编辑器标签事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class RemoveScriptEditorTabEvent extends Event
{
        public final File sqlFile;

        public RemoveScriptEditorTabEvent(File sqlFile)
        {
                this.sqlFile = sqlFile;
        }
}
