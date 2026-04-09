package com.changhong.opendb.app.repository;

import com.changhong.opendb.app.Users;
import com.changhong.opendb.app.model.ConnectionInfo;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialog;
import com.changhong.opendb.app.utils.FileUtils;
import com.changhong.opendb.app.utils.JSONUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;

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
                        VFXDialog.openError(name + "已存在！");

                dir.mkdirs();

                if (!odbc.exists())
                        VFXDialog.tryCall(odbc::createNewFile);

                try (FileOutputStream fos = new FileOutputStream(odbc)) {

                        fos.write(content.getBytes(StandardCharsets.UTF_8));

                } catch (IOException e) {
                        /* 删除文件夹 */
                        FileUtils.forceDelete(dir);
                        VFXDialog.openError(e);
                }
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void updateConnection(String oldName, String newName, String content)
        {
                File newDir = new File(Users.connectionDir, newName);

                if (!Objects.equals(oldName, newName)) {
                        File oldDir = new File(Users.connectionDir, oldName);
                        oldDir.renameTo(newDir);
                }

                File odbc = new File(newDir, ".odbc");

                FileUtils.forceDelete(odbc);

                saveConnection(newName, content);
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
                                VFXDialog.openError(e);
                        }
                }

                Collator collator = Collator.getInstance(Locale.CHINA);
                ret.sort(Comparator.comparing(ConnectionInfo::getName, collator));

                return ret;
        }

}
