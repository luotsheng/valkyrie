package com.changhong.security;

import com.changhong.security.codec.URLCodec;

/**
 * `URL` 接口定义了用于进行 URL 编码和解码的方法，
 * 支持指定字符集的编码方式。
 *
 * <p>该接口的实现类需要提供对字符串进行 URL 编码和解码的功能，
 * 支持通过默认和自定义编码方式处理 URL 字符串。
 *
 * @author Luo Tiansheng
 * @since 1.0
 * @see URLCodec
 */
public interface URL {

    /**
     * #brief: 使用默认字符集对字符串进行 URL 编码
     *
     * @param source 要进行 URL 编码的字符串
     * @return 编码后的 URL 字符串
     */
    String encode(String source);

    /**
     * #brief: 使用指定字符集对字符串进行 URL 编码
     *
     * @param source 要进行 URL 编码的字符串
     * @param enc 编码时使用的字符集
     * @return 编码后的 URL 字符串
     */
    String encode(String source, String enc);

    /**
     * #brief: 使用默认字符集对字符串进行 URL 解码
     *
     * @param source 要进行 URL 解码的字符串
     * @return 解码后的字符串
     */
    String decode(String source);

    /**
     * #brief: 使用指定字符集对字符串进行 URL 解码
     *
     * @param source 要进行 URL 解码的字符串
     * @param enc 解码时使用的字符集
     * @return 解码后的字符串
     */
    String decode(String source, String enc);

}

