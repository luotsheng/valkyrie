package com.changhong.openvdb.app.pane;

import com.changhong.openvdb.driver.api.Driver;
import com.changhong.openvdb.driver.api.Index;
import com.changhong.openvdb.driver.api.Session;
import com.changhong.openvdb.driver.api.Table;
import javafx.scene.control.Tab;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 表设计接口
 * <p>
 * 该接口主要针对不同按钮实现，避免所有面板都写在一个类中，导致
 * 耦合度较高的情况。
 *
 * @author Luo Tiansheng
 * @since 2026/3/27
 */
@Getter
public abstract class Designer<T>
{
        private final Tab tab;

        protected final Session session;
        protected final Driver driver;
        protected final Table table;

        private final Map<Integer, Index> tableIndexMetaDataUpdateBuffer = new HashMap<>();

        public Designer(Session session, Driver driver, Table table, String name)
        {
                this.session = session;
                this.driver = driver;
                this.table = table;
                this.tab = new Tab(name);
        }

        /**
         * 当页面刷新时调用
         */
        public abstract void onReload(Collection<T> values);

        /**
         * 编辑事件回调
         */
        public abstract void onCommitEdit(T oldVal, T newVal);

        /**
         * 当用户点击保存按钮
         */
        public abstract void onSave();

        /**
         * 当用户点击加号按钮
         */
        public abstract void onPlus(T newObject);

        /**
         * 当用户点击减号按钮
         */
        public abstract void onMinus(Collection<T> selectionItems);
}
