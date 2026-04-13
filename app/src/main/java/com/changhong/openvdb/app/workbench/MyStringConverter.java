package com.changhong.openvdb.app.workbench;

import javafx.util.StringConverter;

public class MyStringConverter extends StringConverter<String>
{
        public MyStringConverter() {
        }

        public String toString(String var1) {
                return var1 != null ? var1 : "";
        }

        public String fromString(String var1) {
                return var1;
        }
}
