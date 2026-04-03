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
import com.changhong.string.StringInterface;
import com.changhong.string.StringUtils;
import com.changhong.utils.Generator;
import com.changhong.utils.Transformer;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.changhong.utils.Transformer.atos;

@SuppressWarnings("ALL")
public class StringUtilsTest {

    @Test
    public void operatorsTest() {
        String redGogh = " red_gogh ";
        System.out.printf("STRING_OPERATOR_TRIM: <%s>\n", StringUtils.strcut(redGogh, 0, 0, StringInterface.STRING_IFACE_TRIM_EXT));
        System.out.printf("STRING_OPERATOR_UPPER_CASE: <%s>\n", StringUtils.strcut(redGogh, 0, 0, StringInterface.STRING_IFACE_UPPER_CASE_EXT));
        System.out.printf("STRING_OPERATOR_CAPITALIZE: <%s>\n", StringUtils.strcut(redGogh, 0, 0, StringInterface.STRING_IFACE_TRIM_EXT, StringInterface.STRING_IFACE_CAPITALIZE_EXT));
        System.out.printf("STRING_OPERATOR_REVERSE: <%s>\n", StringUtils.strcut(redGogh, 0, 0, StringInterface.STRING_IFACE_REVERSE_EXT));
        System.out.printf("STRING_OPERATOR_REMOVE_SPACES: <%s>\n", StringUtils.strcut(redGogh, 0, 0, StringInterface.STRING_IFACE_REMOVE_SPACES_EXT));
        System.out.printf("STRING_OPERATOR_LINE_HMP: <%s>\n", StringUtils.strcut(redGogh, 0, 0, StringInterface.STRING_IFACE_REMOVE_SPACES_EXT, StringInterface.STRING_IFACE_LINE_HMP_EXT));
    }

    @Test
    public void atosTest() {
        String vanGogh = "Vincent Van Gogh";
        System.out.printf("atos: byte array to string: %s\n", Transformer.atos(vanGogh.getBytes()));
        System.out.printf("atos: byte array [7 - len] to string: %s\n", Transformer.atos(vanGogh.getBytes(), 7, 0));
    }

    @Test
    public void strcutExmaple() {
        String author = "Luo Tiansheng";
        System.out.println(StringUtils.strcut(author, 0, -1));
    }

    @Test
    public void globMatcherTest() {
        List<String> paths = Lists.newArrayList();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> System.out.print("\rsize: " + paths.size()), 0, 10, TimeUnit.MILLISECONDS);

        boolean isContinue = true;
        while (isContinue) {
            paths.add("/" + Generator.randomLetterCode(3, 8) + "/" + Generator.randomLetterCode(0, 8) + "." + Generator.randomLetterCode(3, 6));
            if (paths.size() > 10000000)
                isContinue = false;
        }
        scheduledExecutorService.shutdown();
        System.out.println();
        paths.add("/api/dwada.xdwadpojawopdjf");
        paths.forEach(V -> {
            if (StringUtils.strant(V, "/api/*.x*f"))
                System.out.println(V);
        });
    }

}
