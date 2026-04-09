package com.changhong.opendb.app.ui.widgets.dialog;

import com.changhong.opendb.app.utils.Causes;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class VFXDialog
{
        public interface DialogCallback {
                void apply() throws Throwable;
        }

        public static void tryCall(DialogCallback callback)
        {
                try {
                        callback.apply();
                } catch (Throwable e) {
                        openError(e);
                }
        }

        public static boolean openConfirm(String fmt, Object... args)
        {
                return ConfirmDialog.showDialog(fmt, args);
        }

        public static boolean openCheckConfirm(String fmt, Object... args)
        {
                return ConfirmDialog.showCheckDialog(fmt, args);
        }

        public static void openError(String fmt, Object... args)
        {
                ErrorDialog.showDialog(fmt, args);
        }

        /**
         * 弹出异常提示框
         */
        public static void openError(Throwable e)
        {
                openError(Causes.message(e));
        }

}
