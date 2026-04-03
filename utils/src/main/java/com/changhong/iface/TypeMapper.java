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

/* Creates on 2023/4/29. */

/**
 * `TypeMapper` 接口定义了一个通用的转换映射功能，
 * 用于将类型为 {@code T} 的值转换为类型为 {@code R} 的值。
 * 该接口的主要目的是提供一个抽象的方法，以便实现不同类型的转换逻辑。
 *
 * <p>实现此接口的类需要定义 {@code call} 方法，
 * 该方法接受一个 {@code T} 类型的参数并返回一个 {@code R} 类型的结果。
 *
 * <p>此接口适用于需要类型转换的场景，例如在数据处理、
 * 数据传输等过程中进行类型的转换与映射。
 *
 * @param <T> 输入值的类型
 * @param <R> 输出值的类型
 *
 * @author Luo Tiansheng
 */
public interface TypeMapper<T, R> {

    /**
     * #brief: 执行类型映射，将输入值转换为另一种类型
     *
     * <p>该方法接收一个输入值，并将其转换为指定的输出类型。实现该方法时需定义具体的转换逻辑。
     *
     * @param value 要转换的输入值
     * @return 转换后的输出值
     */
    R call(T value);

}
