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
 * `InvalidArgumentException` 是一个自定义的非法参数异常类，继承自 `SystemRuntimeException`。
 * 用于在方法或函数调用中，当传入的参数不合法或不符合预期时抛出。<p>
 *
 * <h2>使用场景</h2>
 * <p>常用于参数验证、数据校验等场景。当参数值不符合预期或条件时，可以抛出此异常，
 * 从而在程序的早期阶段捕获错误，防止错误传播。</p>
 *
 * <h2>构造函数</h2>
 * <ul>
 *     <li>{@link #InvalidArgumentException()} - 默认构造函数，不带任何参数。</li>
 *     <li>{@link #InvalidArgumentException(Throwable e)} - 通过已有的异常创建一个新的非法参数异常。</li>
 *     <li>{@link #InvalidArgumentException(String fmt, Object... args)} - 根据格式化字符串和参数创建异常信息。</li>
 *     <li>{@link #InvalidArgumentException(String fmt, Throwable e, Object... args)} - 根据格式化字符串、已有异常和参数创建异常信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 抛出一个简单的非法参数异常
 *     throw new InvalidArgumentException("非法参数：参数值不能为 null");
 *
 *     // 抛出一个包含其他异常的非法参数异常
 *     try {
 *         // 代码逻辑
 *     } catch (NullPointerException e) {
 *         throw new InvalidArgumentException(e);
 *     }
 *
 *     // 抛出一个格式化消息的非法参数异常
 *     throw new InvalidArgumentException("非法参数：期望值在范围 %d 到 %d 之间，但实际值为 %d", minValue, maxValue, actualValue);
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class InvalidArgumentException extends SystemRuntimeException {

    /**
     * 默认构造函数。
     */
    public InvalidArgumentException() {
    }

    /**
     * 通过已有的异常创建一个新的非法参数异常。
     *
     * @param e 原始异常，不能为 null。
     */
    public InvalidArgumentException(Throwable e) {
        super(e);
    }

    /**
     * 根据格式化字符串和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param args 格式化参数。
     */
    public InvalidArgumentException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 根据格式化字符串、已有异常和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param e 原始异常，不能为 null。
     * @param args 格式化参数。
     */
    public InvalidArgumentException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }
}
