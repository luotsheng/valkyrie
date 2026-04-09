package com.changhong.opendb.app.driver.executor;

import com.changhong.exception.SystemRuntimeException;
import com.changhong.opendb.app.driver.*;
import com.changhong.opendb.app.driver.datasource.VirtualDataSource;
import com.changhong.opendb.app.driver.sql.SQL;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        @Getter
        private final String connectionName;

        @Getter
        protected final ProductMetaData productMetaData;

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
         * @param connectionName 连接名称
         * @param ds             数据源
         */
        public SQLExecutor(String connectionName, VirtualDataSource ds)
        {
                this.connectionName = connectionName;
                this.ds = ds;

                try (var conn = ds.getConnection()) {
                        DatabaseMetaData db = conn.getMetaData();
                        productMetaData = new ProductMetaData();
                        productMetaData.setProductName(db.getDatabaseProductName());
                        productMetaData.setVersion(db.getDatabaseProductVersion());
                        productMetaData.setMajorVersion(db.getDatabaseMajorVersion());
                        productMetaData.setMinorVersion(db.getDatabaseMinorVersion());
                } catch (Exception e) {
                        LOG.error("Initialize executor error", e);
                        throw new SystemRuntimeException(e);
                }

        }

        public abstract List<String> getSchemas();

        public abstract List<String> getDatabases();

        public abstract List<TableMetaData> getTables(String db);

        public abstract List<ColumnMetaData> getColumns(TableMetaData table);

        public abstract List<TableIndexMetaData> getIndexes(TableMetaData table);

        public abstract String showCreateTable(String db, String table);

        public abstract void dropTable(String db, String table) throws SQLException;

        public abstract void alterPrimaryKey(TableMetaData tableMetaData, Collection<ColumnMetaData> primaryKeys);

        public abstract void alterChange(TableMetaData tableMetaData, Collection<ColumnMetaData> columnMetaDatas);

        public MutableDataGrid execute(SQL sql) {
              return execute(sql, new DefaultExecutorCallback());
        }

        public abstract MutableDataGrid execute(SQL sql, ExecuteCallback callback);

        public abstract MutableDataGrid selectByPage(TableMetaData table, int start, int size)
                throws SQLException;

        public abstract void cancel(Long id);
}
