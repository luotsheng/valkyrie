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

/* Creates on 2025/2/20. */

import com.changhong.security.key.RSAPrivateKey;
import com.changhong.security.key.RSAPublicKey;
import com.changhong.tuple.Pair;

/**
 * `RSA` 接口定义了用于 RSA 加密和解密操作的方法，包含密钥生成、加密和解密功能。
 * 该接口提供了生成公钥和私钥对、加密消息以及解密消息的方法。
 *
 * <h2>方法概述</h2>
 * <ul>
 *     <li>生成 RSA 密钥对。</li>
 *     <li>使用公钥加密消息。</li>
 *     <li>使用私钥解密消息。</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>密钥大小（如 2048 位或 4096 位）直接影响加密和解密的安全性。</li>
 *     <li>RSA 加密只能加密数据的哈希值，通常需要与其他加密算法联合使用。</li>
 * </ul>
 *
 * @see Pair
 * @see RSAPublicKey
 * @see RSAPrivateKey
 * @author Luo Tiansheng
 */
public interface RSA {

    /**
     * 生成一对 RSA 公钥和私钥，使用默认的密钥大小。
     *
     * @return 包含公钥和私钥的 `Pair` 对象
     */
    Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair();

    /**
     * 生成一对 RSA 公钥和私钥，使用指定的密钥大小。
     *
     * @param size 指定的密钥大小（通常为 2048 或 4096）
     * @return 包含公钥和私钥的 `Pair` 对象
     */
    Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair(int size);

    /**
     * 使用 RSA 公钥加密给定的消息。
     *
     * @param message 要加密的消息
     * @param publicKey 用于加密的公钥
     * @return 加密后的消息
     */
    String encrypt(String message, RSAPublicKey publicKey);

    /**
     * 使用 RSA 私钥解密给定的加密消息。
     *
     * @param encryptedMessage 被加密的消息
     * @param privateKey 用于解密的私钥
     * @return 解密后的原始消息
     */
    String decrypt(String encryptedMessage, RSAPrivateKey privateKey);
}

