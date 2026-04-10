package com.changhong.opendb.app.ui.pane;

import com.changhong.opendb.app.driver.TableIndexMetaData;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.driver.executor.SQLExecutor;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Luo Tiansheng
 * @since 2026/4/10
 */
public class MySQLIndexStructureDesigner extends Designer<TableIndexMetaData>
{
        private final Set<TableIndexMetaData> alterBuffer = new LinkedHashSet<>();
        private final Set<TableIndexMetaData> visibleBuffer = new LinkedHashSet<>();

        public MySQLIndexStructureDesigner(TableMetaData tableMetaData, SQLExecutor executor, String name)
        {
                super(tableMetaData, executor, name);
        }

        @Override
        public void onReload(Collection<TableIndexMetaData> values)
        {
                alterBuffer.clear();
                visibleBuffer.clear();
        }

        @Override
        public void onCommitEdit(TableIndexMetaData oldVal, TableIndexMetaData newVal)
        {
                addAlterBuffer(newVal);
        }

        @Override
        public void applySave()
        {
                if (!alterBuffer.isEmpty()) {
                        executor.dropIndexKeys(tableMetaData, alterBuffer);
                        executor.alterIndexKeys(tableMetaData, alterBuffer);
                }

                if (!visibleBuffer.isEmpty())
                        executor.alterVisible(tableMetaData, visibleBuffer);
        }

        @Override
        public void applyPlus(TableIndexMetaData newObject)
        {
                newObject.setType("NORMAL");
                addAlterBuffer(newObject);
        }

        @Override
        public void applyMinus(Collection<TableIndexMetaData> selectionItems)
        {
                executor.dropIndexKeys(tableMetaData, selectionItems);
        }

        private void addAlterBuffer(TableIndexMetaData index)
        {
                if (index.isVisible() != index.isOriginVisible()) {
                        visibleBuffer.add(index);
                        return;
                }

                if (index.isIntegrityValid()) {
                        alterBuffer.remove(index);
                        return;
                }

                alterBuffer.add(index);
        }
}
