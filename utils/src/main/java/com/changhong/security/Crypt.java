package com.changhong.security;

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

/* Creates on 2023/5/16. */

import com.changhong.security.cipher.AESCipher;
import com.changhong.security.cipher.RSACipher;

/**
 * `Crypt` 是一个工具类，提供了多种加密和解密算法的实现，支持常见的加密需求。
 *
 * <p>该类包含静态方法，可以直接调用，无需实例化对象。支持的算法包括 AES、RSA 等，能够
 * 满足用户对数据加密和解密的需求，确保数据的安全性。
 *
 * <p>本类的主要特点包括：
 * <ul>
 *     <li>提供多种加密算法，支持不同类型的数据处理。</li>
 *     <li>所有方法均为静态，方便直接调用，简化使用流程。</li>
 *     <li>包含基本的异常处理，确保在加密解密过程中提供适当的错误信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 使用 AES 加密
 *     String encrypted = Crypt.AES.encrypt("Hello World", "mysecretkey");
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public final class Crypt {

    public static final AES AES = new AESCipher();    // AES
    public static final RSA RSA = new RSACipher();    // RSA

}
