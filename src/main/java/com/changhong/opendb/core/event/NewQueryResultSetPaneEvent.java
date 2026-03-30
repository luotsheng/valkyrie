package com.changhong.opendb.core.event;

import com.changhong.opendb.driver.JdbcTemplate;
import com.changhong.opendb.driver.TableInfo;
import lombok.AllArgsConstructor;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
@AllArgsConstructor
public class NewQueryResultSetPaneEvent extends Event
{
        public JdbcTemplate jdbcTemplate;
        public String database;
        public TableInfo info;
}
