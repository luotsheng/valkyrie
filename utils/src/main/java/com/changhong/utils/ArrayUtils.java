package com.changhong.utils;

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
 * #brief：提供数组操作的实用工具类。
 *
 * <p>{@code ArrayUtils} 类包含了用于处理数组的常用静态方法。这些方法可以简化数组的拷贝、
 * 检查以及其他常见的操作，避免重复编写低级别的数组处理代码。该类主要用于字节数组的操作，
 * 但可以扩展以支持其他类型的数组。
 *
 * <p>常见方法包括数组的部分拷贝、数组索引和长度检查等。所有方法均为静态方法，因此不需要
 * 实例化该类即可使用。
 *
 * <p>该类旨在为开发者提供高效、可靠的数组操作，减少手动处理数组的复杂度。
 *
 * <p>示例用法：
 * <pre>
 *     byte[] original = {1, 2, 3, 4, 5};
 *     byte[] copy = ArrayUtils.copyOf(original, 0, 3);  // 拷贝前3个元素
 *     ArrayUtils.checkIndexSize(0, 3, original.length);  // 检查索引是否有效
 * </pre>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>数组拷贝</li>
 *   <li>数组长度与边界检查</li>
 *   <li>其他与数组操作相关的常见功能</li>
 * </ul>
 *
 * @author Luo Tiansheng
 * @since 1.8
 */
public class ArrayUtils {

    /**
     * 数据拷贝时对长度进行截断，使得数据可以像指针一样，在某个段落中前后操作
     * 数据。
     *
     * @param size 数组大小
     * @param off  偏移量
     * @param len  长度
     * @return 返回截断后的数据长度
     */
    public static int truncate(int size, int off, int len) {
        return len <= 0 ? (size - off) - Math.abs(len) : len;
    }

    /**
     * #brief：检查数组偏移量和长度是否在数组大小范围内。
     *
     * <p>该方法用于检查偏移量 {@code off} 和长度 {@code len} 的和是否超出数组的大小 {@code size}。
     * 如果超出范围，则会抛出异常，提示索引越界的错误。
     *
     * <p>该方法通常用于确保在进行数组操作时不会发生越界，保证偏移量和长度合法。
     *
     * @param off  偏移量，从该位置开始进行数组操作
     * @param len  要操作的长度
     * @param size 数组的总大小，用于判断是否越界
     *
     * @throws IllegalArgumentException 如果偏移量和长度的和超出数组大小范围
     */
    public static void checkIndexSize(int off, int len, int size) {
        Assert.isTrue(!((off + len ) > size), "Array offset and size out of index: %s", size);
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static byte[] copyOf(byte[] original, int off, int len) {
        len = truncate(original.length, off, len);
        byte[] ret = new byte[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static char[] copyOf(char[] original, int off, int len) {
        len = truncate(original.length, off, len);
        char[] ret = new char[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static short[] copyOf(short[] original, int off, int len) {
        len = truncate(original.length, off, len);
        short[] ret = new short[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static int[] copyOf(int[] original, int off, int len) {
        len = truncate(original.length, off, len);
        int[] ret = new int[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static long[] copyOf(long[] original, int off, int len) {
        len = truncate(original.length, off, len);
        long[] ret = new long[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static float[] copyOf(float[] original, int off, int len) {
        len = truncate(original.length, off, len);
        float[] ret = new float[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static double[] copyOf(double[] original, int off, int len) {
        len = truncate(original.length, off, len);
        double[] ret = new double[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static boolean[] copyOf(boolean[] original, int off, int len) {
        len = truncate(original.length, off, len);
        boolean[] ret = new boolean[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static String[] copyOf(String[] original, int off, int len) {
        len = truncate(original.length, off, len);
        String[] ret = new String[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }

    /**
     * #brief：拷贝数组中的数据到另一个新实例化的数组中
     *
     * <p>将数组总的数据拷贝到一个新的数组中，数组的长度介于 {@code original.length} 到
     * {@code -original.length} 之间。可以通过数组切片的形式拷贝数组。当 {@code len} 参
     * 数为负数时，那么则表示 {@code len=original.length - len} 的新数组长度。也就是从数组
     * 最后开始计算。如果 {@code len} 是 {@code 0} 则表示 从 original[off] - original[original.length]
     * 之间的数据。
     *
     * <p>该函数有很多相似函数，支持泛型对象拷贝。
     *
     * @param original 原数组对象，将从这个数组对象中拷贝数据到新的数组对象中。
     * @param off 偏移量，拷贝起始位置将从 {@code origin[off]} 开始算。
     * @param len 拷贝长度
     * @return 拷贝后的新数组对象。
     */
    public static Object[] copyOf(Object[] original, int off, int len) {
        len = truncate(original.length, off, len);
        Object[] ret = new Object[len];
        System.arraycopy(original, off, ret, 0, len);
        return ret;
    }


}
