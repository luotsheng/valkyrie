package com.changhong.openvdb.driver.redis;

import com.changhong.openvdb.driver.api.Column;
import com.changhong.openvdb.driver.api.DbType;
import com.changhong.openvdb.driver.api.Dialect;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Index;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;
import com.changhong.utils.collection.Lists;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Redis 驱动层实现
 *
 * @author Luo Tiansheng
 * @since 2026/4/20
 */
public class RedisDriver extends Driver {

        private final Jedis jedis;

        /**
         * 构造一个新的驱动实例。
         *
         * @param dataSource 数据源，用于获取数据库连接（不能为 {@code null}）
         * @throws NullPointerException 如果 {@code dataSource} 为 {@code null}
         */
        public RedisDriver(DataSource dataSource) {
                super(dataSource);
                this.jedis = ((RedisDataSource) dataSource).getJedis();
        }

        @Override
        public List<String> getCatalogs() {
                List<String> catalogs = Lists.newArrayList();
                int count = Integer.parseInt(jedis.configGet("databases").get("databases"));

                for (int i = 0; i < count; i++)
                        catalogs.add(String.valueOf(i));

                return catalogs;
        }

        @Override
        public DbType getType()
        {
                return null;
        }

        @Override
        protected Dialect createDialect()
        {
                return null;
        }

        @Override
        public String showCreateTable(Session session, String table)
        {
                return "";
        }

        @Override
        public List<Table> getTables(Session session)
        {
                return List.of();
        }

        @Override
        public List<Index> getIndexes(Session session, String table)
        {
                return List.of();
        }

        @Override
        public Set<String> getIndexTypes()
        {
                return Set.of();
        }

        @Override
        public void dropTable(Session session, String table)
        {

        }

        @Override
        public void dropColumns(Session session, String table, Collection<Column> columns)
        {

        }

        @Override
        public void dropIndexKeys(Session session, String table, Collection<Index> selectionItems)
        {

        }

        @Override
        public void dropPrimaryKey(Session session, String table)
        {

        }

        @Override
        public void addPrimaryKey(Session session, String table, Collection<Column> primaryKeys)
        {

        }

        @Override
        public void alterIndexKeys(Session session, String table, Collection<Index> indexes)
        {

        }

        @Override
        public void alterChange(Session session, String table, Collection<Column> columns)
        {

        }

        @Override
        public void alterVisible(Session session, String table, Collection<Index> indexes)
        {

        }
}
