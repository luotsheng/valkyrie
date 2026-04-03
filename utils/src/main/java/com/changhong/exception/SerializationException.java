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
 * `SerializationException` 表示序列化或反序列化过程中发生的异常。
 *
 * <p>该异常通常用于对象转换为字节流、JSON、XML 或其他格式失败的情况，
 * 以及从这些格式恢复对象时遇到的问题。
 *
 * <p>支持多种构造方式，可直接创建异常、携带异常原因或格式化错误消息。
 */
public class SerializationException extends SystemRuntimeException {

    /**
     * 无参构造，创建默认的序列化异常。
     */
    public SerializationException() {
    }

    /**
     * 使用指定的异常作为原因创建序列化异常。
     *
     * @param e 异常原因
     */
    public SerializationException(Throwable e) {
        super(e);
    }

    /**
     * 使用格式化字符串创建序列化异常。
     *
     * @param fmt  错误消息格式
     * @param args 格式化参数
     */
    public SerializationException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 使用格式化字符串和异常原因创建序列化异常。
     *
     * @param fmt  错误消息格式
     * @param e    异常原因
     * @param args 格式化参数
     */
    public SerializationException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }

}

