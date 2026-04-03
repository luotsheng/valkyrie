package com.changhong.security.key;

/* -------------------------------------------------------------------------------- *\
|*                                                                                  *|
|*    Copyright (C) 2019-2024 Luo Tiansheng All rights reserved.                    *|
|*                                                                                  *|
|*    Licensed under the Apache License, Version 2.0 (the "License");               *|
|*    you may not use this file except in compliance with the License.              *|
|*    You may obtain a copy of the License at                                       *|
|*                                                                                  *|
|*        http://www.apache.org/licenses/LICENSE-2.0                                *|
|*                                                                                  *|
|*    Unless required by applicable law or agreed to in writing, software           *|
|*    distributed under the License is distributed on an "AS IS" BASIS,             *|
|*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.      *|
|*    See the License for the specific language governing permissions and           *|
|*    limitations under the License.                                                *|
|*                                                                                  *|
\* -------------------------------------------------------------------------------- */

/* Creates on 2025/2/20. */

import com.changhong.io.MutableFile;
import com.changhong.utils.Captor;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Luo Tiansheng
 */
public class RSAPublicKey extends AbstractKey {

    public RSAPublicKey(Key key) {
        super(key);
    }

    public RSAPublicKey(byte[] encoded) {
        super(encoded);
    }

    public PublicKey toPublicKey() {
        return Captor.call(() -> {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        });
    }

    public static RSAPublicKey fromKeyFile(String filepath) {
        MutableFile keyfile = new MutableFile(filepath);
        return fromPEMFormat(keyfile.strread());
    }

    public static RSAPublicKey fromPEMFormat(String pem) {
        return new RSAPublicKey(decodePEMFormat(pem));
    }

    @Override
    public String toPEMFormat() {
        return toPEMFormat0("PUBLIC KEY", getEncoded());
    }

}
