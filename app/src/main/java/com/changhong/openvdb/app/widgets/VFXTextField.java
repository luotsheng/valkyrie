package com.changhong.openvdb.app.widgets;

import javafx.application.Platform;
import javafx.scene.control.TextField;

/**
 * @author Luo Tiansheng
 * @since 2026/4/15
 */
public class VFXTextField extends TextField
{
        public VFXTextField()
        {
                super();
        }

        public VFXTextField(String text)
        {
                super(text);
        }

        @Override
        public void requestFocus()
        {
                if (!isFocused())
                       Platform.runLater(super::requestFocus);
        }
}
