package com.changhong.driver.api;

/**
 * 数据库表元数据记录。
 * <p>
 * 该 record 表示数据库中的一张基本表（TABLE）的摘要信息，包含表名、存储引擎、估算大小、
 * 行数、注释等元数据。所有字段均为不可变（immutable）且可直接通过访问器方法获取。
 * <p>
 * 典型使用场景：
 * <ul>
 *   <li>从 {@link java.sql.DatabaseMetaData#getTables} 结果集中提取表基本信息</li>
 *   <li>作为数据字典或 Schema 浏览模块的返回值类型</li>
 *   <li>在数据库管理工具中展示表的统计信息</li>
 * </ul>
 *
 * @param name       表名称（通常与 {@code TABLE_NAME} 列对应），不可为 {@code null}
 * @param createTime 表的创建时间，格式依赖于具体数据库实现（如 MySQL 的 {@code CREATE_TIME}），
 *                   可为 {@code null} 如果数据库不记录或无法获取
 * @param updateTime 表的最后修改时间，格式依赖于具体数据库实现（如 MySQL 的 {@code UPDATE_TIME}），
 *                   可为 {@code null} 如果数据库不记录或无法获取
 * @param engine     存储引擎名称（如 InnoDB、MyISAM），可为 {@code null} 如果数据库不支持存储引擎概念
 * @param size       表占用的存储空间大小（单位：MB），可为 {@code null} 如果无法计算
 * @param rows       表中的估算行数（非精确值，通常来自统计信息），可为 {@code null} 如果数据库不支持行数估算
 * @param comment    表的注释/说明文本，可为 {@code null} 或空字符串
 *
 * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public record Table(
        String name,
        String createTime,
        String updateTime,
        String engine,
        Float size,
        Integer rows,
        String comment) { /* DO NOTHING... */ }
