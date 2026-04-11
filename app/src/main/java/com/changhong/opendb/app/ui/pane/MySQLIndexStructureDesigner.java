package com.changhong.opendb.app.ui.pane;

import com.changhong.driver.api.Driver;
import com.changhong.driver.api.Index;
import com.changhong.driver.api.Session;
import com.changhong.driver.api.Table;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Luo Tiansheng
 * @since 2026/4/10
 */
public class MySQLIndexStructureDesigner extends Designer<Index>
{
        private final Set<Index> alterBuffer = new LinkedHashSet<>();
        private final Set<Index> visibleBuffer = new LinkedHashSet<>();

        public MySQLIndexStructureDesigner(Session session, Driver driver, Table table, String name)
        {
                super(session, driver, table, name);
        }

        @Override
        public void onReload(Collection<Index> values)
        {
                alterBuffer.clear();
                visibleBuffer.clear();
        }

        @Override
        public void onCommitEdit(Index oldVal, Index newVal)
        {
                addAlterBuffer(newVal);
        }

        @Override
        public void applySave()
        {
                if (!alterBuffer.isEmpty()) {
                        driver.dropIndexKeys(session, table, alterBuffer);
                        driver.alterIndexKeys(session, table, alterBuffer);
                }

                if (!visibleBuffer.isEmpty())
                        driver.alterVisible(session, table, visibleBuffer);
        }

        @Override
        public void applyPlus(Index newObject)
        {
                newObject.setType("NORMAL");
                addAlterBuffer(newObject);
        }

        @Override
        public void applyMinus(Collection<Index> selectionItems)
        {
                driver.dropIndexKeys(session, table, selectionItems);
        }

        private void addAlterBuffer(Index index)
        {
                if (index.isVisible() != index.isOriginalVisible()) {
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
