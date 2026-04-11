package com.changhong.opendb.app.ui.pane;

import com.changhong.driver.api.Column;
import com.changhong.driver.api.Driver;
import com.changhong.driver.api.Session;
import com.changhong.driver.api.Table;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Luo Tiansheng
 * @since 2026/4/10
 */
public class MySQLTableStructureDesigner extends Designer<Column>
{
        private final Set<Column> updateBuffer = new HashSet<>();
        private final Set<Column> primaryBuffer = new LinkedHashSet<>();

        public MySQLTableStructureDesigner(Session session, Driver driver, Table table, String name)
        {
                super(session, driver, table, name);
        }

        @Override
        public void onReload(Collection<Column> values)
        {
                primaryBuffer.clear();
                updateBuffer.clear();

                for (Column columnMetaData : values)
                        if (columnMetaData.isPrimary())
                                primaryBuffer.add(columnMetaData);
        }

        @Override
        public void onCommitEdit(Column oldVal, Column newVal)
        {
                /* 检测到主键变动 */
                if (oldVal.isPrimary() != newVal.isPrimary()) {
                        if (newVal.isPrimary()) {
                                primaryBuffer.add(newVal);
                        } else {
                                primaryBuffer.remove(newVal);
                        }

                        return;
                }

                if (newVal.isIntegrityValid()) {
                        updateBuffer.remove(newVal);
                        return;
                }

                /* 变更记录 */
                updateBuffer.add(newVal);
        }

        @Override
        public void applySave()
        {
                driver.alterChange(session, table, updateBuffer);
                driver.alterPrimaryKey(session, table, primaryBuffer);
        }

        @Override
        public void applyPlus(Column newObject)
        {
                /* DO NOTHING... */
        }

        @Override
        public void applyMinus(Collection<Column> selectionItems)
        {
                driver.dropColumns(session, table, selectionItems);
        }
}
