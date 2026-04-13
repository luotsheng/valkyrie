package com.changhong.openvdb.app.exception;

import com.changhong.openvdb.app.widgets.dialog.VFXDialogHelper;

/**
 * @author Luo Tiansheng
 * @since 2026/4/13
 */
public class ApplicationException extends RuntimeException
{

        public ApplicationException(String message)
        {
                super(message);
                VFXDialogHelper.alert(message);
        }
}
