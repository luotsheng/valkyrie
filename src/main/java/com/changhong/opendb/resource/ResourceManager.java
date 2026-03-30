package com.changhong.opendb.resource;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.SVGPath;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 资源管理
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
public class ResourceManager
{
        private static final Map<String, Image> IMAGES = new HashMap<>();

        static {
                loadImages();
        }

        public static ImageView use(String name)
        {
                ImageView imageView = new ImageView(IMAGES.get(name));

                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                return imageView;
        }

        private static void loadImages()
        {
                IMAGES.put("chain", load("/assets/icons/chain.png"));
                IMAGES.put("connect", load("/assets/icons/connect.png"));
                IMAGES.put("database0", load("/assets/icons/database0.png"));
                IMAGES.put("database1", load("/assets/icons/database1.png"));
                IMAGES.put("query", load("/assets/icons/query.png"));
                IMAGES.put("run0", load("/assets/icons/run0.png"));
                IMAGES.put("sql", load("/assets/icons/sql.png"));
                IMAGES.put("table", load("/assets/icons/table.png"));
                IMAGES.put("modify", load("/assets/icons/modify.png"));
                IMAGES.put("plus", load("/assets/icons/plus.png"));
                IMAGES.put("minus", load("/assets/icons/minus.png"));
                IMAGES.put("search", load("/assets/icons/search.png"));

        }

        private static Image load(String path)
        {
                return new Image(Objects.requireNonNull(ResourceManager.class
                                .getResource(path))
                                .toExternalForm());
        }
}
