package com.changhong.openvdb.core.repository;

import com.changhong.openvdb.core.Users;
import com.changhong.openvdb.core.exception.CoreException;
import com.changhong.openvdb.core.model.ConnectionProfile;
import com.changhong.openvdb.core.utils.FileUtils;
import com.changhong.openvdb.core.utils.JSONUtils;
import com.changhong.utils.Captor;
import com.changhong.utils.io.UFile;

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
                File vdbc = new File(dir, ".vdbc");

                if (vdbc.exists())
                        throw new CoreException(name + "已存在！");

                dir.mkdirs();

                if (!vdbc.exists())
                        Captor.call(vdbc::createNewFile);

                try (FileOutputStream fos = new FileOutputStream(vdbc)) {

                        fos.write(content.getBytes(StandardCharsets.UTF_8));

                } catch (IOException e) {
                        /* 删除文件夹 */
                        FileUtils.forceDelete(dir);
                        throw new CoreException(e);
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

                File vdbc = new File(newDir, ".vdbc");

                FileUtils.forceDelete(vdbc);

                saveConnection(newName, content);
        }

        public static void deleteConnection(String name)
        {
                new UFile(Users.connectionDir, name).forceDelete();
        }

        public static List<ConnectionProfile> loadConnections()
        {
                File[] files = Users.connectionDir.listFiles();
                List<ConnectionProfile> ret = new ArrayList<>();

                if (files == null)
                        return ret;

                for (File file : files) {
                        File vdbc = new File(file, ".vdbc");

                        if (FileUtils.isDeepEmptyDirectory(file)) {
                                FileUtils.forceDelete(file);
                                continue;
                        }

                        try (FileInputStream fis = new FileInputStream(vdbc)) {
                                byte[] bytes = fis.readAllBytes();
                                String content = new String(bytes, StandardCharsets.UTF_8);
                                ret.add(JSONUtils.toJavaObject(content, ConnectionProfile.class));
                        } catch (Exception e) {
                                throw new CoreException(e);
                        }
                }

                Collator collator = Collator.getInstance(Locale.CHINA);
                ret.sort(Comparator.comparing(ConnectionProfile::getName, collator));

                return ret;
        }

}
