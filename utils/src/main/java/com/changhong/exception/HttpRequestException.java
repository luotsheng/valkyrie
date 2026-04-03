package com.changhong.exception;

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


/**
 * `HttpRequestException` 是一个自定义的 HTTP 请求异常类，继承自 `SystemRuntimeException`。
 * 用于在处理 HTTP 请求时出现错误或异常情况时抛出。<p>
 *
 * <h2>使用场景</h2>
 * <p>通常用于网络请求、API 调用等操作中，当请求失败、超时或响应异常时，可以抛出此异常。
 * 方便上层逻辑对异常进行捕获和处理。</p>
 *
 * <h2>构造函数</h2>
 * <ul>
 *     <li>{@link #HttpRequestException()} - 默认构造函数，不带任何参数。</li>
 *     <li>{@link #HttpRequestException(Throwable e)} - 通过已有的异常创建一个新的 HTTP 请求异常。</li>
 *     <li>{@link #HttpRequestException(String fmt, Object... args)} - 根据格式化字符串和参数创建异常信息。</li>
 *     <li>{@link #HttpRequestException(String fmt, Throwable e, Object... args)} - 根据格式化字符串、已有异常和参数创建异常信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 抛出一个简单的 HTTP 请求异常
 *     throw new HttpRequestException("HTTP 请求失败：服务器未响应");
 *
 *     // 抛出一个包含其他异常的 HTTP 请求异常
 *     try {
 *         // 代码逻辑，执行 HTTP 请求
 *     } catch (IOException e) {
 *         throw new HttpRequestException(e);
 *     }
 *
 *     // 抛出一个格式化消息的 HTTP 请求异常
 *     throw new HttpRequestException("HTTP 请求失败：状态码 %d", responseCode);
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class HttpRequestException extends SystemRuntimeException {

    /**
     * 默认构造函数。
     */
    public HttpRequestException() {
    }

    /**
     * 通过已有的异常创建一个新的 HTTP 请求异常。
     *
     * @param e 原始异常，不能为 null。
     */
    public HttpRequestException(Throwable e) {
        super(e);
    }

    /**
     * 根据格式化字符串和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param args 格式化参数。
     */
    public HttpRequestException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 根据格式化字符串、已有异常和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param e 原始异常，不能为 null。
     * @param args 格式化参数。
     */
    public HttpRequestException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }
}

