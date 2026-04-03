package com.changhong.opendb.driver.executor;

import com.changhong.opendb.driver.ColumnMetaData;
import com.changhong.opendb.driver.MutableDataGrid;
import com.changhong.opendb.driver.SQL;
import com.changhong.opendb.driver.TableMetaData;
import com.changhong.opendb.driver.datasource.VirtualDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL 执行器
 *
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({
        "SqlDialectInspection",
        "SqlNoDataSourceInspection",
        "UnusedReturnValue"
})
public abstract class SQLExecutor
{
        private static final Logger LOG = LoggerFactory.getLogger(SQLExecutor.class);

        private final String name;
        protected final VirtualDataSource ds;

        /**
         * 任务队列
         */
        protected final Map<Long, Statement> queue = new ConcurrentHashMap<>();

        public interface ExecuteCallback {
                void doCallback(String info, SQLExecutorStatus status);
        }

        /**
         * 创建 Jdbc 目录
         *
         * @param name 连接名称
         * @param ds   数据源
         */
        public SQLExecutor(String name, VirtualDataSource ds)
        {
                this.name = name;
                this.ds = ds;
        }

        /**
         * @return 返回 Jdbc 连接名称
         */
        public String name()
        {
                return name;
        }

        public abstract List<String> databases();

        public abstract List<TableMetaData> tables(String db);

        public abstract List<ColumnMetaData> getColumns(TableMetaData table);

        public abstract void drop(String db, String name) throws SQLException;

        public MutableDataGrid execute(SQL sql) {
              return execute(sql, new DefaultExecutorCallback());
        }

        public abstract MutableDataGrid execute(SQL sql, ExecuteCallback callback);

        public abstract MutableDataGrid select(TableMetaData table, int start, int size)
                throws SQLException;

        public abstract void cancel(Long id);
}
