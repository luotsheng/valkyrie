package com.changhong.opendb.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class FileUtils
{
        public static void forceDelete(File file)
        {
                forceDelete(file.getPath());
        }

        /**
         * 强制删除文件
         */
        @SuppressWarnings({"resource", "CodeBlock2Expr"})
        public static void forceDelete(String pathname)
        {
                Path path = Paths.get(pathname);

                if (Files.isRegularFile(path)) {
                        Catcher.tryCall(() -> Files.deleteIfExists(path));
                        return;
                }

                Catcher.tryCall(() -> {
                        Files.walk(path)
                                .sorted(Comparator.reverseOrder())
                                .forEach(pathVal -> Catcher.tryCall(() -> Files.delete(pathVal)));
                });
        }
}
