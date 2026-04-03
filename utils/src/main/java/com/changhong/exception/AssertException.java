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
 * `AssertException` 是一个自定义的断言异常类，继承自 `SystemRuntimeException`。
 * 用于在程序中断言条件失败时抛出异常，以便快速定位和处理程序逻辑错误。<p>
 *
 * <h2>使用场景</h2>
 * <p>通常用于在代码中进行断言检查时，当断言条件不满足时，抛出此异常。
 * 例如，可以在输入参数验证、逻辑验证等场景中使用。</p>
 *
 * <h2>构造函数</h2>
 * <ul>
 *     <li>{@link #AssertException()} - 默认构造函数，不带任何参数。</li>
 *     <li>{@link #AssertException(Throwable e)} - 通过已有的异常创建一个新的断言异常。</li>
 *     <li>{@link #AssertException(String fmt, Object... args)} - 根据格式化字符串和参数创建异常信息。</li>
 *     <li>{@link #AssertException(String fmt, Throwable e, Object... args)} - 根据格式化字符串、已有异常和参数创建异常信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 抛出一个简单的断言异常
 *     throw new AssertException("断言失败：条件未满足");
 *
 *     // 抛出一个包含其他异常的断言异常
 *     try {
 *         // 代码逻辑
 *     } catch (Exception e) {
 *         throw new AssertException(e);
 *     }
 *
 *     // 抛出一个格式化消息的断言异常
 *     throw new AssertException("断言失败：%s 应为 true", "条件");
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class AssertException extends SystemRuntimeException {

    /**
     * 默认构造函数。
     */
    public AssertException() {
    }

    /**
     * 通过已有的异常创建一个新的断言异常。
     *
     * @param e 原始异常，不能为 null。
     */
    public AssertException(Throwable e) {
        super(e);
    }

    /**
     * 根据格式化字符串和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param args 格式化参数。
     */
    public AssertException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 根据格式化字符串、已有异常和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param e 原始异常，不能为 null。
     * @param args 格式化参数。
     */
    public AssertException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }
}
