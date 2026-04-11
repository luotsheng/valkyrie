package com.changhong.driver.api;

import com.changhong.security.Codec;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.changhong.string.StringStaticize.strip;

/**
 * @author Luo Tiansheng
 * @since 2026/4/4
 */
@Getter
@Setter
public class Index extends Sealable
{
        /**
         * 索引名
         */
        private String name;

        /**
         * 字段文本
         */
        private String columnsText;

        /**
         * 索引类型
         */
        private String type;

        /**
         * 是否可见
         */
        private boolean visible;

        /**
         * 原始名称
         */
        private String originalName;

        @Override
        public String computeIntegrityCode()
        {
                return Codec.toByteHex(
                        (strip(name) +
                         strip(columnsText) +
                         strip(type)).getBytes()
                );
        }

        public void generateColumnText(List<String> columns)
        {
                StringBuilder builder = new StringBuilder();

                for (String col : columns) {
                        builder.append("`").append(col).append("`");
                        builder.append(", ");
                }

                builder.deleteCharAt(builder.length() - 2);
                columnsText = builder.toString();
        }
}
