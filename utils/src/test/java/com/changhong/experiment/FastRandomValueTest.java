package com.changhong.experiment;

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

import com.changhong.utils.Generator;
import org.junit.Test;

import java.util.Random;

@SuppressWarnings("ALL")
public class FastRandomValueTest {

    public static int seed = 0;
    public static final long T = System.nanoTime() % 1000000;
    public static final long A = 1103515245;
    public static final long C = System.nanoTime() % 100000;
    public static final long M = (long) Math.pow(2, 32);

    @Test
    public void fastRandomGenerateTest() {
        Random random = new Random();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            /* 5 sec 541 ms */
            fastNextRandom();

            /* 7 sec 118 ms */
            Generator.randomInt(1000, 9999);
        }
    }

    public static int fastNextRandom() {
        seed = (int) ((A * seed + C) % M);
        return seed;
    }

}
