package com.changhong.security.codec;

import com.changhong.utils.Transformer;
import com.changhong.security.Base64;

import static com.changhong.utils.Transformer.atos;

/**
 * @author Luo Tiansheng
 */
public class Base64Codec implements Base64 {
    @Override
    public String encode(String source) {
        return encode(source.getBytes());
    }

    @Override
    public String encode(byte[] b) {
        return java.util.Base64.getUrlEncoder().encodeToString(b);
    }

    @Override
    public String decode(String src) {
        return Transformer.atos(decodeBytes(src));
    }

    @Override
    public byte[] decodeBytes(String src) {
        return java.util.Base64.getUrlDecoder().decode(src);
    }

}
