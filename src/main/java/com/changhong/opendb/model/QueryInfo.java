package com.changhong.opendb.model;

import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * 查询脚本信息
 *
 * @author Luo Tiansheng
 * @since 2026/3/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryInfo
{
        private ODBNConnection connection;
        private ODBNDatabase database;
        private File sqlFile;

        public String getName()
        {
                return sqlFile.getName();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void delete()
        {
                sqlFile.delete();
        }
}
