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
 * `UnauthorizedException` 表示未经授权的异常。
 *
 * <p>该异常用于在用户未通过身份验证或没有足够权限访问某些资源时抛出。
 * 通常用于 API 访问控制或安全性检查的场景。
 *
 * <p>支持多种构造方式，可直接创建异常、携带异常原因或格式化错误消息。
 */
public class UnauthorizedException extends SystemRuntimeException {

    /**
     * 无参构造，创建默认的未经授权异常。
     */
    public UnauthorizedException() {
    }

    /**
     * 使用指定的异常作为原因创建未经授权异常。
     *
     * @param e 异常原因
     */
    public UnauthorizedException(Throwable e) {
        super(e);
    }

    /**
     * 使用格式化字符串创建未经授权异常。
     *
     * @param fmt  错误消息格式
     * @param args 格式化参数
     */
    public UnauthorizedException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 使用格式化字符串和异常原因创建未经授权异常。
     *
     * @param fmt  错误消息格式
     * @param e    异常原因
     * @param args 格式化参数
     */
    public UnauthorizedException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }
}

