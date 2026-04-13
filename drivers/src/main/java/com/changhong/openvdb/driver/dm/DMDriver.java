package com.changhong.openvdb.driver.dm;

import com.changhong.openvdb.driver.api.*;
import com.changhong.openvdb.driver.api.sql.SQL;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Luo Tiansheng
 * @since 2026/4/13
 */
public class DMDriver extends Driver
{
        public DMDriver(DataSource dataSource)
        {
                super(dataSource);
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
        public List<Column> getColumns(Session session, String table)
        {
                return List.of();
        }

        @Override
        public List<Index> getIndexes(Session session, Table table)
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
        public void alterPrimaryKey(Session session, String table, Collection<Column> primaryKeys)
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

        @Override
        public DataGrid selectByPage(Session session, String table, int off, int size)
        {
                return null;
        }

        @Override
        public DataGrid execute(long jobId, Session session, SQL sql)
        {
                return null;
        }

        @Override
        public void cancel(long jobId)
        {

        }
}
