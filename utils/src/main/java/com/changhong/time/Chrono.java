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

import java.io.Serializable;
import java.time.*;
import java.time.temporal.*;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * `Chrono` 类表示一个时间单位，提供了对日期和时间的操作。它实现了 {@link Calendar} 接口，
 * {@link Temporal} 接口，并且可以进行比较。该类可以用于表示时间戳、日期、时间等多种形式，并提供
 * 丰富的日期和时间操作功能。
 *
 * <p>此类是不可变的，线程安全的，且支持序列化。
 *
 * @author Luo Tiansheng
 * @see Calendar
 * @see Temporal
 * @see Comparable
 * @see Serializable
 * @noinspection unused
 */
public final class Chrono extends Date
        implements Calendar, Temporal, Serializable {

    /**
     * 存储和处理核心本地时间的字段。
     *
     * <p>这些字段提供了与本地时间、时区、日期等相关的信息，可以用于时间的计算和转换。
     * 其中包括本地时间、时区、日期、周字段以及时间戳。
     */
    private final LocalDateTime _core_local_time;

    /**
     * 存储与当前对象关联的时区信息。
     *
     * <p>该字段用于表示当前对象的时区，使用 {@link ZoneId} 来存储时区。
     */
    private final ZoneId zoneId;

    /**
     * 存储日期信息的字段。
     *
     * <p>该字段存储一个 {@link Date} 对象，表示当前对象的日期。
     */
    private final Date date;

    /**
     * 存储本地日期信息的字段。
     *
     * <p>该字段存储一个 {@link LocalDate} 对象，表示当前对象的日期（不包含时间部分）。
     */
    private final LocalDate localDate;

    /**
     * 存储周字段信息的字段。
     *
     * <p>该字段存储一个 {@link WeekFields} 对象，用于处理基于周的日期信息。
     */
    private final WeekFields weekFields;

    /**
     * 存储时间戳信息。
     *
     * <p>该字段表示当前时间的时间戳，以毫秒为单位。
     */
    private final long timestamp;

    /**
     * 系统默认的时区。
     *
     * <p>该常量存储系统默认时区，使用 {@link ZoneId#systemDefault()} 获取。
     */
    private static final ZoneId SYSTEM_DEFAULT_ZONE_ID = ZoneId.systemDefault();

    // non-required zone id

    /**
     * 默认构造方法，使用当前日期创建一个新的 {@link Chrono} 对象。
     */
    private Chrono() {
        this(new Date());
    }

    /**
     * 根据日期时间字符串创建一个新的 {@link Chrono} 对象，使用默认的日期格式解析器。
     *
     * @param text 日期时间字符串
     */
    private Chrono(String text) {
        this(DateFormatter.parse(text));
    }

    /**
     * 根据日期时间字符串和指定的日期格式创建一个新的 {@link Chrono} 对象。
     *
     * @param text 日期时间字符串
     * @param pattern 日期格式模式
     */
    private Chrono(String text, String pattern) {
        this(DateFormatter.parse(text, pattern));
    }

    /**
     * 根据年份和月份创建一个新的 {@link Chrono} 对象，默认日期为该月的第一天，时间为00:00:00。
     *
     * @param year  年份
     * @param month 月份
     */
    private Chrono(int year, int month) {
        this(year, month, 1, 0, 0, 0, 0);
    }

    /**
     * 根据年份、月份和日期创建一个新的 {@link Chrono} 对象，时间为00:00:00。
     *
     * @param year  年份
     * @param month 月份
     * @param day   日期
     */
    private Chrono(int year, int month, int day) {
        this(year, month, day, 0, 0, 0);
    }

    /**
     * 根据年份、月份、日期和小时创建一个新的 {@link Chrono} 对象，分钟和秒为00。
     *
     * @param year  年份
     * @param month 月份
     * @param day   日期
     * @param hour  小时
     */
    private Chrono(int year, int month, int day, int hour) {
        this(year, month, day, hour, 0, 0, 0);
    }

    /**
     * 根据年份、月份、日期、小时和分钟创建一个新的 {@link Chrono} 对象，秒和毫秒为00。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     */
    private Chrono(int year, int month, int day, int hour, int minute) {
        this(year, month, day, hour, minute, 0, 0);
    }

    /**
     * 根据年份、月份、日期、小时、分钟和秒创建一个新的 {@link Chrono} 对象，毫秒为00。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     */
    private Chrono(int year, int month, int day, int hour, int minute, int second) {
        this(year, month, day, hour, minute, second, 0);
    }

    /**
     * 根据年份、月份、日期、小时、分钟、秒和毫秒创建一个新的 {@link Chrono} 对象。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @param millis 毫秒
     */
    private Chrono(int year, int month, int day, int hour, int minute, int second, int millis) {
        this(LocalDateTime.of(year, month, day, hour, minute, second, millis));
    }

    /**
     * 使用 {@link LocalDateTime} 对象创建一个新的 {@link Chrono} 对象。
     *
     * @param localDateTime {@link LocalDateTime} 对象
     */
    private Chrono(LocalDateTime localDateTime) {
        this(localDateTime.atZone(SYSTEM_DEFAULT_ZONE_ID).toInstant().toEpochMilli());
    }

    /**
     * 使用时间戳（毫秒）和系统默认时区创建一个新的 {@link Chrono} 对象。
     *
     * @param timestamp 时间戳
     */
    private Chrono(long timestamp) {
        this(timestamp, SYSTEM_DEFAULT_ZONE_ID);
    }

    // required zone id

    /**
     * 根据年份、月份和时区创建一个新的 {@link Chrono} 对象，默认日期为该月的第一天，时间为00:00:00。
     *
     * @param year   年份
     * @param month  月份
     * @param zoneId 时区
     */
    private Chrono(int year, int month, ZoneId zoneId) {
        this(year, month, 1, 0, 0, 0, 0, zoneId);
    }

    /**
     * 根据年份、月份、日期和时区创建一个新的 {@link Chrono} 对象，时间为00:00:00。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param zoneId 时区
     */
    private Chrono(int year, int month, int day, ZoneId zoneId) {
        this(year, month, day, 0, 0, 0, zoneId);
    }

    /**
     * 根据年份、月份、日期、小时和时区创建一个新的 {@link Chrono} 对象，分钟和秒为00。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param zoneId 时区
     */
    private Chrono(int year, int month, int day, int hour, ZoneId zoneId) {
        this(year, month, day, hour, 0, 0, 0, zoneId);
    }

    /**
     * 根据年份、月份、日期、小时、分钟和时区创建一个新的 {@link Chrono} 对象，秒和毫秒为00。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     * @param zoneId 时区
     */
    private Chrono(int year, int month, int day, int hour, int minute, ZoneId zoneId) {
        this(year, month, day, hour, minute, 0, 0, zoneId);
    }

    /**
     * 根据年份、月份、日期、小时、分钟、秒和时区创建一个新的 {@link Chrono} 对象，毫秒为00。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @param zoneId 时区
     */
    private Chrono(int year, int month, int day, int hour, int minute, int second, ZoneId zoneId) {
        this(year, month, day, hour, minute, second, 0, zoneId);
    }

    /**
     * 根据年份、月份、日期、小时、分钟、秒、毫秒和时区创建一个新的 {@link Chrono} 对象。
     *
     * @param year   年份
     * @param month  月份
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @param millis 毫秒
     * @param zoneId 时区
     */
    private Chrono(int year, int month, int day, int hour, int minute, int second, int millis, ZoneId zoneId) {
        this(LocalDateTime.of(year, month, day, hour, minute, second, millis), zoneId);
    }

    /**
     * 根据 {@link Date} 对象创建一个新的 {@link Chrono} 对象，使用系统默认时区。
     *
     * @param date {@link Date} 对象
     */
    private Chrono(Date date) {
        this(date.getTime(), SYSTEM_DEFAULT_ZONE_ID);
    }

    /**
     * 根据 {@link LocalDateTime} 和时区创建一个新的 {@link Chrono} 对象。
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @param zoneId        时区
     */
    private Chrono(LocalDateTime localDateTime, ZoneId zoneId) {
        this(localDateTime.atZone(zoneId).toInstant().toEpochMilli(), zoneId);
    }

    /**
     * 根据时间戳和时区创建一个新的 {@link Chrono} 对象。
     *
     * @param timestamp 时间戳（毫秒）
     * @param zoneId    时区
     */
    private Chrono(long timestamp, ZoneId zoneId) {
        super(timestamp);

        // 将时间戳转为 LocalDateTime 对象
        _core_local_time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);

        // 初始化属性
        this.zoneId = zoneId;
        this.timestamp = _core_local_time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.date = new Date(timestamp);
        this.localDate = _core_local_time.toLocalDate();
        this.weekFields = WeekFields.of(Locale.getDefault());
    }

    /**
     * 创建当前时间的 `Chrono` 实例。
     *
     * @return 当前时间的 `Chrono` 实例
     */
    public static Chrono now() {
        return new Chrono();
    }

    /**
     * 使用文本日期字符串创建 `Chrono` 实例。
     *
     * @param text 日期字符串
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(String text) {
        return new Chrono(text);
    }

    /**
     * 使用文本日期字符串和自定义日期格式创建 `Chrono` 实例。
     *
     * @param text 日期字符串
     * @param pattern 日期格式
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(String text, String pattern) {
        return new Chrono(text, pattern);
    }

    /**
     * 创建具有指定年份和月份的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month) {
        return new Chrono(year, month);
    }

    /**
     * 创建具有指定年份、月份和日期的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day) {
        return new Chrono(year, month, day);
    }

    /**
     * 创建具有指定年份、月份、日期和小时的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour) {
        return new Chrono(year, month, day, hour);
    }

    /**
     * 创建具有指定年份、月份、日期、小时和分钟的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param minute 分钟
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, int minute) {
        return new Chrono(year, month, day, hour, minute);
    }

    /**
     * 创建具有指定年份、月份、日期、小时、分钟和秒钟的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param minute 分钟
     * @param second 秒钟
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, int minute, int second) {
        return new Chrono(year, month, day, hour, minute, second);
    }

    /**
     * 创建具有指定年份、月份、日期、小时、分钟、秒钟和毫秒的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param minute 分钟
     * @param second 秒钟
     * @param millis 毫秒
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, int minute, int second, int millis) {
        return new Chrono(year, month, day, hour, minute, second, millis);
    }

    /**
     * 创建具有指定年份、月份和时区的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, ZoneId zoneId) {
        return new Chrono(year, month, zoneId);
    }

    /**
     * 创建具有指定年份、月份、日期和时区的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, ZoneId zoneId) {
        return new Chrono(year, month, day, zoneId);
    }

    /**
     * 创建具有指定年份、月份、日期、小时和时区的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, ZoneId zoneId) {
        return new Chrono(year, month, day, hour, zoneId);
    }

    /**
     * 创建具有指定年份、月份、日期、小时、分钟和时区的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param minute 分钟
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, int minute, ZoneId zoneId) {
        return new Chrono(year, month, day, hour, minute, zoneId);
    }

    /**
     * 创建具有指定年份、月份、日期、小时、分钟、秒钟和时区的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param minute 分钟
     * @param second 秒钟
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, int minute, int second, ZoneId zoneId) {
        return new Chrono(year, month, day, hour, minute, second, zoneId);
    }

    /**
     * 创建具有指定年份、月份、日期、小时、分钟、秒钟、毫秒和时区的 `Chrono` 实例。
     *
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param hour 小时
     * @param minute 分钟
     * @param second 秒钟
     * @param millis 毫秒
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono of(int year, int month, int day, int hour, int minute, int second, int millis, ZoneId zoneId) {
        return new Chrono(year, month, day, hour, minute, second, millis, zoneId);
    }

    /**
     * 使用 `LocalDate` 创建 `Chrono` 实例。
     *
     * @param localDate `localDate` 对象
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(LocalDate localDate) {
        return new Chrono(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    /**
     * 使用 `LocalDateTime` 创建 `Chrono` 实例。
     *
     * @param localDateTime `LocalDateTime` 对象
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(LocalDateTime localDateTime) {
        return new Chrono(localDateTime);
    }

    /**
     * 使用时间戳创建 `Chrono` 实例。
     *
     * @param timestamp 时间戳（毫秒）
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(long timestamp) {
        return new Chrono(timestamp);
    }

    /**
     * 使用 `Date` 对象创建 `Chrono` 实例。
     *
     * @param date `Date` 对象
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(Date date) {
        return new Chrono(date);
    }

    /**
     * 使用 `LocalDateTime` 和时区创建 `Chrono` 实例。
     *
     * @param localDateTime `LocalDateTime` 对象
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(LocalDateTime localDateTime, ZoneId zoneId) {
        return new Chrono(localDateTime, zoneId);
    }

    /**
     * 使用时间戳和时区创建 `Chrono` 实例。
     *
     * @param timestamp 时间戳（毫秒）
     * @param zoneId 时区
     * @return 相应的 `Chrono` 实例
     */
    public static Chrono from(long timestamp, ZoneId zoneId) {
        return new Chrono(timestamp, zoneId);
    }

    /**
     * 获取今天的日期（日）。
     *
     * @return 当前日期的天数
     */
    public static int getToday() {
        return now().getDayOfMonth();
    }

    /**
     * 获取当前月份的周数。
     *
     * @return 当前月份的周数
     */
    public static int getCurrentWeekOfMonth() {
        return now().getWeekOfMonth();
    }

    /**
     * #brief: 获取指定时间间隔后的时间点
     *
     * <p>该方法基于当前时间，计算并返回指定时间间隔后的 `Chrono` 对象。
     * 时间间隔以毫秒为单位。
     *
     * @param duration 未来时间的间隔，单位为秒
     * @return 计算后的 `Chrono` 时间点
     */
    public static Chrono moments(long duration) {
        return moments(duration, TimeUnit.SECONDS);
    }

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
    public static Chrono moments(long duration, TimeUnit unit) {
        return now().futureMoments(duration, unit);
    }

    @Override
    public boolean isToday() {
        return isCurrentMonth() && (getDayOfYear() == now().getDayOfYear());
    }

    @Override
    public boolean isCurrentWeek() {
        return isCurrentYear() && (getWeekOfYear() == now().getWeekOfYear());
    }

    @Override
    public boolean isCurrentMonth() {
        return isCurrentYear() && (getMonth() == now().getMonth());
    }

    @Override
    public boolean isCurrentYear() {
        return (getYear() == now().getYear());
    }

    @Override
    public boolean isLeap() {
        return Year.of(_core_local_time.getYear()).isLeap();
    }

    @Override
    public boolean isBefore(Date date) {
        return isBefore(new Chrono(date));
    }

    @Override
    public boolean isBefore(LocalDateTime localDateTime) {
        return isBefore(new Chrono(localDateTime));
    }

    @Override
    public boolean isBefore(Chrono chrono) {
        return _core_local_time.isBefore(chrono._core_local_time);
    }

    @Override
    public boolean isAfter(Date date) {
        return isAfter(new Chrono(date));
    }

    @Override
    public boolean isAfter(LocalDateTime localDateTime) {
        return isAfter(new Chrono(localDateTime));
    }

    @Override
    public boolean isAfter(Chrono chrono) {
        return _core_local_time.isAfter(chrono._core_local_time);
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public ZonedDateTime toZonedDateTime(String zoneId) {
        return toZonedDateTime(ZoneId.of(zoneId));
    }

    @Override
    public ZonedDateTime toZonedDateTime(ZoneId zoneId) {
        return _core_local_time.atZone(zoneId);
    }

    @Override
    public ZonedDateTime toZonedDateTime() {
        return _core_local_time.atZone(zoneId);
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public long getTime() {
        return timestamp;
    }

    @Override
    public Chrono getLastDayOfMonth() {
        return new Chrono(_core_local_time.with(TemporalAdjusters.lastDayOfMonth()));
    }

    @Override
    public Chrono getLastDayOfYear() {
        return new Chrono(_core_local_time.with(TemporalAdjusters.lastDayOfYear()));
    }

    @Override
    public int lengthOfMonth() {
        return _core_local_time.getMonth().length(Year.isLeap(_core_local_time.getYear()));
    }

    @Override
    public int lengthOfYear() {
        return Year.of(_core_local_time.getYear()).length();
    }

    @Override
    public Date toDate() {
        return date;
    }

    @Override
    public LocalDate toLocalDate() {
        return localDate;
    }

    @Override
    public LocalDateTime toLocalDateTime() {
        return _core_local_time;
    }

    @Override
    public Chrono plusNanos(long value) {
        return new Chrono(_core_local_time.plusNanos(value));
    }

    @Override
    public Chrono plusMillis(long value) {
        return new Chrono(_core_local_time.plusNanos(value * 1_000_000));
    }

    @Override
    public Chrono plusSeconds(int value) {
        return new Chrono(_core_local_time.plusSeconds(value));
    }

    @Override
    public Chrono plusMinutes(int value) {
        return new Chrono(_core_local_time.plusMinutes(value));
    }

    @Override
    public Chrono plusHours(int value) {
        return new Chrono(_core_local_time.plusHours(value));
    }

    @Override
    public Chrono plusDays(int value) {
        return new Chrono(_core_local_time.plusDays(value));
    }

    @Override
    public Chrono plusWeeks(int value) {
        return new Chrono(_core_local_time.plusWeeks(value));
    }

    @Override
    public Chrono plusMonths(int value) {
        return new Chrono(_core_local_time.plusMonths(value));
    }

    @Override
    public Chrono plusYears(int value) {
        return new Chrono(_core_local_time.plusYears(value));
    }

    @Override
    public Chrono minusNanos(int value) {
        return new Chrono(_core_local_time.minusNanos(value));
    }

    @Override
    public Chrono minusMillis(int value) {
        return new Chrono(_core_local_time.minusNanos((long) value * 1000));
    }

    @Override
    public Chrono minusSeconds(int value) {
        return new Chrono(_core_local_time.minusSeconds(value));
    }

    @Override
    public Chrono minusMinutes(int value) {
        return new Chrono(_core_local_time.minusMinutes(value));
    }

    @Override
    public Chrono minusHours(int value) {
        return new Chrono(_core_local_time.minusHours(value));
    }

    @Override
    public Chrono minusDays(int value) {
        return new Chrono(_core_local_time.minusDays(value));
    }

    @Override
    public Chrono minusWeeks(int value) {
        return new Chrono(_core_local_time.minusWeeks(value));
    }

    @Override
    public Chrono minusMonths(int value) {
        return new Chrono(_core_local_time.minusMonths(value));
    }

    @Override
    public Chrono minusYears(int value) {
        return new Chrono(_core_local_time.minusYears(value));
    }

    @Override
    public long betweenNanos(Chrono chrono) {
        return Duration.between(this, chrono).toNanos();
    }

    @Override
    public long betweenMillis(Chrono chrono) {
        return Duration.between(this, chrono).toMillis();
    }

    @Override
    public long betweenSeconds(Chrono chrono) {
        return Duration.between(this, chrono).toSeconds();
    }

    @Override
    public long betweenMinutes(Chrono chrono) {
        return Duration.between(this, chrono).toMinutes();
    }

    @Override
    public long betweenHours(Chrono chrono) {
        return Duration.between(this, chrono).toHours();
    }

    @Override
    public long betweenDays(Chrono chrono) {
        return Duration.between(this, chrono).toDays();
    }

    @Override
    public long betweenWeeks(Chrono chrono) {
        return ChronoUnit.WEEKS.between(this, chrono);
    }

    @Override
    public long betweenMonths(Chrono chrono) {
        return ChronoUnit.MONTHS.between(this, chrono);
    }

    @Override
    public long betweenYears(Chrono chrono) {
        return ChronoUnit.YEARS.between(this, chrono);
    }

    @Override
    public long betweenNanos(Date date) {
        return betweenNanos(new Chrono(date));
    }

    @Override
    public long betweenMillis(Date date) {
        return betweenMillis(new Chrono(date));
    }

    @Override
    public long betweenSeconds(Date date) {
        return betweenSeconds(new Chrono(date));
    }

    @Override
    public long betweenMinutes(Date date) {
        return betweenMinutes(new Chrono(date));
    }

    @Override
    public long betweenHours(Date date) {
        return betweenHours(new Chrono(date));
    }

    @Override
    public long betweenDays(Date date) {
        return betweenDays(new Chrono(date));
    }

    @Override
    public long betweenWeeks(Date date) {
        return betweenWeeks(new Chrono(date));
    }

    @Override
    public long betweenMonths(Date date) {
        return betweenMonths(new Chrono(date));
    }

    @Override
    public long betweenYears(Date date) {
        return betweenYears(new Chrono(date));
    }

    @Override
    public Chrono withSecond(int value) {
        return new Chrono(_core_local_time.withSecond(value));
    }

    @Override
    public Chrono withMinute(int value) {
        return new Chrono(_core_local_time.withMinute(value));
    }

    @Override
    public Chrono withHour(int value) {
        return new Chrono(_core_local_time.withHour(value));
    }

    @Override
    public Chrono withDayOfMonth(int value) {
        return new Chrono(_core_local_time.withDayOfMonth(value));
    }

    @Override
    public Chrono withDayOfYear(int value) {
        return new Chrono(_core_local_time.withDayOfYear(value));
    }

    @Override
    public Chrono withMonth(int value) {
        return new Chrono(_core_local_time.withMonth(value));
    }

    @Override
    public Chrono withYear(int value) {
        return new Chrono(_core_local_time.withYear(value));
    }

    @Override
    public int getNano() {
        return _core_local_time.getNano();
    }

    @Override
    public int getMilli() {
        return getNano() / 1_000_000;
    }

    @Override
    public int getSecond() {
        return _core_local_time.getSecond();
    }

    @Override
    public int getMinute() {
        return _core_local_time.getMinute();
    }

    @Override
    public int getHour() {
        return _core_local_time.getHour();
    }

    @Override
    public int getDayOfWeek() {
        return _core_local_time.getDayOfWeek().getValue();
    }

    @Override
    public int getDayOfMonth() {
        return _core_local_time.getDayOfMonth();
    }

    @Override
    public int getDayOfYear() {
        return _core_local_time.getDayOfYear();
    }

    @Override
    public int getWeekOfMonth() {
        return _core_local_time.get(weekFields.weekOfMonth());
    }

    @Override
    public int getWeekOfYear() {
        return _core_local_time.get(weekFields.weekOfYear());
    }

    @Override
    @SuppressWarnings({"deprecation", "MagicConstant"})
    public int getMonth() {
        return _core_local_time.getMonth().getValue();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getYear() {
        return _core_local_time.getYear();
    }

    @Override
    public String format() {
        return format("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public String format(String pattern) {
        return DateFormatter.format(toDate(), pattern);
    }

    @Override
    public Chrono futureMoments(long duration) {
        return futureMoments(duration, TimeUnit.SECONDS);
    }

    @Override
    public Chrono futureMoments(long duration, TimeUnit unit) {
        return plusMillis(unit.toMillis(duration));
    }

    @Override
    public String toString() {
        return format();
    }

    /////////////////////////////////////////////////////////////////////////
    /// Temporal
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return _core_local_time.isSupported(unit);
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        return _core_local_time.with(field, newValue);
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        return _core_local_time.plus(amountToAdd, unit);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return _core_local_time.until(endExclusive, unit);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        return _core_local_time.isSupported(field);
    }

    @Override
    public long getLong(TemporalField field) {
        return _core_local_time.getLong(field);
    }

}
