package com.changhong.driver.api.sql;

import com.changhong.driver.api.DataGrid;
import com.changhong.driver.api.Session;

/**
 * SQL 执行器（统一入口）
 *
 * 用于执行所有类型 SQL 语句，并返回执行结果。
 * 仅负责 SQL 执行过程，不负责 SQL 解析、生成及方言处理。
 *
 * 执行行为说明：
 * - jobId 用于标识一次独立执行任务（由调用方生成）
 * - SQL 为待执行的原始语句
 * - 执行过程由具体数据库驱动实现
 *
 * 返回规则：
 * - 查询类语句（SELECT / SHOW / DESCRIBE 等）返回 DataGrid 结果集
 * - 非查询语句（INSERT / UPDATE / DELETE / DDL 等）返回 null
 *
 * 取消行为：
 * - cancel(jobId) 用于终止对应执行任务
 * - 若任务已结束或不存在，则忽略或返回失败（实现决定）
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public interface SQLExecutor
{
        /**
         * 执行 SQL 任务
         *
         * 执行由 jobId 标识的 SQL 任务，并返回查询结果。
         *
         * 执行规则：
         * - jobId 由调用方提供，用于标识一次独立执行任务
         * - SQL 为待执行的原始语句
         * - 执行过程由具体数据库驱动实现
         *
         * 返回规则：
         * - 查询语句（SELECT / SHOW / DESCRIBE 等）返回 DataGrid
         * - 非查询语句（INSERT / UPDATE / DELETE / DDL 等）返回 null
         *
         * 注意：
         * - 非查询语句的执行结果需通过执行状态或影响行数获取（实现层提供）
         * - 该方法可能为异步执行，结果返回不代表任务已完成（视实现而定）
         *
         * @param jobId 执行任务唯一标识（由调用方生成）
         * @param sql 待执行 SQL 任务
         * @return 查询结果集（非查询语句返回 null）
         */
        DataGrid execute(long jobId, Session session, SQL sql);

        /**
         * 取消 SQL 执行任务
         *
         * 根据 jobId 取消正在执行的 SQL 任务。
         *
         * 行为说明：
         * - 若任务正在执行，则尝试中断执行
         * - 若任务已完成或不存在，则忽略或失败（由实现决定）
         *
         * @param jobId 执行任务唯一标识
         */
        void cancel(long jobId);
}