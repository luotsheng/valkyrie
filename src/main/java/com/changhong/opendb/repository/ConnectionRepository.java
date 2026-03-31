package com.changhong.opendb.repository;

import com.changhong.opendb.Users;
import com.changhong.opendb.model.ConnectionInfo;
import com.changhong.opendb.utils.Catcher;
import com.changhong.opendb.utils.FileUtils;
import com.changhong.opendb.utils.JSONUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接信息
 *
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class ConnectionRepository
{
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void saveConnection(String name, String content)
        {
                File dir = new File(Users.connectionDir, name);
                File odbc = new File(dir, ".odbc");

                if (odbc.exists())
                        Catcher.ithrow(new FileAlreadyExistsException(name + "已存在！"));

                dir.mkdirs();

                if (!odbc.exists())
                        Catcher.tryCall(odbc::createNewFile);

                try (FileOutputStream fos = new FileOutputStream(odbc)) {

                        fos.write(content.getBytes(StandardCharsets.UTF_8));

                } catch (IOException e) {
                        /* 删除文件夹 */
                        FileUtils.forceDelete(dir);
                        Catcher.ithrow(e);
                }
        }

        public static void updateConnection(String name, String content)
        {
                File dir = new File(Users.connectionDir, name);
                File odbc = new File(dir, ".odbc");

                FileUtils.forceDelete(odbc);

                saveConnection(name, content);
        }

        public static List<ConnectionInfo> loadConnections()
        {
                File[] files = Users.connectionDir.listFiles();
                List<ConnectionInfo> ret = new ArrayList<>();

                if (files == null)
                        return ret;

                for (File file : files) {
                        File odbc = new File(file, ".odbc");

                        if (FileUtils.isDeepEmptyDirectory(file)) {
                                FileUtils.forceDelete(file);
                                continue;
                        }

                        try (FileInputStream fis = new FileInputStream(odbc)) {
                                byte[] bytes = fis.readAllBytes();
                                String content = new String(bytes, StandardCharsets.UTF_8);
                                ret.add(JSONUtils.toJavaObject(content, ConnectionInfo.class));
                        } catch (Exception e) {
                                Catcher.ithrow(e);
                        }
                }

                return ret;
        }

}
