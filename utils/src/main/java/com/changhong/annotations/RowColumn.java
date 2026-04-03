package com.changhong.annotations;

import com.changhong.time.DateFormatter;

import java.lang.annotation.*;

/**
 * 自定义注解 `RowColumn`，用于标注字段在 Excel 中的列标题及其格式化模式。
 *
 * <p>该注解可应用于类的字段上，用于指定字段对应的 Excel 列名以及格式化模式。
 * 若字段是日期类型，可以通过 `pattern` 属性指定日期格式，默认为 "yyyy/MM/dd HH:mm:ss" 格式。<p>
 *
 * <h2>功能特点</h2>
 * <ul>
 *     <li>`name` 属性指定该字段在 Excel 中对应的列名。</li>
 *     <li>`pattern` 属性允许自定义日期字段的格式，默认为 "yyyy/MM/dd HH:mm:ss"。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     public class User {
 *         \@RowColumn(name = "用户名", pattern = "yyyy-MM-dd")
 *         private Date birthDate;
 *     }
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowColumn {

    /**
     * Excel 列标题名称
     *
     * @return 列名
     */
    String name();

    /**
     * 日期字段的格式化模式，默认为 "yyyy/MM/dd HH:mm:ss"。
     *
     * @return 日期格式
     */
    String pattern() default DateFormatter.SLASH_PATTERN_Y4H2M2D2H2M2S2;

}