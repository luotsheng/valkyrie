package com.changhong.security;

import com.changhong.security.codec.SHA256Codec;

import java.io.File;

/**
 * `Sha256` 接口定义了用于进行 SHA-256 编码的方法。
 *
 * <p>该接口的实现类需要提供将字符串使用 SHA-256 算法进行编码的功能。
 *
 * @author Luo Tiansheng
 * @since 1.0
 * @see SHA256Codec
 */
public interface SHA256 {

    /**
     * #brief: 对字符串进行 SHA-256 编码
     *
     * @param source 要进行 SHA-256 编码的字符串
     * @return 编码后的 SHA-256 字符串
     */
    String encode(String source);

    /**
     * #brief: 对文件进行 SHA-256 编码
     *
     * @param file 要进行 SHA-256 编码的文件
     * @return 编码后的 SHA-256 字符串
     */
    String encode(File file);

    /**
     * #brief: 对字节数组进行 SHA-256 编码
     *
     * @param source 要进行 SHA-256 编码的字节数组
     * @return 编码后的 SHA-256 字符串
     */
    String encode(byte[] source);

}

