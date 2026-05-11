package valkyrie.utils.collection;

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

/* Creates on 2023/5/6. */

import java.util.*;

/**
 * `Sets` 是一个实用工具类，用于操作和处理各种 {@link Set} 接口的实现类，如 {@link HashSet}、{@link LinkedHashSet} 等。
 * 该类提供了一组静态方法，用于简化和优化对 Set 的常见操作，包括复制、创建、合并，以及通过指定元素快速生成 Set 实例。
 *
 * <p>主要特点和功能包括：
 * <ul>
 *     <li>支持对 {@link Set} 集合的深拷贝，通过 {@link #copy(Set)} 方法创建一个包含相同数据的新 {@link HashSet} 实例。</li>
 *     <li>提供多种 {@code of()} 方法，支持快速创建空或包含指定元素的 Set 实例，简化了 Set 对象的初始化过程。</li>
 *     <li>支持通过集合、数组或可变参数形式传入数据，灵活性高，便于开发时使用。</li>
 *     <li>提供简单的 Set 合并功能 {@link #newHashSet(Collection, Collection)}，允许将两个集合合并为一个新的 {@link HashSet}。</li>
 * </ul>
 *
 * <h2>使用注意事项</h2>
 * <ul>
 *     <li>方法参数要求非空，传入 {@code null} 可能会导致 {@link NullPointerException}。</li>
 *     <li>合并操作会忽略重复的元素，因为 {@link Set} 本身是一个不允许重复元素的集合类型。</li>
 *     <li>在对数组进行截取操作时，请确保索引和长度参数合法，否则会抛出 {@link ArrayIndexOutOfBoundsException}。</li>
 * </ul>
 *
 * <h2>示例用法</h2>
 * <pre>
 *     // 复制一个 Set 对象
 *     Set<String> copiedSet = Sets.copy(originalSet);
 *
 *     // 创建一个包含多个元素的 Set
 *     Set<String> setWithElements = Sets.of("element1", "element2", "element3");
 *
 *     // 合并两个 Set
 *     Set<String> mergedSet = Sets.of(set1, set2);
 * </pre>
 *
 * <p>该类旨在为开发者提供方便且高效的 Set 操作工具，是日常开发中处理 Set 对象的理想选择。同时因 Set 对象也是 Collection 接口
 * 的实现类之一，所以它也可以使用 Lists 中的许多操作函数。
 *
 * @author Luo Tiansheng
 *
 * @see Set
 * @see HashSet
 * @see LinkedHashSet
 * @since 1.0
 */
public class Sets {

    /**
     * 传入一个 {@link Set} 对象，拷贝参数 {@code a} 中的数据到新的 {@link HashSet} 对象中，数据拷贝
     * 过去以后，修改原来的 {@link Set} 对象或修改新分配到对象，并不会影响两个对象之间的数据。
     *
     * @param a 需要拷贝的数据
     * @return 拷贝后的新 {@link HashSet} 对象实例
     */
    public static <E> Set<E> copy(Set<E> a) {
        return new HashSet<>(a);
    }

    /**
     * @return 分配一个空的 {@link HashSet} 集合对象实例。
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<>();
    }

    /**
     * 截取数组 {@code a} 通过 {@code off} 和 {@code len} 两个参数去截取，截取出来的新数组会
     * 分配成一个可变的 {@link HashSet} 对象实例。
     *
     * @param a
     *        泛型可变参数数组
     *
     * @param off
     *        泛型数组开始的索引位置，偏移量。
     *
     * @param len
     *        要截取的长度，这个长度范围应该在 <= {@code a}.length()。如果长度超出 {@code a} 数组
     *        的大小，会抛出 {@link ArrayIndexOutOfBoundsException} 异常。
     *
     * @return 一个新的 {@link HashSet} 对象实例
     *
     * @throws NullPointerException
     *         如果泛型参数为空值。
     *
     * @throws ArrayIndexOutOfBoundsException
     *         如果 {@code len} 超出整个 {@code a} 数组大小的长度。
     *
     * @see #newHashSet(Collection)
     */
    @SuppressWarnings("unchecked")
    public static <E> HashSet<E> newHashSet(E[] a, int off, int len) {
        return (HashSet<E>) newHashSet(Arrays.asList(a, off, len));
    }

    /**
     * 通过传入的 {@link Collection} 实例去分配一个 {@link HashSet} 集合对象实例。{@code collection} 参数不能为空
     * 否则会抛出 {@link NullPointerException} 异常。
     *
     * @param collection
     *        实现了 {@link Collection} 接口的对象实例，如 {@link HashSet}、{@link LinkedList} 等
     *        对象实例皆实现了 {@link Collection} 接口。
     *
     * @return 一个新的 {@link HashSet} 对象实例
     *
     * @throws NullPointerException
     *         如果 {@code collection} 参数为空值
     *
     * @see HashSet#HashSet(Collection)
     */
    public static <E> HashSet<E> newHashSet(Collection<? extends E> collection) {
        return new HashSet<>(collection);
    }

    /**
     * 合并两个实现了 {@link Collection} 接口的对象实例，通过传入顺序合并两个对象成一个新的 {@link HashSet} 对象
     * 实例。两个 {@link Collection} 不能为空，否则会抛出 {@link NullPointerException} 异常。
     *
     * @param a
     *        实现了 {@link Collection} 接口的对象实例，如 {@link HashSet}、{@link LinkedList} 等
     *        对象实例皆实现了 {@link Collection} 接口。

     * @param b
     *        另一个实现了 {@link Collection} 接口的对象的实例
     *
     * @return 一个新的 {@link HashSet} 对象实例
     *
     * @throws NullPointerException
     *         如果 {@code a} 或者 {@code b} 参数为空值
     *
     * @see HashSet#HashSet(Collection)
     */
    public static <E> HashSet<E> newHashSet(Collection<? extends E> a, Collection<? extends E> b) {
        HashSet<E> ret = newHashSet();
        ret.addAll(a);
        ret.addAll(b);
        return (HashSet<E>) ret;
    }

    /**
     * 通过传入的泛型可变参数去分配一个 {@link HashSet} 集合对象实例。泛型可变参数不能为空
     * 否则会抛出 {@link NullPointerException} 异常。
     *
     * @param a
     *        泛型可变参数数组
     *
     * @return 一个新的 {@link HashSet} 对象实例
     *
     * @throws NullPointerException
     *         如果泛型参数为空值
     *
     * @see #newHashSet(Collection)
     */
    @SafeVarargs
    public static <E> HashSet<E> of(E... a) {
        return newHashSet(Arrays.asList(a));
    }

    /**
     * 通过传入的泛型可变参数去分配一个 {@link LinkedHashSet} 集合对象实例。泛型可变参数不能为空
     * 否则会抛出 {@link NullPointerException} 异常。
     *
     * @param a
     *        泛型可变参数数组
     *
     * @return 一个新的 {@link LinkedHashSet} 对象实例
     *
     * @throws NullPointerException
     *         如果泛型参数为空值
     *
     * @see #newHashSet(Collection)
     */
    @SuppressWarnings("unchecked")
    public static <E> Set<E> newLinkedHashSet(E... a)
    {
        return new LinkedHashSet<>(Arrays.asList(a));
    }
}
