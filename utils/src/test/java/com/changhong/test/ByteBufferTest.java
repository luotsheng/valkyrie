package com.changhong.test;

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

import com.changhong.io.ByteBuffer;
import org.junit.Test;

@SuppressWarnings("ALL")
public class ByteBufferTest {

    @Test
    public void writeTest() {
        ByteBuffer byteBuffer = ByteBuffer.allocate();
        byteBuffer.writeChar('A'); /* 2 Byte */
        byteBuffer.writeShort((short) 255); /* 2 Byte */
        byteBuffer.writeInt(12138); /* 4 Byte */
        byteBuffer.writeLong(10086L); /* 4 Byte */
        byteBuffer.writeFloat(32.5f); /* 4 Byte */
        byteBuffer.writeDouble(64.5d); /* 8 Byte */

        System.out.println(byteBuffer);
        byteBuffer.markIndex().rewind();
        byteBuffer.readChar();
        System.out.println(byteBuffer);
        byteBuffer.reset();
        System.out.println(byteBuffer);
        byteBuffer.compact();
        System.out.println(byteBuffer);

        System.out.println("==================================");
        byteBuffer.rewind();
        System.out.printf("%s: %s\n", byteBuffer, byteBuffer.readChar());
        System.out.printf("%s: %s\n", byteBuffer, byteBuffer.readShort());
        System.out.printf("%s: %s\n", byteBuffer, byteBuffer.readInt());
        System.out.printf("%s: %s\n", byteBuffer, byteBuffer.readLong());
        System.out.printf("%s: %s\n", byteBuffer, byteBuffer.readFloat());
        System.out.printf("%s: %s\n", byteBuffer, byteBuffer.readDouble());

    }

}
