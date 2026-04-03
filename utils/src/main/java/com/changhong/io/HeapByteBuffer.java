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
 * @author Luo Tiansheng
 */
public class HeapByteBuffer extends AbstractByteBuffer {

    /** 字节缓冲区 */
    private byte[] buf;
    /** 每次扩容大小为初始分配大小 */
    private static final int initializeCapacity = IOUtils.DEFAULT_BYTE_BUFFER_SIZE;

    HeapByteBuffer(int capacity) {
        buf = new byte[capacity];
    }

    /** 确保数据写入时缓冲区内部容量足够 */
    private void ensureCapacity(int size) {
        if (buf.length < (capacity + size)) {
            byte[] n = new byte[(buf.length + size) + initializeCapacity];
            System.arraycopy(buf, 0, n, 0, buf.length);
            buf = n;
        }
    }

    @Override
    public int size() {
        return buf.length;
    }

    @Override
    public void read0(byte[] b, int off, int len) {
        System.arraycopy(buf, index, b, off, len);
        index += len;
    }

    @Override
    void write0(byte[] a, int off, int len) {
        ensureCapacity(len);
        System.arraycopy(a, off, buf, index, len);
        index += len;
        capacity += len;
    }

    @Override
    public ByteBuffer compact() {
        if (size() > capacity) {
            byte[] n = new byte[capacity];
            System.arraycopy(buf, 0, n, 0, capacity);
            buf = n;
        }
        return this;
    }

}
