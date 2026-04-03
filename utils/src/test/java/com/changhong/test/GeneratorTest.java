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

import com.changhong.utils.Generator;
import org.junit.Test;

@SuppressWarnings("ALL")
public class GeneratorTest {

    @Test
    public void nextCodeTest() {
        for (int i = 0; i < 165; i++) {
            System.out.println(Generator.randomCode(8) + "75b0-11ef-a8c9-005056935d62");
        }
    }

    @Test
    public void nextLetterTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Generator.randomLetterCode(8));
        }
    }

    @Test
    public void nextNumberTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Generator.randomNumberCode(6));
        }
    }

    @Test
    public void nextCodeRangeTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Generator.randomCode(0, 8));
        }
    }

    @Test
    public void nextLetterRangeTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Generator.randomLetterCode(0, 6));
        }
    }

    @Test
    public void nextNumberRangeTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Generator.randomNumberCode(0, 6));
        }
    }

}
