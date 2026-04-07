package com.changhong.tuple;

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


import static com.changhong.utils.TypeConverter.anyeq;

/**
 * 表示包含三个元素的元组。
 *
 * <p>该类用于表示一个由三个不同类型的元素组成的元组。提供访问这三个元素的方法，并重写了 `equals` 方法，
 * 以支持元组之间的比较。该类可用于需要返回多个相关值的场景，如方法返回多个值时。
 *
 * <h2>功能特点</h2>
 * <ul>
 *     <li>存储三个元素，支持不同类型的组合。</li>
 *     <li>提供访问元素的方法：`first()`、`second()`、`third()`。</li>
 *     <li>重写了 `equals` 方法，支持元组之间的值比较。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     Tuple<String, Integer, Boolean> tuple = new Tuple<>("example", 42, true);
 *     String firstValue = tuple.first(); // 获取第一个值
 *     Integer secondValue = tuple.second(); // 获取第二个值
 *     Boolean thirdValue = tuple.third(); // 获取第三个值
 * </pre>
 *
 * @param <A> 元组的第一个元素类型
 * @param <B> 元组的第二个元素类型
 * @param <C> 元组的第三个元素类型
 * @author Luo Tiansheng
 */
public class Tuple<A, B, C> {

    private final A first;
    private final B second;
    private final C third;

    /**
     * 构造一个包含三个元素的元组。
     *
     * @param first 第一个元素
     * @param second 第二个元素
     * @param third 第三个元素
     */
    public Tuple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * 获取元组的第一个元素。
     *
     * @return 第一个元素
     */
    public A first() {
        return first;
    }

    /**
     * 获取元组的第二个元素。
     *
     * @return 第二个元素
     */
    public B second() {
        return second;
    }

    /**
     * 获取元组的第三个元素。
     *
     * @return 第三个元素
     */
    public C third() {
        return third;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * 比较当前元组与另一个元组是否相等。
     *
     * @param tuple 要比较的元组
     * @return 如果元组的三个元素都相等，则返回 `true`，否则返回 `false`
     */
    public boolean equals(Tuple<A, B, C> tuple) {
        return anyeq(first, tuple.first) && anyeq(second, tuple.second) && anyeq(third, tuple.third);
    }

}
