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
 * `IOWriteException` 是一个自定义的 I/O 写入异常类，继承自 `SystemRuntimeException`。
 * 当在进行 I/O 写入操作时发生错误或异常情况时，可以抛出此异常以便更好地处理和定位问题。<p>
 *
 * <h2>使用场景</h2>
 * <p>适用于文件写入、流写入、网络数据发送等 I/O 操作的场景。当写入过程中出现错误或异常时，
 * 可以使用此异常进行捕获和处理，提供更加明确的异常信息。</p>
 *
 * <h2>构造函数</h2>
 * <ul>
 *     <li>{@link #IOWriteException()} - 默认构造函数，不带任何参数。</li>
 *     <li>{@link #IOWriteException(Throwable e)} - 通过已有的异常创建一个新的 I/O 写入异常。</li>
 *     <li>{@link #IOWriteException(String fmt, Object... args)} - 根据格式化字符串和参数创建异常信息。</li>
 *     <li>{@link #IOWriteException(String fmt, Throwable e, Object... args)} - 根据格式化字符串、已有异常和参数创建异常信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 抛出一个简单的 I/O 写入异常
 *     throw new IOWriteException("文件写入失败：磁盘空间不足");
 *
 *     // 抛出一个包含其他异常的 I/O 写入异常
 *     try {
 *         // 写入文件操作
 *     } catch (IOException e) {
 *         throw new IOWriteException(e);
 *     }
 *
 *     // 抛出一个格式化消息的 I/O 写入异常
 *     throw new IOWriteException("写入文件 %s 时发生错误：%s", filePath, errorMessage);
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class IOWriteException extends SystemRuntimeException {

    /**
     * 默认构造函数。
     */
    public IOWriteException() {
    }

    /**
     * 通过已有的异常创建一个新的 I/O 写入异常。
     *
     * @param e 原始异常，不能为 null。
     */
    public IOWriteException(Throwable e) {
        super(e);
    }

    /**
     * 根据格式化字符串和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param args 格式化参数。
     */
    public IOWriteException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 根据格式化字符串、已有异常和参数创建异常信息。
     *
     * @param fmt 格式化字符串，不能为 null。
     * @param e 原始异常，不能为 null。
     * @param args 格式化参数。
     */
    public IOWriteException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }

}

