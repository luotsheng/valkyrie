package com.changhong.opendb.app.driver;

import com.changhong.collection.Lists;
import com.changhong.security.Codec;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.changhong.string.StringStaticize.strip;

/**
 * @author Luo Tiansheng
 * @since 2026/4/4
 */
public class TableIndexMetaData extends Sealable
{
        /**
         * 索引名
         */
        @Getter
        @Setter
        private String name;

        /**
         * 字段文本
         */
        @Getter
        @Setter
        private String columnsText;

        /**
         * 索引类型
         */
        @Getter
        @Setter
        private String type;

        /**
         * 是否可见
         */
        @Getter
        @Setter
        private boolean visible;

        @Getter
        @Setter
        private String originName;

        @Getter
        @Setter
        private boolean originVisible;

        /**
         * 索引列
         */
        @Getter
        @Setter
        private List<TableIndexColumn> columnMetaDatas = Lists.newArrayList();

        @Override
        public String computeIntegrityCode()
        {
                return Codec.toByteHex(
                        (strip(name) +
                         strip(columnsText) +
                         strip(type)).getBytes()
                );
        }

        public void generateColumnText()
        {
                StringBuilder builder = new StringBuilder();

                for (TableIndexColumn meta : columnMetaDatas) {
                        builder.append("`").append(meta.getName()).append("`");
                        if (meta.getPrefixLength() != null)
                                builder.append("(").append(meta.getPrefixLength()).append(")");
                        builder.append(", ");
                }

                builder.deleteCharAt(builder.length() - 2);
                columnsText = builder.toString();
        }
}
