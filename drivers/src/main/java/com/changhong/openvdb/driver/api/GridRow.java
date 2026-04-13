package com.changhong.openvdb.driver.api;

import java.util.ArrayList;

/**
 * @author Luo Tiansheng
 * @since 2026/4/11
 */
public class GridRow extends ArrayList<String>
{
        public GridRow()
        {
                this(0);
        }

        public GridRow(int initCapacity)
        {
                for (int i = 0; i < initCapacity; i++)
                        add(null);
        }
}
