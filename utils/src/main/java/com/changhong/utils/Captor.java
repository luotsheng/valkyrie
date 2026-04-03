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

import com.changhong.iface.ActionFunction;
import com.changhong.iface.CallableFunction;
import com.changhong.exception.AssertException;
import com.changhong.exception.SystemRuntimeException;

/**
 * @author Luo Tiansheng
 */
public class Captor {

    /**
     * 执行指定的无返回值函数，如果发生任何异常则忽略并不处理。
     *
     * <p>该方法可用于执行可能会抛出异常的操作，但不需要关心异常的情况。
     *
     * @param function 要执行的函数
     */
    public static void icall(ActionFunction function) {
        try {
            function.call();
        } catch (Throwable e) {
            /* do nothing... */
        }
    }

    /**
     * #brief: 执行带返回值的回调函数，并在出现异常时返回 `null`
     *
     * <p>该方法执行传入的 `RetFunction` 接口的回调函数，并捕获任何异常。如果函数调用成功，
     * 则返回其结果。如果在调用过程中抛出异常，则捕获异常并返回 `null`。
     *
     * <p>方法主要用于在执行可能抛出异常的操作时，简化异常处理流程，使调用者无需手动处理异常。
     *
     * @param function 要执行的回调函数，该函数返回类型为 `T`
     * @param <T> 返回值的类型
     * @return 回调函数的返回值，若发生异常则返回 `null`
     */
    public static <T> T icall(CallableFunction<T> function) {
        try {
            return function.call();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 执行指定的无返回值函数，如果发生异常则抛出断言异常。
     *
     * <p>此方法用于执行可能抛出异常的操作，并在发生异常时提供详细的异常信息。
     *
     * @param function 要执行的函数
     * @throws AssertException 如果函数执行时发生异常
     */
    public static void call(ActionFunction function) {
        try {
            function.call();
        } catch (Throwable e) {
            throw new SystemRuntimeException(e.getMessage());
        }
    }

    /**
     * 执行指定的无返回值函数，如果发生异常则抛出断言异常，并使用自定义的异常信息。
     *
     * <p>此方法用于执行可能抛出异常的操作，并在发生异常时提供详细的异常信息和格式化支持。
     *
     * @param function 要执行的函数
     * @param fmt 自定义异常信息的格式
     * @param args 格式化参数
     * @throws AssertException 如果函数执行时发生异常
     */
    public static void call(ActionFunction function, String fmt, Object... args) {
        try {
            function.call();
        } catch (Throwable e) {
            throw new SystemRuntimeException(fmt, args);
        }
    }

    /**
     * 执行指定的有返回值函数，如果发生异常则抛出断言异常。
     *
     * <p>此方法用于执行可能抛出异常的操作，并在发生异常时提供详细的异常信息。
     *
     * @param function 要执行的函数
     * @param <T> 返回值的类型
     * @return 函数返回的值；如果发生异常，则抛出 {@link AssertException}
     * @throws AssertException 如果函数执行时发生异常
     */
    public static <T> T call(CallableFunction<T> function) {
        try {
            return function.call();
        } catch (Throwable e) {
            throw new SystemRuntimeException(e.getMessage());
        }
    }

    /**
     * 执行指定的有返回值函数，如果发生异常则抛出断言异常，并使用自定义的异常信息。
     *
     * <p>此方法用于执行可能抛出异常的操作，并在发生异常时提供详细的异常信息和格式化支持。
     *
     * @param function 要执行的函数
     * @param fmt 自定义异常信息的格式
     * @param args 格式化参数
     * @param <T> 返回值的类型
     * @return 函数返回的值；如果发生异常，则抛出 {@link AssertException}
     * @throws AssertException 如果函数执行时发生异常
     */
    public static <T> T call(CallableFunction<T> function, String fmt, Object... args) {
        try {
            return function.call();
        } catch (Throwable e) {
            throw new SystemRuntimeException(fmt, args);
        }
    }

}
