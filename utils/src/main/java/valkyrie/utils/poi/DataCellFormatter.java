package valkyrie.utils.poi;

/* -------------------------------------------------------------------------------- *\
|*                                                                                  *|
|*    Copyright (C) 2019-2024 Luo Tiansheng All rights reserved.                    *|
|*                                                                                  *|
|*    Licensed under the Apache License, Version 2.0 (the "License");               *|
|*    you may not use this file except in compliance with the License.              *|
|*    You may obtain a copy of the License at                                       *|
|*                                                                                  *|
|*        http://www.apache.org/licenses/LICENSE-2.0                                *|
|*                                                                                  *|
|*    Unless required by applicable law or agreed to in writing, software           *|
|*    distributed under the License is distributed on an "AS IS" BASIS,             *|
|*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.      *|
|*    See the License for the specific language governing permissions and           *|
|*    limitations under the License.                                                *|
|*                                                                                  *|
\* -------------------------------------------------------------------------------- */

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import valkyrie.utils.time.DateFormatter;

/**
 * `DataCellFormatter` 是一个用于格式化 Excel 单元格数据的工具类。
 *
 * <p>该类提供了基于 Apache POI 的方法，用于将单元格内容格式化为字符串形式。
 * 根据单元格类型，该类能够处理日期格式化、数值格式化以及其他通用单元格类型。<p>
 *
 * <h2>功能特点</h2>
 * <ul>
 *     <li>支持空单元格处理，返回 "NULL" 以标识空值。</li>
 *     <li>自动判断单元格类型，针对日期类型的单元格进行日期格式化。</li>
 *     <li>对于其他类型的单元格，使用 {@link DataFormatter} 进行通用格式化。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     Cell cell = sheet.getRow(0).getCell(0);
 *     CellDataFormatter formatter = new CellDataFormatter();
 *     String formattedValue = formatter.formatCellValue(cell);
 * </pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>需要确保 Apache POI 库已导入项目。</li>
 *     <li>日期格式默认为 "yyyy/MM/dd HH:mm:ss"，可根据需求调整。</li>
 * </ul>
 *
 * @author Luo Tiansheng
 * @see Cell
 * @see DataFormatter
 * @see DateUtil
 * @since 1.0
 */
public class DataCellFormatter {

    private final DataFormatter formatter = new DataFormatter();

    /**
     * 格式化单元格内容为字符串。
     *
     * <p>根据单元格类型判断处理逻辑：
     * <ul>
     *     <li>如果单元格为空，返回 "NULL"。</li>
     *     <li>如果单元格为日期类型，返回格式化后的日期字符串。</li>
     *     <li>对于其他类型，返回 {@link DataFormatter} 的格式化结果。</li>
     * </ul>
     *
     * @param cell 要格式化的单元格对象
     * @return 格式化后的单元格内容字符串
     */
    public String formatCellValue(Cell cell) {
        if (cell == null)
            return "NULL";

        CellType type = cell.getCellType();
        if (type == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell))
            return DateFormatter.format(cell.getDateCellValue(), "yyyy/MM/dd HH:mm:ss");

        return formatter.formatCellValue(cell);
    }

}

