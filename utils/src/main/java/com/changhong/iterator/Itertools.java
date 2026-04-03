package com.changhong.iterator;

import com.changhong.tuple.Pair;
import com.changhong.collection.Lists;

import java.util.Collection;
import java.util.List;

/**
 * `Itertools` 是一个工具类，提供了一系列操作集合或迭代器的静态方法。
 *
 * <p>该类中的方法旨在简化对集合或迭代器的常见操作，如配对、分组、排序等。通过简洁的接口，
 * 可以方便地处理迭代器相关的常见任务。
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public class Itertools {

    /**
     * #brief: 将两个集合按顺序配对，返回元组的可迭代对象
     *
     * <p>该方法将两个集合中的元素按顺序配对，生成一个包含元组的可迭代对象。每个元组的左值来自
     * 第一个集合，右值来自第二个集合。适用于需要将两个集合的元素一一对应地处理的场景。
     *
     * @param lefts 第一个集合，元素将作为元组的左值
     * @param rights 第二个集合，元素将作为元组的右值
     * @param <A> 左集合中元素的类型
     * @param <B> 右集合中元素的类型
     * @return 包含配对元组的可迭代对象
     * @throws IllegalArgumentException 如果两个集合的大小不同
     */
    public static <A, B> Iterable<Pair<A, B>> zip(Collection<A> lefts, Collection<B> rights) {
        // 计算最小总数
        int count = (lefts == null || rights == null) ? 0 : Math.min(lefts.size(), rights.size());

        if (count == 0)
            return Lists.newArrayList();

        List<Pair<A, B>> retPairs = Lists.newArrayList();

        Pair<List<A>, List<B>> zipPair = Pair.of(Lists.newArrayList(lefts), Lists.newArrayList(rights));

        for (int i = 0; i < count; i++)
            retPairs.add(new Pair<>(zipPair.first().get(i), zipPair.second().get(i)));

        return retPairs;
    }

}
