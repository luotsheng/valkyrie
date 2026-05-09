package valkyrie.driver.suggestion;

import lombok.Getter;

/**
 * Monaco 代码补全项
 *
 * @author Luo Tiansheng
 * @since 2026/5/9
 */
@Getter
public class Suggestion
{
        private final String label;
        private final String kind;
        private final String insertText;
        private final String detail;

        private Suggestion(String label, String kind)
        {
                this(label, label, kind);
        }

        private Suggestion(String label, String insertText, String kind)
        {
                this.label = label;
                this.kind = kind;
                this.insertText = insertText;
                this.detail = getDetail(kind);
        }

        public static Suggestion ofKeyword(String label)
        {
                return new Suggestion(label, "Keyword");
        }

        public static Suggestion ofFunction(String label)
        {
                return new Suggestion(label, "Function");
        }

        public static Suggestion ofOperator(String label)
        {
                return new Suggestion(label, "Operator");
        }

        public static Suggestion ofClass(String label)
        {
                return new Suggestion(label, "Class");
        }

        public static Suggestion ofField(String label)
        {
                return new Suggestion(label, "Field");
        }

        public static Suggestion ofModule(String label)
        {
                return new Suggestion(label, "Module");
        }

        public static Suggestion ofSnippet(String label, String insertText)
        {
                return new Suggestion(label, insertText, "Snippet");
        }

        private static String getDetail(String kind)
        {
                return switch (kind) {
                        case "Keyword" -> "关键字";
                        case "Function" -> "函数";
                        case "Operator" -> "操作符";
                        case "Class" -> "表";
                        case "Field" -> "字段";
                        case "Module" -> "模块";
                        case "Snippet" -> "代码片段";
                        default -> throw new UnsupportedOperationException();
                };
        }

}