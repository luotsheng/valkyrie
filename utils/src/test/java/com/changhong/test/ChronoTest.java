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

import com.changhong.time.Chrono;
import com.changhong.time.DateFormatter;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

@SuppressWarnings("ALL")
public class ChronoTest {

    @Test
    public void wrapTest() {
        System.out.println(Chrono.from(LocalDate.now()));
    }

    @Test
    public void formatTest() {
        System.out.println(Chrono.now().getDayOfWeek());
    }

    @Test
    public void getLastDayOfMonthTest() {
        System.out.printf("2024年是否是闰年：%s\n", Chrono.of(2024, 01).isLeap());
        System.out.printf("2024年一共有%s天\n", Chrono.of(2024, 01).lengthOfYear());
        for (int i = 1; i < 13; i++) {
            Chrono chrono = Chrono.of(2025, i);
            System.out.printf("2025年%s月份一共有%s天\n", i, chrono.lengthOfMonth());
        }
    }

    @Test
    public void beforeOrAfterTest() {
        Chrono chrono = Chrono.now();
        System.out.printf("is before: %s\n", chrono.isBefore(DateFormatter.parse("2024-01-01")));
        System.out.printf("is after: %s\n", chrono.isAfter(DateFormatter.parse("2024-01-01")));
    }

    @Test
    public void getLastDayTest() {
        Chrono chrono = Chrono.from("2026-02-01 20:30:59");
        System.out.printf("Last day of month: %s\n", chrono.getLastDayOfMonth());
        System.out.printf("Last day of year: %s\n", chrono.getLastDayOfYear());
    }

    @Test
    public void withTest() {
        Chrono chrono = Chrono.from("2024-02-01");
        System.out.printf("With second: %s\n", chrono.withSecond(59));
        System.out.printf("With minute: %s\n", chrono.withMinute(59));
        System.out.printf("With day: %s\n", chrono.withDayOfMonth(28));
    }

    @Test
    public void getWeekOfMonthTest() {
        Chrono chrono = Chrono.from("2024-02-01");
        System.out.println(chrono.getWeekOfMonth());
    }

    @Test
    public void getWeekOfYearTest() {
        Chrono chrono = Chrono.from("2024-02-01");
        System.out.println(chrono.getWeekOfYear());
    }

    public static void main(String[] args) {
        Chrono chrono = Chrono.from(new Date());

        System.out.printf("plus Weeks 1: %s\n", chrono.plusWeeks(1));

        System.out.printf("Day of week: %s\n", chrono.getDayOfWeek());
        System.out.printf("Day of month: %s\n", chrono.getDayOfMonth());
        System.out.printf("Day of year: %s\n", chrono.getDayOfYear());

        System.out.printf("Week of month: %s\n", chrono.getWeekOfMonth());
        System.out.printf("Week of year: %s\n", chrono.getWeekOfYear());

        System.out.printf("Year: %s\n", chrono.getYear());
        System.out.printf("Month: %s\n", chrono.getMonth());
        System.out.printf("Day: %s\n", chrono.getDayOfMonth());
        System.out.printf("Hour: %s\n", chrono.getHour());
        System.out.printf("Minute: %s\n", chrono.getMinute());
        System.out.printf("Second: %s\n", chrono.getSecond());
        System.out.printf("Milli: %s\n", chrono.getMilli());
        System.out.printf("Nano: %s\n", chrono.getNano());

        Chrono c1 = Chrono.of(2023, 2, 1, 19, 3, 10);
        Chrono c2 = Chrono.of(2025, 3, 2, 23, 5, 23);
        System.out.printf("Between Nanos: %s\n", c1.betweenNanos(c2));
        System.out.printf("Between Millis: %s\n", c1.betweenMillis(c2));
        System.out.printf("Between Seconds: %s\n", c1.betweenSeconds(c2));
        System.out.printf("Between Minutes: %s\n", c1.betweenMinutes(c2));
        System.out.printf("Between Hours: %s\n", c1.betweenHours(c2));
        System.out.printf("Between Days: %s\n", c1.betweenDays(c2));
        System.out.printf("Between Months: %s\n", c1.betweenMonths(c2));
        System.out.printf("Between Weeks: %s\n", c1.betweenWeeks(c2));
        System.out.printf("Between Year: %s\n", c1.betweenYears(c2));
    }

}
