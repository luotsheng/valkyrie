package com.changhong.poi;

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

import com.changhong.collection.Lists;
import com.changhong.iface.TypeMapper;

import java.util.ArrayList;
import java.util.Collection;

import static com.changhong.utils.Transformer.atos;

/**
 * 表示一行数据，继承自 {@link ArrayList}，用于存储字符串类型的单元格数据。
 *
 * <p>此类提供了多种构造函数，以便于创建行对象，并可通过索引获取单元格数据。
 *
 * <p>示例用法：
 * <pre>
 *      Row row = new Row("Value1", "Value2");
 *      String firstValue = row.get(0);
 * </pre>
 *
 * @author Luo Tiansheng
 */
public class Row extends ArrayList<String> {

    /**
     * 创建一个空的行对象。
     */
    public Row() {
    }

    /**
     * 创建一个具有指定初始容量的行对象。
     *
     * @param initialCapacity 初始容量。
     */
    public Row(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 创建一个行对象，使用指定的集合初始化。
     *
     * @param c 用于初始化行的字符串集合。
     */
    public Row(Collection<? extends String> c) {
        super(c);
    }

    /**
     * 创建一个行对象，使用可变参数初始化。
     *
     * @param values 初始化行的字符串值。
     */
    public Row(String... values) {
        super(Lists.fromVarargs(values));
    }

    /**
     * 获取指定索引的单元格数据，并通过提供的映射器进行转换。
     *
     * <p>此方法将行中指定索引的单元格数据映射到指定类型。
     *
     * <p>示例用法：
     * <pre>
     *      String value = row.get(0, String::toUpperCase);
     * </pre>
     *
     * @param i      单元格的索引。
     * @param mapper 用于转换的映射器。
     * @param <R>    转换后的返回类型。
     * @return 转换后的单元格数据。
     */
    public <R> R get(int i, TypeMapper<Object, R> mapper) {
        return mapper.call(get(i));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String text : this)
            builder.append(text).append(" ");
        int len = builder.length();
        builder.delete(len - 1, len);
        return atos(builder);
    }

}
