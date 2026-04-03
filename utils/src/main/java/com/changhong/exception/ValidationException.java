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
 * `ValidationException` 表示数据验证失败时抛出的异常。
 *
 * <p>该异常用于处理参数校验、业务规则检查或数据一致性验证失败的情况。
 *
 * <p>支持无参构造、异常包装及格式化错误消息，以便提供详细的错误信息。
 */
public class ValidationException extends SystemRuntimeException {

    /**
     * 无参构造，创建默认的验证异常。
     */
    public ValidationException() {
    }

    /**
     * 使用指定的异常作为原因创建验证异常。
     *
     * @param e 异常原因
     */
    public ValidationException(Throwable e) {
        super(e);
    }

    /**
     * 使用格式化字符串创建验证异常。
     *
     * @param fmt  错误消息格式
     * @param args 格式化参数
     */
    public ValidationException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 使用格式化字符串和异常原因创建验证异常。
     *
     * @param fmt  错误消息格式
     * @param e    异常原因
     * @param args 格式化参数
     */
    public ValidationException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }

}

