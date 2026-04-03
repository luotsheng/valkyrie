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

import com.alibaba.fastjson.JSON;
import com.changhong.bean.BeanUtils;
import com.changhong.student.HighStudent;
import com.changhong.student.PrimaryStudent;
import org.junit.Test;

@SuppressWarnings("ALL")
public class BeanUtilsTest {

    @Test
    public void copyPropertiesTest() {
        PrimaryStudent judy = new PrimaryStudent();
        judy.setAge(18);

        System.out.println(JSON.toJSONString(BeanUtils.copyProperties(judy, HighStudent.class)));
    }

}
