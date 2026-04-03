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

import com.changhong.io.MutableFile;
import com.changhong.utils.Transformer;
import org.junit.Test;

import static com.changhong.utils.Transformer.atos;

@SuppressWarnings("ALL")
public class MutableFileTest {

    /**
     * 随机读写访问测试
     */
    @Test
    public void randomAccessTest() {
        MutableFile mutableFile = new MutableFile(".dat");

        // write float
        mutableFile.open();
        {
            float writeValue = 114.514f;
            mutableFile.writeFloat(writeValue);
            System.out.printf("-------------------------------------------------------------------\n");
            System.out.printf("File random access write float example, write value: %f\n", writeValue);
        }
        mutableFile.close();

            System.out.printf("-------------------------------------------------------------------\n");

        // read float
        mutableFile.open();
        {
            float readValue = 0.0f;
            readValue = mutableFile.readFloat();
            System.out.printf("File random access read float example, read value: %f\n", readValue);
            System.out.printf("-------------------------------------------------------------------\n");
        }
        mutableFile.close();

        mutableFile.forceDelete();
    }

    /**
     * 随机读写访问测试, 写入两个 Int
     */
    @Test
    public void randomAccessWriteTest() {
        MutableFile mutableFile = new MutableFile(".dat");

        mutableFile.open();

        mutableFile.writeInt(1001);
        mutableFile.writeInt(1002);
        mutableFile.writeInt(1003);

        mutableFile.seek(0);

        System.out.printf("----------------------------------------------------\n");
        System.out.printf("random access write example read(0): %s\n", mutableFile.readInt());
        System.out.printf("random access write example read(1): %s\n", mutableFile.readInt());
        System.out.printf("random access write example read(2): %s\n", mutableFile.readInt());
        System.out.printf("----------------------------------------------------\n");

        mutableFile.forceDelete();
    }

    @Test
    public void readBytesTest() {
        System.out.println(Transformer.atos(new MutableFile("Desktop://log.txt").readBytes()));
    }

    @Test
    public void listFileTest() {
        for (MutableFile mutableFile : new MutableFile("D:/aaaa").listFiles()) {
            System.out.println(mutableFile.getPath());
        }
    }

}
