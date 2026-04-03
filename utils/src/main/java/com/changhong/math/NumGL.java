package com.changhong.math;

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

/**
 * #brief: 数值计算工具类
 *
 * <p>提供一系列数值计算相关的静态方法，包括四舍五入、绝对值、幂运算、阶乘、
 * 三角函数、角度与弧度转换、误差计算以及平方根等。
 * 适用于需要高精度数值计算的场景。
 *
 * @author Luo Tiansheng
 */
public class NumGL {

    /**
     * #brief: 四舍五入取整
     *
     * <p>将浮点数四舍五入为最接近的整数。
     *
     * @param x 输入的浮点数
     * @return 四舍五入后的整数
     */
    public static int round(float x) {
        return (int) (x + 0.5f);
    }

    /**
     * #brief: 计算 short 类型绝对值
     *
     * <p>返回 short 类型数值的绝对值。
     *
     * @param x 输入的 short 值
     * @return 绝对值
     */
    public static int abs(short x) {
        return x <= 0 ? -x : x;
    }

    /**
     * #brief: 计算 int 类型绝对值
     *
     * <p>返回 int 类型数值的绝对值。
     *
     * @param x 输入的 int 值
     * @return 绝对值
     */
    public static int abs(int x) {
        return x <= 0 ? -x : x;
    }

    /**
     * #brief: 计算 long 类型绝对值
     *
     * <p>返回 long 类型数值的绝对值。
     *
     * @param x 输入的 long 值
     * @return 绝对值
     */
    public static long abs(long x) {
        return x <= 0L ? -x : x;
    }

    /**
     * #brief: 计算 float 类型绝对值
     *
     * <p>返回 float 类型数值的绝对值。
     *
     * @param x 输入的 float 值
     * @return 绝对值
     */
    public static float abs(float x) {
        return x <= 0.0f ? -x : x;
    }

    /**
     * #brief: 计算 double 类型绝对值
     *
     * <p>返回 double 类型数值的绝对值。
     *
     * @param x 输入的 double 值
     * @return 绝对值
     */
    public static double abs(double x) {
        return x <= 0.0D ? -x : x;
    }

    /**
     * #brief: 计算浮点数的幂
     *
     * <p>计算浮点数 a 的 b 次幂，支持负指数。
     *
     * @param a 底数
     * @param b 指数
     * @return a 的 b 次幂
     */
    public static float pow(float a, int b) {
        float q = 1.0f;
        boolean n = a < 0;
        while (b > 0) {
            if ((b & 1) == 1)
                q = q * a;
            a = a * a;
            b = b >> 1;
        }
        return n ? 1 / q : q;
    }

    /**
     * #brief: 计算整数的阶乘
     *
     * <p>计算整数 x 的阶乘（x!），x 必须为非负整数。
     *
     * @param x 输入的整数
     * @return x 的阶乘
     */
    public static int fac(int x) {
        int q = 1;
        if (x >= 3)
            x = x + 1;
        for (int i = 2; i < x; i++)
            q = q * i;
        return q;
    }

    /**
     * #brief: 计算正弦值
     *
     * <p>使用泰勒级数近似计算浮点数的正弦值。
     *
     * @param a 输入的角度（弧度）
     * @return 正弦值
     */
    public static float sin(float a) {
        a = (float) (a % (2.0f * Math.PI));

        float q     = a;
        float t     = a;
        int   sign  = 1;
        int   limit = a < 1.0f ? 15 : 31;

        for (int n = 3; n <= limit; n += 2) {
            t *= (a * a) / (n * (n - 1));
            q += ((sign *= -1) * t);
        }

        return q;
    }

    /**
     * #brief: 计算余弦值
     *
     * <p>使用泰勒级数近似计算浮点数的余弦值。
     *
     * @param a 输入的角度（弧度）
     * @return 余弦值
     */
    public static float cos(float a) {
        a = (float) (a % (2.0f * Math.PI));

        float q     = 1.0f;
        float t     = 1.0f;
        int   sign  = 1;
        int   limit = a < 1.0f ? 16 : 32;

        for (int n = 2; n <= limit; n += 2) {
            t *= (a * a) / (n * (n - 1));
            q += (sign *= -1) * t;
        }

        return q;
    }

    /**
     * #brief: 弧度转角度
     *
     * <p>将弧度值转换为角度值。
     *
     * @param angrad 输入的弧度值
     * @return 转换后的角度值
     */
    public static int degrees(float angrad) {
        return round(angrad * (180.0f / (float) Math.PI));
    }

    /**
     * #brief: 角度转弧度
     *
     * <p>将角度值转换为弧度值。
     *
     * @param angdeg 输入的角度值
     * @return 转换后的弧度值
     */
    public static float radians(float angdeg) {
        return angdeg * ((float) Math.PI / 180.0f);
    }

    /**
     * #brief: 计算相对误差
     *
     * <p>计算两个浮点数之间的相对误差百分比。
     *
     * @param a 参考值
     * @param b 测量值
     * @return 相对误差百分比
     */
    public static float err(float a, float b) {
        return abs(round((abs(a - b) / a * 100.0f) * 100.0f) / 100.0f);
    }

    /**
     * #brief: 计算平方根
     *
     * <p>使用牛顿迭代法计算浮点数的平方根。
     *
     * @param a 输入的浮点数
     * @return 平方根值
     */
    public static float sqrt(float a) {
        if (a == 0)
            return a;

        int maxIter = 1000;

        float x = a;
        for (int i = 0; i < maxIter; i++) {
            float n = 0.5f * (x + a / x);

            if (abs(n - x) < (float) 1e-10) {
                return n;
            }

            x = n;
        }

        return x;
    }

    public static float len(Vector2f vector) {
        return sqrt((vector.x * vector.x) + (vector.y * vector.y));
    }

    public static void main(String[] args) {
        float a = radians(261);
        float v1 = 0.0f;
        float v2 = 0.0f;

        float sqrt_v = 25;

        System.out.printf("sqrt(%s) = %s\n", sqrt_v, sqrt(sqrt_v));
        System.out.println("radians = " + a);
        System.out.println("degrees = " + degrees(a));

        v1 = (float) Math.sin(a);
        v2 = NumGL.sin(a);
        System.out.printf("sin:\n    v1 = %s\n    v2 = %s\n    err = %s\n", v1, v2, err(v2, v1));

        v1 = (float) Math.cos(a);
        v2 = NumGL.cos(a);
        System.out.printf("cos:\n    v1 = %s\n    v2 = %s\n    err = %s\n", v1, v2, err(v2, v1));

        System.out.printf("(Vector2f length) = %s\n", len(new Vector2f(-3.0f, -4.0f)));
    }

}
