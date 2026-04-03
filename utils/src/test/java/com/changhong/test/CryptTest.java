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

import com.changhong.security.Codec;
import com.changhong.security.Crypt;
import org.junit.Test;

@SuppressWarnings("ALL")
public class CryptTest {

    /**
     * AES 编码、解码测试
     */
    @Test
    public void aesTest() {
        String text = "牛子一掰，全场笑歪！";
        String secret = Codec.randomNextSecret();
        System.out.println("AES secret key: " + secret);

        // Encoder
        String key = Crypt.AES.encrypt(text, secret);;
        System.out.println("AES text encrypt: " + key);

        // Decoder
        System.out.println("AES text decrypt: " + Crypt.AES.decrypt(key, secret));

    }

}
