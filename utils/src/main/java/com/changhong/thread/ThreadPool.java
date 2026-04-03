package com.changhong.thread;

import com.changhong.system.SystemUtils;

import java.util.concurrent.*;

/**
 * `ThreadPool` 类提供了固定大小和调度的线程池，方便任务的提交和调度执行。
 *
 * <p>该类包含以下特点：
 * <ul>
 *     <li>根据可用处理器数量初始化固定线程池和调度线程池，以优化资源使用。</li>
 *     <li>提供方法提交 `Runnable` 和 `Callable` 任务到固定线程池。</li>
 *     <li>支持调度任务的执行，可以在指定时间后运行任务。</li>
 * </ul>
 *
 * <p>该类的设计旨在简化多线程编程，避免直接使用线程管理，提升代码的可读性和可维护性。
 *
 * <h2>使用示例</h2>
 * <pre>
 *     ThreadPool.taskSubmit(() -> {
 *         // 任务代码
 *     });
 *
 *     ThreadPool.schedule(() -> {
 *         // 调度任务代码
 *     }, 10, TimeUnitOperator.SECONDS);
 * </pre>
 *
 * @author Luo Tiansheng
 * @see ExecutorService
 * @see ScheduledExecutorService
 * @since 1.0
 */
public class ThreadPool {

    /**
     * 当前可用的处理器数量
     */
    public static final int N_THREAD = SystemUtils.availableProcessors();

    /**
     * 固定线程池，用于执行提交的任务
     */
    private static final ExecutorService fixedThreadPool =
            Executors.newFixedThreadPool(N_THREAD);

    /**
     * 调度线程池，用于定时执行任务
     */
    private static final ScheduledExecutorService scheduledThreadPool =
            Executors.newScheduledThreadPool(N_THREAD);

    /**
     * #brief: 提交一个可运行的任务到固定线程池
     *
     * <p>该方法将给定的 `Runnable` 任务提交到固定线程池，以便进行并发执行。
     *
     * @param runnable 要执行的任务
     */
    public static void taskSubmit(Runnable runnable) {
        fixedThreadPool.submit(runnable);
    }

    /**
     * #brief: 提交一个可调用的任务到固定线程池
     *
     * <p>该方法将给定的 `Callable` 任务提交到固定线程池，并返回一个 `Future` 对象以获取任务结果。
     *
     * @param callable 要执行的任务
     * @param <V> 任务返回值的类型
     * @return 任务的 `Future` 对象
     */
    public static <V> Future<V> taskSubmit(Callable<V> callable) {
        return fixedThreadPool.submit(callable);
    }

    /**
     * #brief: 在指定时间后调度可运行任务的执行
     *
     * <p>该方法将给定的 `Runnable` 任务调度到指定的时间后执行。
     *
     * @param runnable 要调度的任务
     * @param t 延迟时间
     * @param timeUnit 时间单位
     */
    public static void schedule(Runnable runnable, long t, TimeUnit timeUnit) {
        scheduledThreadPool.schedule(runnable, t, timeUnit);
    }

    /**
     * #brief: 在指定时间后调度可调用任务的执行
     *
     * <p>该方法将给定的 `Callable` 任务调度到指定的时间后执行，并返回一个 `Future` 对象以获取任务结果。
     *
     * @param callable 要调度的任务
     * @param t 延迟时间
     * @param timeUnit 时间单位
     * @param <V> 任务返回值的类型
     * @return 任务的 `Future` 对象
     */
    public static <V> Future<V> schedule(Callable<V> callable, long t, TimeUnit timeUnit) {
        return scheduledThreadPool.schedule(callable, t, timeUnit);
    }

}
