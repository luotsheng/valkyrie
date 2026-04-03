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
 * `IOReadException` 是一个自定义的 I/O 读取异常类，继承自 `SystemRuntimeException`。
 * 当在进行 I/O 读取操作时发生错误或异常情况时，可以抛出此异常以便更好地处理和定位问题。<p>
 *
 * <h2>使用场景</h2>
 * <p>适用于文件读取、流读取、网络数据读取等 I/O 操作的场景。当读取过程中出现错误或异常时，
 * 可以使用此异常进行捕获和处理，提供更加明确的异常信息。</p>
 *
 * <h2>构造函数</h2>
 * <ul>
 *     <li>{@link #IOReadException()} - 默认构造函数，不带任何参数。</li>
 *     <li>{@link #IOReadException(Throwable e)} - 通过已有的异常创建一个新的 I/O 读取异常。</li>
 *     <li>{@link #IOReadException(String fmt, Object... args)} - 根据格式化字符串和参数创建异常信息。</li>
 *     <li>{@link #IOReadException(String fmt, Throwable e, Object... args)} - 根据格式化字符串、已有异常和参数创建异常信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 抛出一个简单的 I/O 读取异常
 *     throw new IOReadException("文件读取失败：文件路径不可访问");
 *
 *     // 抛出一个包含其他异常的 I/O 读取异常
 *     try {
 *         // 读取文件操作
 *     } catch (IOException e) {
 *         throw new IOReadException(e);
 *     }
 *
 *     // 抛出一个格式化消息的 I/O 读取异常
 *     throw new IOReadException("读取文件 %s 时发生错误：%s", filePath, errorMessage);
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class IOReadException extends SystemRuntimeException {

    /**
     * 默认构造函数。
     */
    public IOReadException() {
    }

    /**
     * 通过已有的异常创建一个新的 I/O 读取异常。
     *
     * @param e 原始异常，不能为 null。
     */
    public IOReadException(Throwable e) {
        super(e);
    }

    /**
     * 根据格式化字符串和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param args 格式化参数。
     */
    public IOReadException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 根据格式化字符串、已有异常和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param e 原始异常，不能为 null。
     * @param args 格式化参数。
     */
    public IOReadException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }

}

