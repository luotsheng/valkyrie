package com.changhong.opendb;

import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;

/**
 * 用户数据
 *
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class Users
{
        public static final String USER_HOME = System.getProperty("user.home");

        private static final String BASE_SAVE_PATH = USER_HOME + "/.odb";
        private static final String CONN_SAVE_PATH = BASE_SAVE_PATH + "/C";

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void saveConnection(String name, String content)
        {
                File dir = new File(CONN_SAVE_PATH + "/" + name);
                File odbc = new File(dir, ".odbc");

                if (dir.exists())
                        Catcher.ithrow(new FileAlreadyExistsException(name + "已存在！"));

                dir.mkdirs();

                if (!odbc.exists())
                        Catcher.tryCall(odbc::createNewFile);

                try (FileOutputStream odbcOutputStream = new FileOutputStream(odbc)) {

                        odbcOutputStream.write(content.getBytes(StandardCharsets.UTF_8));

                } catch (IOException e) {
                        /* 删除文件夹 */
                        FileUtils.forceDelete(dir);
                        Catcher.ithrow(e);
                }
        }

}
