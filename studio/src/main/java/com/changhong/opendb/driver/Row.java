package com.changhong.opendb.driver;

import java.util.ArrayList;

/**
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
public class Row extends ArrayList<String>
{
        public Row()
        {
                this(0);
        }

        public Row(int initCapacity)
        {
                for (int i = 0; i < initCapacity; i++)
                        add(null);
        }
}
