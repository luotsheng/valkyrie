package com.changhong.opendb.app.ui.pane;

import com.changhong.opendb.app.driver.ColumnMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Luo Tiansheng
 * @since 2026/4/10
 */
public class MySQLTableStructureDesigner extends Designer<ColumnMetaData>
{
        private final Set<ColumnMetaData> updateBuffer = new HashSet<>();
        private final Set<ColumnMetaData> primaryBuffer = new LinkedHashSet<>();

        public MySQLTableStructureDesigner(TableMetaData tableMetaData, SQLExecutor executor, String name)
        {
                super(tableMetaData, executor, name);
        }

        @Override
        public void onReload(Collection<ColumnMetaData> values)
        {
                primaryBuffer.clear();
                updateBuffer.clear();

                for (ColumnMetaData columnMetaData : values)
                        if (columnMetaData.isPrimary())
                                primaryBuffer.add(columnMetaData);
        }

        @Override
        public void onCommitEdit(ColumnMetaData oldVal, ColumnMetaData newVal)
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

                /* 变更记录 */
                updateBuffer.add(newVal);
        }

        @Override
        public void applySave()
        {
                executor.alterChange(tableMetaData, updateBuffer);
                executor.alterPrimaryKey(tableMetaData, primaryBuffer);
        }

        @Override
        public void applyPlus(ColumnMetaData newObject)
        {
                /* DO NOTHING... */
        }

        @Override
        public void applyMinus(Collection<ColumnMetaData> selectionItems)
        {
                executor.deleteColumns(tableMetaData, selectionItems);
        }
}
