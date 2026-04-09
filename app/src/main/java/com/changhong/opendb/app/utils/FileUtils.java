package com.changhong.opendb.app.utils;

import com.changhong.opendb.app.ui.widgets.dialog.VFXDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class FileUtils
{
        public static boolean isDeepEmptyDirectory(File file)
        {
                return isDeepEmptyDirectory(file.toPath());
        }

        public static boolean isDeepEmptyDirectory(Path path)
        {
                try (Stream<Path> stream = Files.walk(path)) {

                        return stream
                                .filter(p -> !Files.isDirectory(p))
                                .findFirst()
                                .isEmpty();

                } catch (IOException e) {
                        VFXDialog.openError(e);
                }

                return false;
        }

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
                        VFXDialog.tryCall(() -> Files.deleteIfExists(path));
                        return;
                }

                VFXDialog.tryCall(() -> {
                        Files.walk(path)
                                .sorted(Comparator.reverseOrder())
                                .forEach(pathVal -> VFXDialog.tryCall(() -> Files.delete(pathVal)));
                });
        }
}
