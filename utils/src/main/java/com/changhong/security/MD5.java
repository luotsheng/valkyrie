package com.changhong.security;

import com.changhong.security.codec.MD5Codec;

/**
 * `MD5` 接口定义了用于生成 MD5 哈希值的多种方法，支持 32 位和 16 位的 MD5 哈希，
 * 并且提供大写和小写的格式转换。
 *
 * <p>该接口的实现类需实现 MD5 加密算法，支持对字符串和字节数组进行加密，
 * 返回不同格式的 MD5 哈希值。
 *
 * @author Luo Tiansheng
 * @since 1.0
 * @see MD5Codec 实现类示例
 */
public interface MD5 {

    /**
     * #brief: 生成字符串的 32 位小写 MD5 哈希值
     *
     * @param source 要进行 MD5 哈希的字符串
     * @return 32 位小写 MD5 哈希值
     */
    String lower32(String source);

    /**
     * #brief: 生成字节数组的 32 位小写 MD5 哈希值
     *
     * @param b 要进行 MD5 哈希的字节数组
     * @return 32 位小写 MD5 哈希值
     */
    String lower32(byte[] b);

    /**
     * #brief: 生成字符串的 16 位小写 MD5 哈希值
     *
     * @param source 要进行 MD5 哈希的字符串
     * @return 16 位小写 MD5 哈希值
     */
    String lower16(String source);

    /**
     * #brief: 生成字节数组的 16 位小写 MD5 哈希值
     *
     * @param b 要进行 MD5 哈希的字节数组
     * @return 16 位小写 MD5 哈希值
     */
    String lower16(byte[] b);

    /**
     * #brief: 生成字符串的 32 位大写 MD5 哈希值
     *
     * @param source 要进行 MD5 哈希的字符串
     * @return 32 位大写 MD5 哈希值
     */
    String upper32(String source);

    /**
     * #brief: 生成字节数组的 32 位大写 MD5 哈希值
     *
     * @param b 要进行 MD5 哈希的字节数组
     * @return 32 位大写 MD5 哈希值
     */
    String upper32(byte[] b);

    /**
     * #brief: 生成字符串的 16 位大写 MD5 哈希值
     *
     * @param source 要进行 MD5 哈希的字符串
     * @return 16 位大写 MD5 哈希值
     */
    String upper16(String source);

    /**
     * #brief: 生成字节数组的 16 位大写 MD5 哈希值
     *
     * @param b 要进行 MD5 哈希的字节数组
     * @return 16 位大写 MD5 哈希值
     */
    String upper16(byte[] b);

}

