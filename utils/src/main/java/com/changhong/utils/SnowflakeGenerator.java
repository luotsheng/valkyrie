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

/* Creates on 2023/1/10. */

/**
 * 雪花算法生成器实例化对象.
 *
 * @author Luo Tiansheng
 */
public final class SnowflakeGenerator {

    //起始时间戳( 2020-12-26 00:00:00 )
    private static final long START_STAMP = 1608912000000L;

    //序列号占用位数
    private static final long SEQUENCE_BIT = 12;

    //机器标识占用位数
    private static final long MACHINE_BIT = 5;

    //数据中心占用位数
    private static final long DATACENTER_BIT = 5;

    //序列号最大值
    private static final long MAX_SEQUENCE = ~(-1L << 12); // 4095

    /**
     * 偏移量
     **/
    private static final long MACHINE_LEFT = SEQUENCE_BIT;

    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private static long sequence; // 序列号  range(0 ~ 4095)
    private static long lastStamp; // 上一次时间戳

    /**
     * 数据中心ID
     */
    private final int dataCenterId;

    /**
     * 机器ID
     */
    private final int machineId;

    /**
     * 实例化雪花算法ID对象
     */
    public SnowflakeGenerator(int dataCenterId, int machineId) {
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 生成ID
     */
    public long nextId() {
        return nextId(this.dataCenterId, this.machineId);
    }

    /**
     * 生成Id
     *
     * @param dataCenterId 数据中心
     * @param machineId    机器标识
     */
    public static synchronized long nextId(Integer dataCenterId, Integer machineId) {

        long currentStamp = System.currentTimeMillis();

        if (currentStamp < lastStamp) {
            throw new IllegalArgumentException("时间被回退，不能继续产生id");
        }

        if (currentStamp == lastStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                // 序列号已经到最大值, 使用下一个时间戳
                currentStamp = getNextStamp();
            }
        } else {
            //不同毫秒，序列号重置
            sequence = 0L;
        }

        lastStamp = currentStamp;//当前时间戳存档，用于判断下次产生id时间戳是否相同

        return (currentStamp - START_STAMP) << TIMESTAMP_LEFT
                | dataCenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;

    }

    /**
     * 阻塞直至获得下一个时间戳
     */
    private static long getNextStamp() {
        long newStamp = getCurrentStamp();
        while (newStamp <= lastStamp) {
            newStamp = getCurrentStamp();
        }

        return newStamp;
    }

    /**
     * 获取当前时间戳
     */
    private static long getCurrentStamp() {
        return System.currentTimeMillis();
    }

}
