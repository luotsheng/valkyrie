package com.changhong.utils.security.cipher;

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

import com.changhong.utils.Captor;
import com.changhong.utils.TypeConverter;
import com.changhong.utils.security.Codec;
import com.changhong.utils.security.RSA;
import com.changhong.utils.security.key.RSAPrivateKey;
import com.changhong.utils.security.key.RSAPublicKey;
import com.changhong.utils.tuple.Pair;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * @author Luo Tiansheng
 */
@SuppressWarnings("DataFlowIssue")
public class RSACipher implements RSA {

    @Override
    public Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair() {
        return generateKeyPair(2048);
    }

    @Override
    public Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair(int size) {
        KeyPairGenerator keyPairGenerator =
                Captor.icall(() -> KeyPairGenerator.getInstance("RSA"));
        keyPairGenerator.initialize(size);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return Pair.of(new RSAPublicKey(keyPair.getPublic()), new RSAPrivateKey(keyPair.getPrivate()));
    }

    @Override
    public String encrypt(String message, RSAPublicKey publicKey) {
        return Captor.call(() -> {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey.toPublicKey());
            byte[] b = cipher.doFinal(TypeConverter.atob(message));
            return Codec.BASE64.encode(b);
        });
    }

    @Override
    public String decrypt(String encryptedMessage, RSAPrivateKey privateKey) {
        return Captor.call(() -> {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey.toPrivateKey());
            byte[] b = cipher.doFinal(Codec.BASE64.decodeBytes(encryptedMessage));
            return TypeConverter.atos(b);
        });
    }

}
