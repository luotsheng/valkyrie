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
 * `UnsupportedOperationException` 是一个自定义的异常类，继承自 `SystemRuntimeException`。
 * 当操作不被支持或无法实现时，可以抛出此异常以表明该操作无法完成。<p>
 *
 * <h2>使用场景</h2>
 * <p>适用于不支持特定操作的场景。例如，当调用不被实现或当前环境下不允许执行的操作时，
 * 可以使用此异常进行捕获和处理，明确地指示操作的不可支持性。</p>
 *
 * <h2>构造函数</h2>
 * <ul>
 *     <li>{@link #UnsupportedOperationException()} - 默认构造函数，不带任何参数。</li>
 *     <li>{@link #UnsupportedOperationException(Throwable e)} - 通过已有的异常创建一个新的不支持操作异常。</li>
 *     <li>{@link #UnsupportedOperationException(String fmt, Object... args)} - 根据格式化字符串和参数创建异常信息。</li>
 *     <li>{@link #UnsupportedOperationException(String fmt, Throwable e, Object... args)} - 根据格式化字符串、已有异常和参数创建异常信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 抛出一个简单的不支持操作异常
 *     throw new UnsupportedOperationException("此操作不被支持");
 *
 *     // 抛出一个包含其他异常的不支持操作异常
 *     try {
 *         // 进行不支持的操作
 *     } catch (SomeException e) {
 *         throw new UnsupportedOperationException(e);
 *     }
 *
 *     // 抛出一个格式化消息的不支持操作异常
 *     throw new UnsupportedOperationException("操作 %s 不被支持：%s", operationName, errorMessage);
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class UnsupportedOperationException extends SystemRuntimeException {

    /**
     * 默认构造函数。
     */
    public UnsupportedOperationException() {
    }

    /**
     * 通过已有的异常创建一个新的不支持操作异常。
     *
     * @param e 原始异常，不能为 null。
     */
    public UnsupportedOperationException(Throwable e) {
        super(e);
    }

    /**
     * 根据格式化字符串和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param args 格式化参数。
     */
    public UnsupportedOperationException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 根据格式化字符串、已有异常和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param e 原始异常，不能为 null。
     * @param args 格式化参数。
     */
    public UnsupportedOperationException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }

}

