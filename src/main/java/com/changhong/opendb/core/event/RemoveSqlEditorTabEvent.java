package com.changhong.opendb.core.event;

import java.io.File;

/**
 * 移除 sql 编辑器标签事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class RemoveSqlEditorTabEvent extends Event
{
        public final File sqlFile;

        public RemoveSqlEditorTabEvent(File sqlFile)
        {
                this.sqlFile = sqlFile;
        }
}
