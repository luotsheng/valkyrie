package com.changhong.test;

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

import com.changhong.security.cipher.RSACipher;
import com.changhong.security.key.RSAPrivateKey;
import com.changhong.security.key.RSAPublicKey;
import com.changhong.tuple.Pair;
import org.junit.Test;

@SuppressWarnings("ALL")
public class RSACipherTest {

    @Test
    public void rsaCipherTest() {
        RSACipher rsaCipher = new RSACipher();
        Pair<RSAPublicKey, RSAPrivateKey> pair = rsaCipher.generateKeyPair();

        String encryptMessage = rsaCipher.encrypt("你好，这是一条秘密消息！！！", pair.first());
        System.out.println("密文：" + encryptMessage);
        String decryptMessage = rsaCipher.decrypt(encryptMessage, pair.second());
        System.out.println("明文：" + decryptMessage);

        ////////////////////////////////////////////////////////////
        /// PEM
        ////////////////////////////////////////////////////////////
        System.out.println("———————————————————————— PEM ————————————————————————");
        String pemPublicKey = pair.first().toPEMFormat();
        String pemPrivateKey = pair.second().toPEMFormat();

        encryptMessage = rsaCipher.encrypt("这是一条 PEM 格式密钥加密解密测试消息。", RSAPublicKey.fromPEMFormat(pemPublicKey));
        System.out.println("密文：" + encryptMessage);
        decryptMessage = rsaCipher.decrypt(encryptMessage, RSAPrivateKey.fromPEMFormat(pemPrivateKey));
        System.out.println("明文：" + decryptMessage);
    }

}
