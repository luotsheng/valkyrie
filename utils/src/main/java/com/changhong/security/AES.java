package com.changhong.security;

/**
 * `AES` 接口定义了用于 AES 加密和解密的方法，支持对字符串和字节数组的处理。
 *
 * <p>该接口的实现类需要提供 AES 加密和解密功能，确保数据的安全性和机密性。
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public interface AES {

    /**
     * #brief: 使用指定密钥对字符串进行 AES 加密
     *
     * @param data 要进行 AES 加密的字符串
     * @param secret 用于加密的密钥
     * @return 加密后的字符串
     */
    String encrypt(String data, String secret);

    /**
     * #brief: 使用指定密钥对字节数组进行 AES 加密
     *
     * @param bytes 要进行 AES 加密的字节数组
     * @param secret 用于加密的密钥
     * @return 加密后的字节数组
     */
    String encrypt(byte[] bytes, String secret);

    /**
     * #brief: 使用指定密钥对字符串进行 AES 解密
     *
     * @param data 要进行 AES 解密的字符串
     * @param secret 用于解密的密钥
     * @return 解密后的字符串
     */
    String decrypt(String data, String secret);

    /**
     * #brief: 使用指定密钥对字节数组进行 AES 解密
     *
     * @param bytes 要进行 AES 解密的字节数组
     * @param secret 用于解密的密钥
     * @return 解密后的字节数组
     */
    byte[] decrypt(byte[] bytes, String secret);

}


