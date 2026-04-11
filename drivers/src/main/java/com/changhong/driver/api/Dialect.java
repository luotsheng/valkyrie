package com.changhong.driver.api;

/**
 * 数据库方言接口。
 * <p>
 * 用于封装特定数据库的 SQL 语法差异，为上层的 SQL 构建和执行提供统一适配。
 * 不同数据库（MySQL、PostgreSQL、Oracle 等）应提供各自的 {@code Dialect} 实现，
 * 以生成符合该数据库语法的 SQL 语句。
 * <p>
 * <b>职责范围：</b>
 * <ul>
 *   <li>生成数据库特定的 DDL 语句（如 {@code SHOW CREATE TABLE}）</li>
 *   <li>生成分页查询的 {@code LIMIT} 或 {@code ROWNUM} 子句</li>
 *   <li>转义标识符（表名、列名等）以防止 SQL 注入或处理保留字</li>
 * </ul>
 * <p>
 * <b>实现注意事项：</b>
 * <ul>
 *   <li>实现类应为无状态（stateless）且线程安全</li>
 *   <li>每个数据库产品应有唯一对应的方言实现</li>
 * </ul>
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public interface Dialect
{
        /**
         * 生成数据库特定的分页子句。
         * <p>
         * 该方法接收原始 SQL 查询语句，并返回包装后的带分页限制的 SQL。
         * 不同数据库的分页实现方式不同：
         * <ul>
         *   <li>MySQL / PostgreSQL: {@code sql LIMIT size OFFSET off}</li>
         *   <li>SQL Server (老版本): 使用 {@code ROW_NUMBER()} 子查询</li>
         *   <li>Oracle (12c 之前): 使用 {@code ROWNUM} 嵌套查询</li>
         *   <li>Oracle (12c+): 使用 {@code OFFSET off ROWS FETCH NEXT size ROWS ONLY}</li>
         * </ul>
         *
         * @param sql  原始 SQL 查询语句（不能为 {@code null} 或空白）
         * @param off  起始偏移量（从 0 开始），表示跳过前 {@code off} 条记录
         * @param size 返回的最大记录条数（必须大于 0）
         * @return 添加分页子句后的完整 SQL 语句
         * @throws IllegalArgumentException 如果 {@code off < 0} 或 {@code size <= 0}，或原始 SQL 无效
         * @throws NullPointerException 如果 {@code sql} 为 {@code null}
         */
        String limit(String sql, int off, int size);

        /**
         * 转义数据库标识符（如表名、列名）。
         * <p>
         * 根据数据库的引号规则，将标识符包裹在合适的引号中，以处理：
         * <ul>
         *   <li>包含特殊字符（如空格、连字符）的标识符</li>
         *   <li>与数据库保留字冲突的标识符</li>
         *   <li>大小写敏感或大小写折叠的需求</li>
         * </ul>
         * <b>示例：</b>
         * <ul>
         *   <li>MySQL: {@code quote("order")} 返回 {@code `order`}</li>
         *   <li>PostgreSQL / ANSI SQL: {@code quote("order")} 返回 {@code "order"}</li>
         *   <li>SQL Server: {@code quote("order")} 返回 {@code [order]}</li>
         * </ul>
         *
         * @param identifier 原始标识符（不能为 {@code null} 或空白）
         * @return 转义后的标识符，可直接拼接在 SQL 语句中使用
         * @throws IllegalArgumentException 如果 {@code identifier} 为 {@code null} 或仅含空白字符
         */
        String quote(String identifier);
}
