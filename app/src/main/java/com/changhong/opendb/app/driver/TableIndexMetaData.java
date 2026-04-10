package com.changhong.opendb.app.driver;

import com.changhong.collection.Lists;
import com.changhong.security.Codec;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static com.changhong.string.StringStaticize.*;

/**
 * @author Luo Tiansheng
 * @since 2026/4/4
 */
public class TableIndexMetaData
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

        /**
         * 索引列
         */
        @Getter
        @Setter
        private List<TableIndexColumn> columnMetaDatas = Lists.newArrayList();

        /** 是否调用了完整性校验码生成 */
        private boolean isFinalIntegrityCode = false;

        /**
         * 用于校验类是否被篡改
         */
        private String integrityCode;

        /**
         * 给这个类做一个简单的签名，用于后续校验字段是否存在
         * 更改。
         */
        public String computeIntegrityCode()
        {
                return Codec.toByteHex(
                        (strip(name) + strip(columnsText) + strip(type) + visible).getBytes()
                );
        }

        /**
         * 生成完整性校验码，这个方法只允许被调用一次
         */
        public void finalIntegrityCode()
        {
                if (isFinalIntegrityCode)
                        throw new IllegalCallerException("Illegal call finalIntegrityCode()");

                integrityCode = computeIntegrityCode();
                isFinalIntegrityCode = true;
        }

        /**
         * 校验签名是否一致
         */
        public boolean isIntegrityValid()
        {
                return streq(computeIntegrityCode(), integrityCode);
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
