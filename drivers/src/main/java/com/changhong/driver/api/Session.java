package com.changhong.driver.api;

/**
 * 数据库会话上下文记录。
 * <p>
 * 封装当前数据库连接的会话级别作用域信息，包括当前目录（catalog）和当前模式（schema）。
 * 该类用于在调用 JDBC 操作时传递上下文，以便在获取连接后正确设置
 * {@link java.sql.Connection#setCatalog(String)} 和 {@link java.sql.Connection#setSchema(String)}。
 * <p>
 * 该 record 为不可变（immutable）数据类型，可直接用于表示一次数据库交互的作用域范围。
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * Session session = new Session("my_catalog", "my_schema");
 * try (Connection conn = dataSource.getConnection()) {
 *     if (session.catalog() != null) conn.setCatalog(session.catalog());
 *     if (session.schema() != null) conn.setSchema(session.schema());
 *     // 执行 SQL...
 * }
 * }</pre>
 *
 * @param catalog 当前目录名称，可为 {@code null}（表示不设置或使用默认目录）
 * @param schema  当前模式名称，可为 {@code null}（表示不设置或使用默认模式）
 * @author Luo Tiansheng
 * @since 2026/4/11
 * @see java.sql.Connection#setCatalog(String)
 * @see java.sql.Connection#setSchema(String)
 */
public record Session(String catalog, String schema)
{
        public Session(String catalog)
        {
                this(catalog, null);
        }
}
