package com.changhong.time;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 处理时间的接口，提供多种时间转换、计算和格式化方法。
 *
 * <p>该接口用于表示具有时间信息的类，提供了多种操作，如将时间转换为不同格式，时间加减操作，
 * 时间差的计算，以及获取时间的各个组件（年、月、日等）。接口还支持时间的格式化输出。<p>
 *
 * <h2>功能特点</h2>
 * <ul>
 *     <li>支持将时间转换为不同时区的 `ZonedDateTime`。</li>
 *     <li>提供加减时间的功能，支持纳秒、毫秒、秒、分钟、小时、天、周、月和年。</li>
 *     <li>计算与其他时间对象或日期之间的时间差，支持不同的单位。</li>
 *     <li>获取时间的各种组件，如纳秒、毫秒、小时、月份等。</li>
 *     <li>提供时间的格式化功能，支持自定义格式输出时间。</li>
 * </ul>
 *
 * <h2>方法说明</h2>
 * <ul>
 *     <li>{@link #toZonedDateTime(String)}: 将时间转换为指定时区的 `ZonedDateTime`。</li>
 *     <li>{@link #toZonedDateTime(ZoneId)}: 使用 `ZoneId` 将时间转换为指定时区的 `ZonedDateTime`。</li>
 *     <li>{@link #toZonedDateTime()}: 获取当前时间的 `ZonedDateTime`。</li>
 *     <li>{@link #getZoneId()}: 获取当前时间的时区。</li>
 *     <li>{@link #getTime()}: 获取当前时间的毫秒表示。</li>
 *     <li>{@link #toDate()}: 将时间转换为 `Date` 类型。</li>
 *     <li>{@link #toLocalDate()}: 将时间转换为 `LocalDate`。</li>
 *     <li>{@link #toLocalDateTime()}: 将时间转换为 `LocalDateTime`。</li>
 *     <li>加时间：{@link #plusNanos(long)}, {@link #plusMillis(long)}, {@link #plusSeconds(int)}, ...</li>
 *     <li>减时间：{@link #minusNanos(int)}, {@link #minusMillis(int)}, {@link #minusSeconds(int)}, ...</li>
 *     <li>计算时间差：如 {@link #betweenNanos(Chrono)}, {@link #betweenMillis(Date)}, ...</li>
 *     <li>获取时间组件：如 {@link #getNano()}, {@link #getDayOfWeek()}, {@link #getYear()}, ...</li>
 *     <li>{@link #format()}: 格式化当前时间为默认字符串。</li>
 *     <li>{@link #format(String)}: 格式化时间为自定义格式的字符串。</li>
 * </ul>
 *
 * @author Luo Tiansheng
 * @see ZonedDateTime
 * @see Chrono
 */
@SuppressWarnings("unused")
public interface Calendar {

    /**
     * 判断当前时间是否是今天。
     *
     * @return 如果是今天，返回 true；否则返回 false
     */
    boolean isToday();

    /**
     * 判断当前时间是否是本周。
     *
     * @return 如果是本周，返回 true；否则返回 false
     */
    boolean isCurrentWeek();

    /**
     * 判断当前时间是否是本月。
     *
     * @return 如果是本月，返回 true；否则返回 false
     */
    boolean isCurrentMonth();

    /**
     * 判断当前时间是否是本年。
     *
     * @return 如果是本年，返回 true；否则返回 false
     */
    boolean isCurrentYear();

    /**
     * 判断当前年份是否为闰年。
     *
     * @return 如果是闰年返回 true，否则返回 false
     */
    boolean isLeap();

    /**
     * 判断当前时间是否早于指定的 Date 对象。
     *
     * @param date 要比较的 Date 对象
     * @return 如果当前时间早于指定时间，则返回 true，否则返回 false
     */
    boolean isBefore(Date date);

    /**
     * 判断当前时间是否早于指定的 LocalDateTime 对象。
     *
     * @param localDateTime 要比较的 LocalDateTime 对象
     * @return 如果当前时间早于指定时间，则返回 true，否则返回 false
     */
    boolean isBefore(LocalDateTime localDateTime);

    /**
     * 判断当前时间是否早于指定的 Chrono 实例。
     *
     * @param chrono 要比较的 Chrono 实例
     * @return 如果当前时间早于指定时间，则返回 true，否则返回 false
     */
    boolean isBefore(Chrono chrono);

    /**
     * 判断当前时间是否晚于指定的 Date 对象。
     *
     * @param date 要比较的 Date 对象
     * @return 如果当前时间晚于指定时间，则返回 true，否则返回 false
     */
    boolean isAfter(Date date);

    /**
     * 判断当前时间是否晚于指定的 LocalDateTime 对象。
     *
     * @param localDateTime 要比较的 LocalDateTime 对象
     * @return 如果当前时间晚于指定时间，则返回 true，否则返回 false
     */
    boolean isAfter(LocalDateTime localDateTime);

    /**
     * 判断当前时间是否晚于指定的 Chrono 实例。
     *
     * @param chrono 要比较的 Chrono 实例
     * @return 如果当前时间晚于指定时间，则返回 true，否则返回 false
     */
    boolean isAfter(Chrono chrono);

    /**
     * 获取当前时间的纳秒数（相对于某个固定时间点，例如 JVM 启动时）。
     *
     * @return 当前时间的纳秒数
     */
    long nanoTime();

    /**
     * 获取当前的时间戳，单位是毫秒。
     *
     * @return 当前时间的毫秒时间戳
     */
    long currentTimeMillis();

    /**
     * 将当前时间转换为指定时区的 `ZonedDateTime`。
     *
     * <p>该方法将时间转换为指定时区（通过 `zoneId`）的 `ZonedDateTime` 对象，允许根据不同的时区进行时间操作。
     *
     * @param zoneId 时区的 ID
     * @return 转换后的 `ZonedDateTime`
     */
    ZonedDateTime toZonedDateTime(String zoneId);

    /**
     * 将当前时间转换为指定时区的 `ZonedDateTime`。
     *
     * <p>该方法将时间转换为指定的 `ZoneId` 对象所代表的时区的 `ZonedDateTime` 对象。
     *
     * @param zoneId 时区对象
     * @return 转换后的 `ZonedDateTime`
     */
    ZonedDateTime toZonedDateTime(ZoneId zoneId);

    /**
     * 获取当前时间的 `ZonedDateTime` 对象。
     *
     * <p>该方法返回当前时间对应的 `ZonedDateTime` 对象，不涉及时区的转换。
     *
     * @return 当前时间的 `ZonedDateTime`
     */
    ZonedDateTime toZonedDateTime();

    /**
     * 获取当前时间的时区 ID。
     *
     * <p>该方法返回当前时间所使用的时区的 `ZoneId` 对象。
     *
     * @return 当前时间的时区 ID
     */
    ZoneId getZoneId();

    /**
     * 获取当前时间的毫秒表示。
     *
     * <p>该方法返回当前时间的毫秒时间戳（自1970年1月1日以来的毫秒数）。
     *
     * @return 当前时间的毫秒表示
     */
    long getTime();

    /**
     * 获取当前月份的最后一天，并返回新的 Chrono 实例。
     *
     * @return 该月份最后一天的 Chrono 实例
     */
    Chrono getLastDayOfMonth();

    /**
     * 获取当前年份的最后一天，并返回新的 Chrono 实例。
     *
     * @return 当前年份最后一天的 Chrono 实例
     */
    Chrono getLastDayOfYear();

    /**
     * 返回当前月份的天数（28、29、30 或 31）。
     *
     * @return 当前月份的总天数
     */
    int lengthOfMonth();

    /**
     * 返回当前年份的天数（365 或 366，闰年为 366）。
     *
     * @return 当前年份的总天数
     */
    int lengthOfYear();

    /**
     * 将当前时间转换为 `Date` 对象。
     *
     * <p>该方法将当前时间转换为标准的 `Date` 类型，适用于与老旧 API 的兼容性需求。
     *
     * @return 当前时间的 `Date` 表示
     */
    Date toDate();

    /**
     * 将当前时间转换为 `LocalDate` 对象。
     *
     * <p>该方法返回当前时间的日期部分，精确到日，忽略时间部分。
     *
     * @return 当前时间的 `LocalDate`
     */
    LocalDate toLocalDate();

    /**
     * 将当前时间转换为 `LocalDateTime` 对象。
     *
     * <p>该方法返回当前时间的日期和时间部分，精确到秒，忽略时区信息。
     *
     * @return 当前时间的 `LocalDateTime`
     */
    LocalDateTime toLocalDateTime();

    /**
     * 将当前时间加上指定的纳秒数。
     *
     * <p>该方法将当前时间增加指定数量的纳秒，返回增加后的时间。
     *
     * @param value 要增加的纳秒数
     * @return 增加后的时间对象
     */
    Chrono plusNanos(long value);

    /**
     * 将当前时间加上指定的毫秒数。
     *
     * <p>该方法将当前时间增加指定数量的毫秒，返回增加后的时间。
     *
     * @param value 要增加的毫秒数
     * @return 增加后的时间对象
     */
    Chrono plusMillis(long value);

    /**
     * 将当前时间加上指定的秒数。
     *
     * <p>该方法将当前时间增加指定数量的秒，返回增加后的时间。
     *
     * @param value 要增加的秒数
     * @return 增加后的时间对象
     */
    Chrono plusSeconds(int value);

    /**
     * 将当前时间加上指定的分钟数。
     *
     * <p>该方法将当前时间增加指定数量的分钟，返回增加后的时间。
     *
     * @param value 要增加的分钟数
     * @return 增加后的时间对象
     */
    Chrono plusMinutes(int value);

    /**
     * 将当前时间加上指定的小时数。
     *
     * <p>该方法将当前时间增加指定数量的小时，返回增加后的时间。
     *
     * @param value 要增加的小时数
     * @return 增加后的时间对象
     */
    Chrono plusHours(int value);

    /**
     * 将当前时间加上指定的天数。
     *
     * <p>该方法将当前时间增加指定数量的天数，返回增加后的时间。
     *
     * @param value 要增加的天数
     * @return 增加后的时间对象
     */
    Chrono plusDays(int value);

    /**
     * 将当前时间加上指定的周数。
     *
     * <p>该方法将当前时间增加指定数量的周，返回增加后的时间。
     *
     * @param value 要增加的周数
     * @return 增加后的时间对象
     */
    Chrono plusWeeks(int value);

    /**
     * 将当前时间加上指定的月数。
     *
     * <p>该方法将当前时间增加指定数量的月，返回增加后的时间。
     *
     * @param value 要增加的月数
     * @return 增加后的时间对象
     */
    Chrono plusMonths(int value);

    /**
     * 将当前时间加上指定的年数。
     *
     * <p>该方法将当前时间增加指定数量的年，返回增加后的时间。
     *
     * @param value 要增加的年数
     * @return 增加后的时间对象
     */
    Chrono plusYears(int value);

    /**
     * 将当前时间减去指定的纳秒数。
     *
     * <p>该方法将当前时间减少指定数量的纳秒，返回减少后的时间。
     *
     * @param value 要减少的纳秒数
     * @return 减少后的时间对象
     */
    Chrono minusNanos(int value);

    /**
     * 将当前时间减去指定的毫秒数。
     *
     * <p>该方法将当前时间减少指定数量的毫秒，返回减少后的时间。
     *
     * @param value 要减少的毫秒数
     * @return 减少后的时间对象
     */
    Chrono minusMillis(int value);

    /**
     * 将当前时间减去指定的秒数。
     *
     * <p>该方法将当前时间减少指定数量的秒，返回减少后的时间。
     *
     * @param value 要减少的秒数
     * @return 减少后的时间对象
     */
    Chrono minusSeconds(int value);

    /**
     * 将当前时间减去指定的分钟数。
     *
     * <p>该方法将当前时间减少指定数量的分钟，返回减少后的时间。
     *
     * @param value 要减少的分钟数
     * @return 减少后的时间对象
     */
    Chrono minusMinutes(int value);

    /**
     * 将当前时间减去指定的小时数。
     *
     * <p>该方法将当前时间减少指定数量的小时，返回减少后的时间。
     *
     * @param value 要减少的小时数
     * @return 减少后的时间对象
     */
    Chrono minusHours(int value);

    /**
     * 将当前时间减去指定的天数。
     *
     * <p>该方法将当前时间减少指定数量的天数，返回减少后的时间。
     *
     * @param value 要减少的天数
     * @return 减少后的时间对象
     */
    Chrono minusDays(int value);

    /**
     * 将当前时间减去指定的周数。
     *
     * <p>该方法将当前时间减少指定数量的周，返回减少后的时间。
     *
     * @param value 要减少的周数
     * @return 减少后的时间对象
     */
    Chrono minusWeeks(int value);

    /**
     * 将当前时间减去指定的月数。
     *
     * <p>该方法将当前时间减少指定数量的月，返回减少后的时间。
     *
     * @param value 要减少的月数
     * @return 减少后的时间对象
     */
    Chrono minusMonths(int value);

    /**
     * 将当前时间减去指定的年数。
     *
     * <p>该方法将当前时间减少指定数量的年，返回减少后的时间。
     *
     * @param value 要减少的年数
     * @return 减少后的时间对象
     */
    Chrono minusYears(int value);

    /**
     * 计算当前时间与给定时间之间的纳秒差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的纳秒差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的纳秒差
     */
    long betweenNanos(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的毫秒差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的毫秒差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的毫秒差
     */
    long betweenMillis(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的秒数差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的秒数差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的秒数差
     */
    long betweenSeconds(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的分钟差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的分钟差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的分钟差
     */
    long betweenMinutes(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的小时差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的小时差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的小时差
     */
    long betweenHours(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的天数差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的天数差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的天数差
     */
    long betweenDays(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的周数差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的周数差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的周数差
     */
    long betweenWeeks(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的月数差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的月数差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的月数差
     */
    long betweenMonths(Chrono temporal);

    /**
     * 计算当前时间与给定时间之间的年数差。
     *
     * <p>该方法返回当前时间与指定时间对象之间的年数差值。
     *
     * @param temporal 给定的时间对象
     * @return 两个时间之间的年数差
     */
    long betweenYears(Chrono temporal);

    /**
     * 计算当前时间与给定日期之间的纳秒差。
     *
     * <p>该方法返回当前时间与指定日期之间的纳秒差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的纳秒差
     */
    long betweenNanos(Date date);

    /**
     * 计算当前时间与给定日期之间的毫秒差。
     *
     * <p>该方法返回当前时间与指定日期之间的毫秒差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的毫秒差
     */
    long betweenMillis(Date date);

    /**
     * 计算当前时间与给定日期之间的秒数差。
     *
     * <p>该方法返回当前时间与指定日期之间的秒数差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的秒数差
     */
    long betweenSeconds(Date date);

    /**
     * 计算当前时间与给定日期之间的分钟差。
     *
     * <p>该方法返回当前时间与指定日期之间的分钟差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的分钟差
     */
    long betweenMinutes(Date date);

    /**
     * 计算当前时间与给定日期之间的小时差。
     *
     * <p>该方法返回当前时间与指定日期之间的小时差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的小时差
     */
    long betweenHours(Date date);

    /**
     * 计算当前时间与给定日期之间的天数差。
     *
     * <p>该方法返回当前时间与指定日期之间的天数差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的天数差
     */
    long betweenDays(Date date);

    /**
     * 计算当前时间与给定日期之间的周数差。
     *
     * <p>该方法返回当前时间与指定日期之间的周数差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的周数差
     */
    long betweenWeeks(Date date);

    /**
     * 计算当前时间与给定日期之间的月数差。
     *
     * <p>该方法返回当前时间与指定日期之间的月数差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的月数差
     */
    long betweenMonths(Date date);

    /**
     * 计算当前时间与给定日期之间的年数差。
     *
     * <p>该方法返回当前时间与指定日期之间的年数差值。
     *
     * @param date 给定的日期对象
     * @return 两个时间之间的年数差
     */
    long betweenYears(Date date);

    /**
     * 设置秒数，并返回新的 Chrono 实例。
     *
     * @param value 目标秒数
     * @return 具有指定秒数的新 Chrono 实例
     */
    Chrono withSecond(int value);

    /**
     * 设置分钟数，并返回新的 Chrono 实例。
     *
     * @param value 目标分钟数
     * @return 具有指定分钟数的新 Chrono 实例
     */
    Chrono withMinute(int value);

    /**
     * 设置小时数，并返回新的 Chrono 实例。
     *
     * @param value 目标小时数
     * @return 具有指定小时数的新 Chrono 实例
     */
    Chrono withHour(int value);

    /**
     * 返回一个新的 Chrono 实例，将当前日期调整为指定的月份中的某一天。
     *
     * @param value 目标日（1-31），如果超出该月天数，可能抛出异常
     * @return 具有更新日期的 Chrono 实例
     */
    Chrono withDayOfMonth(int value);

    /**
     * 返回一个新的 Chrono 实例，将当前日期调整为指定年份中的某一天。
     *
     * @param value 目标日（1-366），如果超出该年天数，可能抛出异常
     * @return 具有更新日期的 Chrono 实例
     */
    Chrono withDayOfYear(int value);

    /**
     * 设置月份，并返回新的 Chrono 实例。
     *
     * @param value 目标月份（1-12）
     * @return 具有指定月份的新 Chrono 实例
     */
    Chrono withMonth(int value);

    /**
     * 设置年份，并返回新的 Chrono 实例。
     *
     * @param value 目标年份
     * @return 具有指定年份的新 Chrono 实例
     */
    Chrono withYear(int value);

    /**
     * 获取当前时间的纳秒部分。
     *
     * <p>该方法返回当前时间的纳秒部分。
     *
     * @return 当前时间的纳秒部分
     */
    int getNano();

    /**
     * 获取当前时间的毫秒部分。
     *
     * <p>该方法返回当前时间的毫秒部分。
     *
     * @return 当前时间的毫秒部分
     */
    int getMilli();

    /**
     * 获取当前时间的秒部分。
     *
     * <p>该方法返回当前时间的秒部分。
     *
     * @return 当前时间的秒部分
     */
    int getSecond();

    /**
     * 获取当前时间的分钟部分。
     *
     * <p>该方法返回当前时间的分钟部分。
     *
     * @return 当前时间的分钟部分
     */
    int getMinute();

    /**
     * 获取当前时间的小时部分。
     *
     * <p>该方法返回当前时间的小时部分。
     *
     * @return 当前时间的小时部分
     */
    int getHour();

    /**
     * 获取当前时间是星期几。
     *
     * <p>该方法返回当前时间的星期几（1 - 7，1代表星期一）。
     *
     * @return 当前时间的星期几
     */
    int getDayOfWeek();

    /**
     * 获取当前时间是当前月的第几天。
     *
     * <p>该方法返回当前时间是当前月的第几天。
     *
     * @return 当前时间是当前月的第几天
     */
    int getDayOfMonth();

    /**
     * 获取当前时间是当前年的第几天。
     *
     * <p>该方法返回当前时间是当前年的第几天。
     *
     * @return 当前时间是当前年的第几天
     */
    int getDayOfYear();

    /**
     * 获取当前时间是当前月的第几周。
     *
     * <p>该方法返回当前时间是当前月的第几周。
     *
     * @return 当前时间是当前月的第几周
     */
    int getWeekOfMonth();

    /**
     * 获取当前时间是当前年的第几周。
     *
     * <p>该方法返回当前时间是当前年的第几周。
     *
     * @return 当前时间是当前年的第几周
     */
    int getWeekOfYear();

    /**
     * 获取当前时间的月份。
     *
     * <p>该方法返回当前时间的月份（1 - 12，1代表1月）。
     *
     * @return 当前时间的月份
     */
    int getMonth();

    /**
     * 获取当前时间的年份。
     *
     * <p>该方法返回当前时间的年份。
     *
     * @return 当前时间的年份
     */
    int getYear();

    /**
     * 格式化当前时间为默认的字符串表示。
     *
     * <p>该方法返回当前时间的字符串表示，使用默认的 yyyy-MM-dd HH:mm:ss 格式。
     *
     * @return 当前时间的字符串表示
     */
    String format();

    /**
     * 格式化当前时间为指定模式的字符串表示。
     *
     * <p>该方法根据给定的模式格式化当前时间。
     *
     * @param pattern 格式化的模式
     * @return 当前时间按指定模式格式化后的字符串表示
     */
    String format(String pattern);

    /**
     * #brief: 获取指定时间间隔后的时间点
     *
     * <p>该方法基于当前时间，计算并返回指定时间间隔后的 `Chrono` 对象。
     * 时间间隔以毫秒为单位。
     *
     * @param duration 未来时间的间隔，单位为毫秒
     * @return 计算后的 `Chrono` 时间点
     */
    Chrono futureMoments(long duration);

    /**
     * #brief: 获取指定时间间隔后的时间点（自定义时间单位）
     *
     * <p>该方法基于当前时间，计算并返回指定时间间隔后的 `Chrono` 对象。
     * 允许自定义时间单位，如秒、分钟、小时等。
     *
     * @param duration 未来时间的间隔
     * @param unit 时间单位，决定 `duration` 的计算方式
     * @return 计算后的 `Chrono` 时间点
     */
    Chrono futureMoments(long duration, TimeUnit unit);

}
