package com.changhong.iface;

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
 * `ActionFunction` 接口定义了一个可执行的操作，该操作不返回结果但可能抛出异常。
 *
 * <p>实现该接口的类需要提供具体的 `call` 方法，以执行特定的逻辑。
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public interface ActionFunction {

    /**
     * #brief: 执行可执行的逻辑
     *
     * <p>该方法定义了具体的执行逻辑，可能会抛出异常。
     * 实现该方法时需要处理可能的异常情况。
     *
     * @throws Throwable 可能抛出的异常，需在调用时处理
     */
    void call() throws Throwable;

}