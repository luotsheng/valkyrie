package com.changhong.openvdb.app.ui.pane;

import com.changhong.collection.Lists;
import com.changhong.openvdb.driver.api.Column;
import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;

import java.util.*;

/**
 * @author Luo Tiansheng
 * @since 2026/4/10
 */
public class TableStructureDesigner extends Designer<Column>
{
        private final Set<Column> alterBuffer = new HashSet<>();
        private final Set<Column> primaryBuffer = new LinkedHashSet<>();

        private boolean primaryChange = false;

        public TableStructureDesigner(Session session, Driver driver, Table table, String name)
        {
                super(session, driver, table, name);
        }

        @Override
        public void onReload(Collection<Column> values)
        {
                primaryBuffer.clear();
                alterBuffer.clear();
                primaryChange = false;

                /* 重新加载页面时将原有主键保存起来 */
                for (Column columnMetaData : values) {
                        if (columnMetaData.isPrimary())
                                primaryBuffer.add(columnMetaData);
                }
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

                        primaryChange = true;

                        return;
                }

                if (newVal.isIntegrityValid()) {
                        alterBuffer.remove(newVal);
                        return;
                }

                /* 变更记录 */
                alterBuffer.add(newVal);
        }

        @Override
        public void onSave()
        {
                List<Column> autoIncrements = Lists.newArrayList();

                /* [Step 1]: 如果存在自增列，先移除 */
                for (Column column : alterBuffer) {
                        if (column.isAutoIncrement()) {
                                column.setAutoIncrement(false);
                                autoIncrements.add(column);
                        }
                }

                /* [Step 2]: 执行字段新增或修改操作 */
                if (!alterBuffer.isEmpty())
                        driver.alterChange(session, table, alterBuffer);

                /* [Step 3]: 设置表主键字段 */
                if (primaryChange)
                        driver.alterPrimaryKey(session, table, primaryBuffer);

                /* [Step 4]: 恢复自增列 */
                if (!autoIncrements.isEmpty()) {
                        autoIncrements.forEach(e -> e.setAutoIncrement(true));
                        driver.alterChange(session, table, autoIncrements);
                }
        }

        @Override
        public void onPlus(Column newObject)
        {
                /* DO NOTHING... */
        }

        @Override
        public void onMinus(Collection<Column> selectionItems)
        {
                driver.dropColumns(session, table, selectionItems);
        }
}
