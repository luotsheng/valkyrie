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
 * `Maps` 是一个实用工具类，专门用于操作和处理各种 {@link Map} 接口的实现类，如 {@link HashMap}、{@link LinkedHashMap} 等。
 * 该类提供了一组静态方法，用于简化和优化对 Map 的常见操作，包括检查是否为空、复制、创建、合并以及通过指定键值对快速生成 Map 实例。
 *
 * <p>主要特点和功能包括：
 * <ul>
 *     <li>提供对 Map 集合的空检查方法 {@link #isEmpty(Map)}，避免手动进行非空检查。</li>
 *     <li>支持 Map 对象的深拷贝，通过 {@link #copy(Map)} 方法创建一个包含相同数据的新 Map 实例。</li>
 *     <li>多种 {@code of()} 方法支持快速创建 Map 实例，允许传入多个键值对进行初始化，便于开发时使用。</li>
 *     <li>提供简单的 Map 合并功能 {@link #newHashMap(Map, Map)}，但需注意返回的结果是无序的 {@link HashMap}。</li>
 * </ul>
 *
 * <h2>使用注意事项</h2>
 * <ul>
 *     <li>当需要保留 Map 的插入顺序时，请避免使用该类中的合并方法，因其返回的始终是无序的 {@link HashMap}。</li>
 *     <li>方法参数要求非空，传入 {@code null} 可能会导致 {@link NullPointerException}。</li>
 *     <li>对于同一个键，合并操作中后一个 Map 中的值会覆盖前一个 Map 中的值。</li>
 * </ul>
 *
 * <h2>示例用法</h2>
 * <pre>
 *     // 检查 Map 是否为空
 *     boolean isMapEmpty = Maps.isEmpty(map);
 *
 *     // 复制一个 Map 对象
 *     Map<String, Integer> copiedMap = Maps.copy(originalMap);
 *
 *     // 创建一个包含两个键值对的 Map
 *     Map<String, String> mapWithPairs = Maps.of("key1", "value1", "key2", "value2");
 *
 *     // 合并两个 Map
 *     Map<String, Integer> mergedMap = Maps.of(map1, map2);
 * </pre>
 *
 * <p>该类旨在为开发者提供方便且高效的 Map 操作工具，是日常开发中处理 Map 对象的理想选择。
 *
 * @author Luo Tiansheng
 *
 * @see Map
 * @see HashMap
 * @see LinkedHashMap
 * @since 1.0
 * @noinspection DuplicatedCode
 */
public class Maps {

    /**
     * 判断参数 {@code map} 实现了 {@link Map} 接口的对象实例，是否为空。如果要满足函数
     * 返回 {@code false} 那么该对象不能为 {@code null} 并且该对象的 {@code size} 必须大
     * 于 0。反之返回 {@code true}。<p>
     *
     * <p>
     * 这个函数主要的作用在于判断各种 {@link LinkedHashMap}、{@link HashMap} 等继承了
     * {@link Map} 接口的子接口。
     *
     * @param map
     *        一个 {@link Map} 接口对象实例
     *
     * @return {@code false} 表示集合不为空，集合内有真实的数据。反之 {@code true} 表示集合
     *         内是空的，没有真实的任何数据。
     *
     * @see List
     * @see Set
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 传入一个 {@link Map} 对象，拷贝参数 {@code a} 中的数据到新的 {@link HashMap} 对象中，数据拷贝
     * 过去以后，修改原来的 {@link Map} 对象或修改新分配到对象，并不会影响两个对象之间的数据。
     *
     * @param a 需要拷贝的数据
     * @return 拷贝后的新 {@link HashMap} 对象实例
     */
    public static <K, V> Map<K, V> copy(Map<K, V> a) {
        return new HashMap<>(a);
    }

    /**
     * @return 分配一个新的容量默认为 16 个空间的 {@link Map} 实例。
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 合并两个实现了 {@link Map} 接口实例，合并的 {@link Map} 对象可以是任何实现类。但是有一个地方
     * 需要注意，也就是如果想要的合并结果是有序的。比如你想要合并两个 {@link LinkedHashMap} 成一个单独
     * 的 {@link Map} 对象并且要保证数据的有序。那么这个函数并不适合这样的操作。
     *
     * <p>因为这个函数的返回结果是 {@link HashMap}，而 {@link HashMap} 是一个无序的
     * {@link Map} 实现类。
     *
     * @param a
     *          一个要合并的 {@link Map} 实现
     *
     * @param b
     *          另一个要合并的 {@link Map} 实现
     *
     * @return 合并后的 {@link HashMap} 对象。
     */
    public static <K, V> Map<K, V> of(Map<K, V> a, Map<K, V> b) {
        Map<K, V> retmap = newHashMap();
        retmap.putAll(a);
        retmap.putAll(b);
        return retmap;
    }

    /**
     * 通过参数传入，key 和 value 创建一个 {@link Map} 对象实例。这个函数必须传入
     * {@code key} 和 {@code value} 并且返回的 Map 对象实例中包含传入的 {@code key}
     * 和 {@code value}
     *
     * 这个函数支持传入一个键值对。
     *
     * @param k1
     *          key
     *
     * @param v1
     *          value
     *
     * @return 返回一个包含传入的 key 和 value 的 {@link Map} 对象实例。
     */
    public static <K, V> Map<K, V> of(K k1, V v1) {
        Map<K, V> retmap = newHashMap();
        retmap.put(k1, v1);
        return retmap;
    }

    /**
     * 通过参数传入，key 和 value 创建一个 {@link Map} 对象实例。这个函数必须传入
     * {@code key} 和 {@code value} 并且返回的 Map 对象实例中包含传入的 {@code key}
     * 和 {@code value}
     *
     * 这个函数支持传入两个键值对。
     *
     * @return 返回一个包含传入的 key 和 value 的 {@link Map} 对象实例。
     */
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> retmap = newHashMap();
        retmap.put(k1, v1);
        retmap.put(k2, v2);
        return retmap;
    }

    /**
     * 通过参数传入，key 和 value 创建一个 {@link Map} 对象实例。这个函数必须传入
     * {@code key} 和 {@code value} 并且返回的 Map 对象实例中包含传入的 {@code key}
     * 和 {@code value}
     *
     * 这个函数支持传入三个键值对。
     *
     * @return 返回一个包含传入的 key 和 value 的 {@link Map} 对象实例。
     */
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> retmap = newHashMap();
        retmap.put(k1, v1);
        retmap.put(k2, v2);
        retmap.put(k3, v3);
        return retmap;
    }

    /**
     * 通过参数传入，key 和 value 创建一个 {@link Map} 对象实例。这个函数必须传入
     * {@code key} 和 {@code value} 并且返回的 Map 对象实例中包含传入的 {@code key}
     * 和 {@code value}
     *
     * 这个函数支持传入四个键值对。
     *
     * @return 返回一个包含传入的 key 和 value 的 {@link Map} 对象实例。
     */
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> retmap = newHashMap();
        retmap.put(k1, v1);
        retmap.put(k2, v2);
        retmap.put(k3, v3);
        retmap.put(k4, v4);
        return retmap;
    }

    /**
     * 通过参数传入，key 和 value 创建一个 {@link Map} 对象实例。这个函数必须传入
     * {@code key} 和 {@code value} 并且返回的 Map 对象实例中包含传入的 {@code key}
     * 和 {@code value}
     *
     * 这个函数支持传入五个键值对。
     *
     * @return 返回一个包含传入的 key 和 value 的 {@link Map} 对象实例。
     */
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> retmap = newHashMap();
        retmap.put(k1, v1);
        retmap.put(k2, v2);
        retmap.put(k3, v3);
        retmap.put(k4, v4);
        retmap.put(k5, v5);
        return retmap;
    }

    /**
     * 通过参数传入，key 和 value 创建一个 {@link Map} 对象实例。这个函数必须传入
     * {@code key} 和 {@code value} 并且返回的 Map 对象实例中包含传入的 {@code key}
     * 和 {@code value}
     *
     * 这个函数支持传入六个键值对。
     *
     * @return 返回一个包含传入的 key 和 value 的 {@link Map} 对象实例。
     */
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        Map<K, V> retmap = newHashMap();
        retmap.put(k1, v1);
        retmap.put(k2, v2);
        retmap.put(k3, v3);
        retmap.put(k4, v4);
        retmap.put(k5, v5);
        retmap.put(k6, v6);
        return retmap;
    }

}
