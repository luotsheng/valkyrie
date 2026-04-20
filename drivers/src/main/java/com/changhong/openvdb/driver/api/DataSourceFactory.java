package com.changhong.openvdb.driver.api;

import com.changhong.openvdb.driver.redis.RedisDataSource;

/**
 * @author Luo Tiansheng
 * @since 2026/4/20
 */
public class DataSourceFactory
{
        public static CloseableDataSource getDataSource(ConnectionConfig config)
        {
                return switch (config.getType()) {
                        case mysql, dm -> new PooledDataSource(config);
                        case redis -> new RedisDataSource(config);
                };
        }
}
