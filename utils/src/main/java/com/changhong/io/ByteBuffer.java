package com.changhong.io;

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

/* Creates on 2023/5/8. */

/**
 * ByteBuffer 接口对象（大端模式）
 *
 * @author Luo Tiansheng
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class ByteBuffer {

    /**
     * @return 分配一个默认 4kb 大小的 {@link ByteBuffer} 缓冲区。
     */
    public static ByteBuffer allocate() {
        return allocate(IOUtils.DEFAULT_BYTE_BUFFER_SIZE);
    }

    /**
     * 分配一个默认 {@code size} 大小的 {@link ByteBuffer} 缓冲区。该缓冲区使用 JVM 中内部的堆内存储存数据,
     * 所以这个 {@link ByteBuffer} 的最大存储大小取决于 JVM 的启动参数 {@code -Xmx} 设置的堆内存
     * 有多大。
     * <p>
     * 但是在 JDK1.9 版本之前一般 JVM 的堆内存不会调整到太大，通常来说就几百兆，几个G的大小。JDK9 后
     * 的新 GC 机制对于特大内存（如几百G、几TB的内存）、大数据来说后续 JVM 的内存可能已经足够使用了
     * 并不需要在使用之前的 Unsafe 类去堆外分配新的内存。而且这种情况也不是安全的。
     * <p>
     * 所以通常来说在堆中分配的内存就足以完成 90% 的使用需求了，这个类后续应该也不会扩展 DirectByteBuffer
     * 实现类。
     *
     * @return 创建一个 {@link HeapByteBuffer} 子类对象实例，内部的缓冲区默认大小为
     *         参数 {@code size} 的值。
     */
    public static ByteBuffer allocate(int size) {
        return new HeapByteBuffer(size);
    }

    /**
     * #brief：传入一个字节数组，将字节数组封装成 ByteBuf 对象。读写指针默认在最后一位。<p>
     *
     * 传入一个字节数组，将字节数组封装成 ByteBuf 对象。这个字节数组可以是
     * 任意大小。封装后读写指针位置将重置在最后一位方便数据写入。<p>
     *
     * @param b
     *        字节数组
     *
     * @return 封装了 {@code b} 字节数组的缓冲区对象实例。
     */
    public static ByteBuffer wrap(byte[] b) {
        return wrap(b, 0, b.length);
    }

    /**
     * #brief：传入一个字节数组，将字节数组封装成 ByteBuf 对象。读写指针默认在最后一位。<p>
     *
     * 传入一个字节数组，将字节数组封装成 ByteBuf 对象。这个字节数组可以是
     * 任意大小。封装后读写指针位置将重置在最后一位方便数据写入。<p>
     *
     * @param b
     *        要写入数据的字节数组
     *
     * @param off
     *        开始索引位置
     *
     * @param len
     *        写入长度
     *
     * @return 封装了 {@code b} 字节数组的缓冲区对象实例。
     */
    public static ByteBuffer wrap(byte[] b, int off, int len) {
        ByteBuffer buffer = allocate();
        buffer.writeBytes(b, off, len);
        return buffer;
    }

    /**
     * #brief: 拷贝当前缓冲区数据。<p>
     *
     * 在内存空间中开辟一块新的区域，将当前缓冲区中的数据拷贝到新的缓冲区
     * 对象中，两个缓冲区互不影响。
     *
     * @return 新的缓冲区对象
     */
    public abstract ByteBuffer duplicate();

    /**
     * 获取缓冲区的总大小（单位：字节）。
     *
     * @return 缓冲区的大小。
     */
    public abstract int size();

    /**
     * 获取当前可读的字节数。
     * <p>
     * 该值通常等于 `size() - 当前读取位置`。
     *
     * @return 可读取的字节数。
     */
    public abstract int readableBytes();

    /**
     * @return 当前可写的字节数。
     */
    public abstract int writeableBytes();

    /**
     * @return 返回当前读写索引位置
     */
    public abstract int index();

    /**
     * @return 当前有效的缓冲区大小（单位：字节）。
     */
    public abstract int capacity();

    /**
     * 将缓冲区的读取/写入位置设置到指定的偏移量（相对于起始位置）。
     *
     * @param off 偏移量（从缓冲区起始位置计算）。
     * @return 调整后新的 {@link ByteBuffer} 视图。
     */
    public abstract ByteBuffer seekSet(int off);

    /**
     * 在当前读取/写入位置的基础上进行偏移调整。
     *
     * @param off 偏移量（可正可负，正值向后移动，负值向前移动）。
     * @return 调整后新的 {@link ByteBuffer} 视图。
     */
    public abstract ByteBuffer seekCur(int off);

    /**
     * 以缓冲区末尾为基准，向前或向后偏移指定字节数。
     *
     * @param off 偏移量（通常为负值表示向前移动，0 代表缓冲区末尾）。
     * @return 调整后新的 {@link ByteBuffer} 视图。
     */
    public abstract ByteBuffer seekEnd(int off);

    /**
     * 跳过指定长度的字节，并且将读取/写入位置向后偏移指定长度。<p>
     *
     * @param len 跳过的字节数。
     */
    public abstract void skipBytes(int len);

    /**
     * #brief: 标记当前读取/写入索引位置。<p>
     *
     * 标记当前读写指针索引位置，用于跳过某段内存操作后回到之前标记的位置。使用
     * {@code reset()} 函数回到最开始标记位置，默认索引位置为 0。<p>
     */
    public abstract ByteBuffer markIndex();

    /**
     * #brief: 回到之前标记的索引位置。<p>
     *
     * 将读写指针重置到最开始标记的位置，默认索引位置为 0。<p>
     */
    public abstract ByteBuffer reset();

    /**
     * #brief: 重置读写索引回到 0 的位置<p>
     *
     * 重置读写索引到最开始的位置，也就是索引 0，写入后读取缓冲区内部
     * 数据。
     */
    public abstract ByteBuffer rewind();

    /**
     * #brief: 读取一个 Byte 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 1 个字节的数据。
     *
     * @return 从字节数组读到的 Byte 值
     */
    public abstract byte readByte();

    /**
     * #brief: 从字节数组中读取前 2 个字节并转换为 char 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 2 个字节的数据。将
     * 读取到的字节数组转换成一个 char 类型结果。
     *
     * @return 从字节数组读到的 char 值
     */
    public abstract char readChar();

    /**
     * #brief: 从字节数组中读取前 n * Char.SIZE 个字节并转换为 char[] 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 {@code n * Char.SIZE} 个字节的数据。将
     * 读取到的字节数组转换成一个 char[] 类型结果。
     *
     * @return 从字节数组读到的 char[] 值
     */
    public abstract char[] readChars(int n);

    /**
     * #brief: 从字节数组中读取前 2 个字节并转换为 short 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 2 个字节的数据。将
     * 读取到的字节数组转换成一个 short 类型结果。
     *
     * @return 从字节数组读到的 short 值
     */
    public abstract short readShort();

    /**
     * #brief: 从字节数组中读取前 4 个字节并转换为 int 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 4 个字节的数据。将
     * 读取到的字节数组转换成一个 int 类型结果。
     *
     * @return 从字节数组读到的 int 值
     */
    public abstract int readInt();

    /**
     * #brief: 从字节数组中读取前 8 个字节并转换为 long 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 8 个字节的数据。将
     * 读取到的字节数组转换成一个 long 类型结果。
     *
     * @return 从字节数组读到的 long 值
     */
    public abstract long readLong();

    /**
     * #brief: 从字节数组中读取前 8 个字节并转换为 float 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 4 个字节的数据。将
     * 读取到的字节数组转换成一个 float 类型结果。
     *
     * @return 从字节数组读到的 float 值
     */
    public abstract float readFloat();

    /**
     * #brief: 从字节数组中读取前 8 个字节并转换为 double 类型的值。<p>
     *
     * 根据当前 {@code position} 的开始位置往后读取 8 个字节的数据。将
     * 读取到的字节数组转换成一个 double 类型结果。
     *
     * @return 从字节数组读到的 double 值
     */
    public abstract double readDouble();

    /**
     * #brief: 从字节数组中读取指定长度的字节数据。<p>
     *
     * 读取指定长度的字节数，并返回读取的字节数组。
     *
     * @param nb 读取缓冲区长度
     * @return 新的字节数组
     */
    public abstract byte[] readBytes(int nb);

    /**
     * 读取 {@link ByteBuffer} 中的字节数据，通过传入的 {@code b}、{@code off}、{@code len} 这三个参数
     * 来确定需要读取多少个字节到 {@code b} 字节数组中。这个函数内部调用了 {@link #read0(byte[], int, int)}
     * 函数。它与 {@code read0()} 函数唯一不同的区别就在于，这个函数会做索引越界等校验，来确保读取时的安全性、
     * 以及稳定性
     *
     * @param b
     *        读取 {@link ByteBuffer} 中数据到 {@code b} 这个字节数组中
     *
     * @param off
     *        字节数组起始索引，从 {@code b[off]} 开始写入第一个字节
     *
     * @param len
     *        读取的总长度，字节数组结束索引为 {@code b[off + len]}
     */
    public abstract int readBytes(byte[] b, int off, int len);

    /**
     * #brief: 写入单个字节数据到缓冲区中
     *
     * @param b
     *        要写入的字节
     */
    public abstract ByteBuffer writeByte(byte b);

    /**
     * #brief: 写入 char 类型的数据到字节缓冲区中。<p>
     *
     * 写入 char 值到字节缓冲区中，占用 2 个字节的空间。先写入高位字节，
     * 后写入低位字节。
     *
     * @param c
     *        char 类型
     */
    public abstract ByteBuffer writeChar(char c);

    /**
     * #brief: 写入 char[] 类型的数据到字节缓冲区中。<p>
     *
     * 写入 char[] 值到字节缓冲区中，占用 {@code value / Char.SIZE} 个字节的空间。先写入
     * 高位字节，后写入低位字节。
     *
     * @param ch
     *        char 数组
     */
    public abstract ByteBuffer writeChars(char[] ch);

    /**
     * #brief: 写入 short 类型的数据到字节缓冲区中。<p>
     *
     * 写入 short 值到字节缓冲区中，占用 2 个字节的空间。先写入高位字节，
     * 后写入低位字节。
     *
     * @param v
     *        short 类型的整数
     */
    public abstract ByteBuffer writeShort(short v);

    /**
     * #brief: 写入 int 类型的数据到字节缓冲区中。<p>
     *
     * 写入 int 值到字节缓冲区中，占用 4 个字节的空间。先写入高位字节，
     * 后写入低位字节。
     *
     * @param i
     *        int 类型的整数
     */
    public abstract ByteBuffer writeInt(int i);

    /**
     * #brief: 写入 long 类型的数据到字节缓冲区中。<p>
     *
     * 写入 long 值到字节缓冲区中，占用 8 个字节的空间。先写入高位字节，
     * 后写入低位字节。
     *
     * @param l
     *        long 类型的整数
     */
    public abstract ByteBuffer writeLong(long l);

    /**
     * #brief: 写入 lofloatng 类型的数据到字节缓冲区中。<p>
     *
     * 写入 float 值到字节缓冲区中，占用 4 个字节的空间。先写入高位字节，
     * 后写入低位字节。
     *
     * @param f
     *        float 类型的整数
     */
    public abstract ByteBuffer writeFloat(float f);

    /**
     * #brief: 写入 double 类型的数据到字节缓冲区中。<p>
     *
     * 写入 double 值到字节缓冲区中，占用 8 个字节的空间。先写入高位字节，
     * 后写入低位字节。
     *
     * @param d
     *        double 类型的整数
     */
    public abstract ByteBuffer writeDouble(double d);

    /**
     * 将整个字节缓冲的内容写入到 {@link ByteBuffer} 中。
     */
    public abstract ByteBuffer writeBytes(byte[] b);

    /**
     * 通过 {@code off} 索引读取字节数组中的内存写入到缓冲区。读取的第一个字节从 {@code b[off]} 开始
     * 到 {@code b[len]} 结束。它与大部分 io 操作的传参方式相同。
     *
     * @param b
     *        要写入数据的字节数组
     *
     * @param off
     *        开始索引位置
     *
     * @param len
     *        写入长度
     */
    public abstract ByteBuffer writeBytes(byte[] b, int off, int len);

    /**
     * #brief: ByteBuf 数据读取唯一接口，需要子类自行实现。<p>
     *
     * 读取 {@link ByteBuffer} 中的字节数据，这个函数是一个抽象函数，同时也是所有 {@code read()}
     * 函数的底层接口。类似 {@link #write0(byte[], int, int)} 函数的作用，所有的读取操作底层都
     * 必须由它来完成，子类需要实现这个函数。可以参考 {@link HeapByteBuffer} 实现。这是目前唯一
     * 一个库内自带的实现子类。
     * <p>
     * 实现 {@code read0()} 函数内部并不需要做任何有关校验的代码，因为校验代码在父类中已经提供
     * 了，所以子类实现 {@code read0()} 时只需要实现读取即可。
     *
     * @param b
     *        读取 {@link ByteBuffer} 中数据到 {@code b} 这个字节数组中
     *
     * @param off
     *        字节数组起始索引，从 {@code b[off]} 开始写入第一个字节
     *
     * @param len
     *        读取的总长度，字节数组结束索引为 {@code b[off + len]}
     */
    abstract void read0(byte[] b, int off, int len);

    /**
     * #brief: ByteBuf 数据写入唯一接口，需要子类自行实现。<p>
     *
     * {@code write0()} 函数是 {@link ByteBuffer} 抽象类中需要子类去实现的一个函数。这个函数是
     * 所有数据写入的唯一接口，写入到 {@link ByteBuffer} 中的所有数据都必须经过它，其他的写入函数
     * 也只是在它之上做了一层封装，所以 {@code write0()} 这个函数就决定子类实现的 {@link ByteBuffer}
     * 好不好用、性能、安全等所有关键性问题。
     * <p>
     * 写入数据会有空指针、长度、偏移量等索引检查，但这个函数不需要做任何检查，因为在其他 {@code write()}
     * 函数中都已经做了这样的数据写入检查了。它只需要确保内部缓冲区的容量足够大，并且不会超出边界溢出即可。
     * 完成了这两个需求，那么它就算是一个合格的 {@link ByteBuffer} 子类。
     *
     * @param b
     *        写入的数据内容字节数组
     *
     * @param off
     *        从参数 {@code b[off]} 位置开始读取数据写入到 {@link ByteBuffer}
     *
     * @param len
     *        字节数组的结束位置，读取的内容从 {@code b[off]} 到 {@code b[len]}
     */
    abstract void write0(byte[] b, int off, int len);

    /**
     * @return 分配一个新的 {@code byte} 字节数组，将 {@link ByteBuffer} 缓冲区中
     *         有效的数据拷贝到新分配的字节数组，并返回。
     */
    public abstract byte[] toByteArray();

    /**
     * #brief: 压缩缓冲区内存区域<p>
     *
     * 压缩当前缓冲区内存区域，将未实际使用的缓冲区区域清除，只保留有效缓冲区大小的
     * 数据区域。
     */
    public abstract ByteBuffer compact();

}
