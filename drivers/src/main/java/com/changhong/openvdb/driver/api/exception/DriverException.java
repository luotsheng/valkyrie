package com.changhong.openvdb.driver.api.exception;

import com.changhong.exception.Causes;
import lombok.Getter;

import java.sql.SQLException;

/**
 * 驱动运行时异常
 *
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
@Getter
public class DriverException extends RuntimeException
{
        private int errorCode;

        public DriverException(Throwable cause)
        {
                super(Causes.original(cause));

                if (getCause() instanceof SQLException sql)
                        errorCode = sql.getErrorCode();
        }
}
