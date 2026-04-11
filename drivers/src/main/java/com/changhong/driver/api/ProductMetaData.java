package com.changhong.driver.api;

import lombok.Data;

/**
 * 数据库产品信息
 *
 * @author Luo Tiansheng
 * @since 2026/4/6
 */
@Data
public class ProductMetaData
{
        private String productName;

        private String version;

        private int majorVersion;

        private int minorVersion;
}
