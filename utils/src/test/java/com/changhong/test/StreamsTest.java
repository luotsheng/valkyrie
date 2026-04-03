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

import com.changhong.collection.Lists;
import com.changhong.stream.Streams;
import com.changhong.system.SystemUtils;
import com.changhong.utils.Generator;
import org.junit.Test;

import java.util.List;

@SuppressWarnings("ALL")
public class StreamsTest {

    @Test
    public void parallelTest() {
        List<Integer> objects = Lists.newArrayList();
        for (int i = 0; i < 100000000; i++)
            objects.add(Generator.randomInt());

        long start = SystemUtils.timestamp();
        System.out.println(Streams.count(objects, o -> o > 1024 && o < Integer.MAX_VALUE));
        long end = SystemUtils.timestamp();
        System.out.printf("非并行计算耗时：%sms\n", (end - start));
    }

}
