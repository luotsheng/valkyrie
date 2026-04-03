package com.changhong.opendb.app.model;

import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.ui.navigator.node.ODBNDatabase;
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

        public String getDatabaseName()
        {
                return database.getName();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void delete()
        {
                sqlFile.delete();
        }
}
