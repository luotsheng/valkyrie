package com.changhong.driver.exception;

import java.sql.SQLException;

/**
 * SQL 运行时异常
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public class SQLRuntimeException extends RuntimeException
{
        public SQLRuntimeException(SQLException exception)
        {
                super(exception);
        }
}
