package com.changhong.stream;

import com.changhong.collection.Lists;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * `Stream` 是一个工具类，提供了对集合、数组及其他数据源进行流式处理的相关方法。
 *
 * <p>该类的设计目标是简化数据处理操作，支持基于流式操作的链式调用方式，例如过滤、
 * 映射、排序、聚合等。这些方法以简洁、可读性强的方式实现了常见的数据处理需求。
 *
 * <p>主要功能包括：
 * <ul>
 *     <li>提供对集合元素的流式操作，如 {@link #filter}、{@link #map}。</li>
 *     <li>支持通过自定义函数实现复杂的数据变换和处理逻辑。</li>
 *     <li>基于 Java Stream API 提供易用的工具方法，避免冗长代码。</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>方法中的流式操作可能会消耗大量内存，特别是对大数据集操作时需谨慎。</li>
 *     <li>部分方法要求输入参数不能为空，否则会抛出 {@link NullPointerException} 异常。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 过滤集合中的偶数
 *     List<Integer> evenNumbers = Stream.filter(numbers, n -> n % 2 == 0);
 *
 *     // 将字符串集合转换为长度列表
 *     List<Integer> lengths = Stream.map(strings, String::length);
 * </pre>
 *
 * <p>本类旨在通过工具方法的封装，提升代码可读性和维护性，特别适用于需要流式操作的场景。
 *
 * @author Luo Tiansheng
 * @see Stream
 * @since 1.0
 */
public class Streams {

    /**
     * #brief: 根据集合大小配置并返回合适的流（串行或并行）
     *
     * <p>该方法根据集合的大小决定是否使用并行流。如果集合的元素数量超过 15000，则使用并行流，
     * 否则使用串行流。适用于需要根据数据量选择不同性能策略的场景。
     *
     * @param collection 待处理的集合
     * @param <T> 集合中元素的类型
     * @return 配置后的流，可能是串行流或并行流
     * @throws NullPointerException 如果集合为空
     * @see Collection#size()
     * @see Stream#parallel()
     */
    private static <T> Stream<T> configure(Collection<T> collection) {
        return collection.size() > 15000 ? collection.stream().parallel() : collection.stream();
    }

    /**
     * #brief: 根据给定条件过滤集合中的元素并返回结果列表
     *
     * <p>该方法接收一个集合和一个过滤条件，返回满足条件的元素列表。适用于需要根据特定规则
     * 过滤集合元素的场景。
     *
     * @param collection 待处理的集合
     * @param predicate 过滤条件，指定集合元素需要满足的条件
     * @param <T> 集合中元素的类型
     * @return 过滤后的新列表
     * @throws NullPointerException 如果集合为空或过滤条件为空
     * @see Stream#filter(Predicate)
     * @see Collectors#toList()
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<? super T> predicate) {
        if (Lists.isEmpty(collection))
            return Lists.newArrayList();
        return configure(collection).filter(predicate).collect(Collectors.toList());
    }


    /**
     * #brief: 将集合中的每个元素映射为另一个类型的元素并返回结果列表
     *
     * <p>该方法接收一个集合和一个映射函数，将集合中的每个元素应用映射函数后，返回包含
     * 映射结果的新列表。适用于需要对集合元素进行转换或处理的场景。
     *
     * @param collection 待处理的集合
     * @param function 映射函数，用于将集合中的元素转换为另一个类型
     * @param <T> 集合中原始元素的类型
     * @param <R> 映射后结果列表中元素的类型
     * @return 包含映射结果的新列表
     * @throws NullPointerException 如果集合或映射函数为空
     * @see Stream#map(Function)
     * @see Collectors#toList()
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> function) {
        if (Lists.isEmpty(collection))
            return Lists.newArrayList();
        return configure(collection).map(function).collect(Collectors.toList());
    }

    /**
     * #brief: 去除集合中的重复元素并返回结果列表
     *
     * <p>该方法接收一个集合，去除集合中的重复元素，并返回包含唯一元素的新列表。适用于需要
     * 保证集合元素唯一性的场景。
     *
     * @param collection 待处理的集合
     * @param <T> 集合中元素的类型
     * @return 去重后的新列表
     * @throws NullPointerException 如果集合为空
     * @see Stream#distinct()
     * @see Collectors#toList()
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        if (Lists.isEmpty(collection))
            return Lists.newArrayList();
        return configure(collection).distinct().collect(Collectors.toList());
    }

    /**
     * #brief: 根据自然顺序对集合中的元素进行排序并返回结果列表
     *
     * <p>该方法将集合中的元素按自然顺序进行排序，返回排序后的新列表。适用于集合中元素
     * 已经实现了 {@link Comparable} 接口的情况。
     *
     * @param collection 待排序的集合
     * @param <T> 集合中元素的类型
     * @return 排序后的新列表
     * @throws NullPointerException 如果集合为空
     * @see Comparator#naturalOrder()
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sorted(Collection<T> collection) {
        return sorted(collection, (Comparator<T>) Comparator.naturalOrder());
    }

    /**
     * #brief: 根据逆序对集合中的元素进行排序并返回结果列表
     *
     * <p>该方法将集合中的元素按逆序进行排序，返回排序后的新列表。适用于集合中元素需要
     * 按降序排列的场景。
     *
     * @param collection 待排序的集合
     * @param <T> 集合中元素的类型
     * @return 排序后的新列表
     * @throws NullPointerException 如果集合为空
     * @see Comparator#reverseOrder()
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> reversed(Collection<T> collection) {
        return sorted(collection, (Comparator<T>) Comparator.reverseOrder());
    }

    /**
     * #brief: 根据指定比较器对集合中的元素进行排序并返回结果列表
     *
     * <p>该方法将集合中的元素根据指定的比较器进行排序，返回排序后的新列表。适用于需要
     * 按照自定义规则排序集合元素的场景。
     *
     * @param collection 待排序的集合
     * @param comparator 排序的比较器
     * @param <T> 集合中元素的类型
     * @return 排序后的新列表
     * @throws NullPointerException 如果集合为空或比较器为空
     * @see Stream#sorted(Comparator)
     * @see Collectors#toList()
     */
    public static <T> List<T> sorted(Collection<T> collection, Comparator<T> comparator) {
        if (Lists.isEmpty(collection))
            return Lists.newArrayList();
        return configure(collection).sorted(comparator).collect(Collectors.toList());
    }

    /**
     * #brief: 统计集合中符合条件的元素个数
     *
     * <p>该方法接收一个集合和一个过滤条件，返回集合中符合条件的元素数量。适用于需要
     * 根据特定规则统计集合元素数量的场景。
     *
     * @param collection 待处理的集合
     * @param predicate 过滤条件，指定集合元素需要满足的条件
     * @param <T> 集合中元素的类型
     * @return 符合条件的元素数量
     * @throws NullPointerException 如果集合为空或过滤条件为空
     * @see Stream#filter(Predicate)
     * @see Stream#count()
     */
    public static <T> int count(Collection<T> collection, Predicate<? super T> predicate) {
        if (Lists.isEmpty(collection))
            return 0;
        return (int) configure(collection).filter(predicate).count();
    }

    /**
     * #brief: 判断集合中是否有元素符合指定条件
     *
     * <p>该方法检查集合中是否至少有一个元素满足给定的条件。如果集合为空，返回 false。
     * 适用于需要检查集合中是否有符合条件的元素的场景。
     *
     * @param collection 待检查的集合
     * @param predicate 过滤条件，指定集合元素需要满足的条件
     * @param <T> 集合中元素的类型
     * @return 如果集合中有元素符合条件，则返回 true；否则返回 false
     * @throws NullPointerException 如果集合为空或过滤条件为空
     * @see Stream#anyMatch(Predicate)
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (Lists.isEmpty(collection))
            return false;
        return configure(collection).anyMatch(predicate);
    }

    /**
     * #brief: 判断集合中是否没有任何元素符合指定条件
     *
     * <p>该方法检查集合中是否没有任何元素满足给定的条件。如果集合为空，返回 false。
     * 适用于需要检查集合中是否没有符合条件的元素的场景。
     *
     * @param collection 待检查的集合
     * @param predicate 过滤条件，指定集合元素需要满足的条件
     * @param <T> 集合中元素的类型
     * @return 如果集合中没有元素符合条件，则返回 true；否则返回 false
     * @throws NullPointerException 如果集合为空或过滤条件为空
     * @see Stream#noneMatch(Predicate)
     */
    public static <T> boolean noneMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (Lists.isEmpty(collection))
            return false;
        return configure(collection).noneMatch(predicate);
    }

}
