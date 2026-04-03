package com.changhong.opendb.app.ui.workbench;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

/**
 * 被修改的单元格
 *
 * @author Luo Tiansheng
 * @since 2026/4/3
 */
@Data
@AllArgsConstructor
public class ModifyCell
{
        /**
         * 列索引
         */
        private int columnIndex;

        /**
         * 行索引
         */
        private int rowIndex;

        /**
         * 旧值
         */
        private String oldValue;

        /**
         * 新值
         */
        private String newValue;

        public boolean isUnmodified()
        {
                return Objects.equals(oldValue, newValue);
        }

}
