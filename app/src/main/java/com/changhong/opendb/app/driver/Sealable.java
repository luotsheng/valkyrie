package com.changhong.opendb.app.driver;

import static com.changhong.string.StringStaticize.streq;

/**
 * 防子类篡改
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public abstract class Sealable
{
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
        public abstract String computeIntegrityCode();

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
}
