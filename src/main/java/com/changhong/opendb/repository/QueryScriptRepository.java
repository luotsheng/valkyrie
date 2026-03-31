package com.changhong.opendb.repository;

import com.changhong.opendb.Users;
import com.changhong.opendb.utils.Catcher;

import java.io.File;
import java.io.FileWriter;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * 脚本数据
 *
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class QueryScriptRepository
{
        public static void saveQueryScript(String conn,
                                           String db,
                                           String name,
                                           String content)
        {
                File sqlFile = new File(
                        strfmt("%s/%s/%s/%s", Users.connectionDir, conn, db, name)
                );

                saveQueryScript(sqlFile, content);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void saveQueryScript(File sqlFile, String content)
        {
                Catcher.tryCall(() -> {
                        if (!sqlFile.exists())
                                sqlFile.createNewFile();

                        try (FileWriter fw = new FileWriter(sqlFile)) {
                                fw.write(content);
                        }
                });
        }
}
