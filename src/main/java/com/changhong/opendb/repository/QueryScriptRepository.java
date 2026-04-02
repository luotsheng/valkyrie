package com.changhong.opendb.repository;

import com.changhong.opendb.Users;
import com.changhong.opendb.model.QueryInfo;
import com.changhong.opendb.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.utils.Catcher;

import java.io.File;
import java.io.FileWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.changhong.opendb.utils.StringUtils.strfmt;

/**
 * 脚本数据
 *
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class QueryScriptRepository
{
        public static File saveQueryScript(String conn,
                                           String db,
                                           String name,
                                           String content)
        {
                File sqlFile = new File(
                        strfmt("%s/%s/%s/%s.sql", Users.connectionDir, conn, db, name)
                );

                return saveQueryScript(sqlFile, content);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static File saveQueryScript(File sqlFile, String content)
        {
                Catcher.tryCall(() -> {
                        if (!sqlFile.exists()) {
                                sqlFile.getParentFile().mkdirs();
                                sqlFile.createNewFile();
                        }

                        try (FileWriter fw = new FileWriter(sqlFile)) {
                                fw.write(content);
                        }
                });

                return sqlFile;
        }

        public static List<QueryInfo> loadQueryInfo(ODBNConnection connection, ODBNDatabase database)
        {
                List<QueryInfo> queryInfos = new ArrayList<>();

                File sqlDir = new File(
                        strfmt("%s/%s/%s",
                                Users.connectionDir,
                                connection.getName(),
                                database.getName())
                );

                File[] files = sqlDir.listFiles();
                if (files == null)
                        return queryInfos;

                for (File file : files)
                        queryInfos.add(new QueryInfo(connection, database, file));

                Collator collator = Collator.getInstance(Locale.CHINA);
                queryInfos.sort(Comparator.comparing(QueryInfo::getName, collator));

                return queryInfos;
        }
}
