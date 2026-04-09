package com.changhong.opendb.app.repository;

import com.changhong.opendb.app.Users;
import com.changhong.opendb.app.model.QueryInfo;
import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import com.changhong.opendb.app.ui.navigator.node.ODBNDatabase;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialogHelper;

import java.io.File;
import java.io.FileWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.changhong.string.StringStaticize.strwfmt;

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
                        strwfmt("%s/%s/%s/%s.sql", Users.connectionDir, conn, db, name)
                );

                return saveQueryScript(sqlFile, content);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static File saveQueryScript(File sqlFile, String content)
        {
                VFXDialogHelper.runWith(() -> {
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
                        strwfmt("%s/%s/%s",
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
