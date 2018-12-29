package org.song.qswidgets.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.song.qswidgets.AppContext;


/**
 * 作者：nayuta on 2016/6/12 15:11
 * 邮箱：
 * <p/>
 * 可以跑在子线程的toast
 */
public final class ToastUtil {

    private static Toast toast;
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    showCustomToast((String) msg.obj, Toast.LENGTH_LONG);
                    break;
                case 2:
                    showCustomToast((String) msg.obj, Toast.LENGTH_SHORT);
                    break;
            }
        }
    };


    public static void showToast(String message) {
        MAIN_HANDLER.sendMessage(MAIN_HANDLER.obtainMessage(1, message));
    }

    public static void showToast(int resid) {
        MAIN_HANDLER.sendMessage(MAIN_HANDLER.obtainMessage(1, AppContext.getInstance().getResources().getString(resid)));
    }

    public static void showShortToast(String message) {
        MAIN_HANDLER.sendMessage(MAIN_HANDLER.obtainMessage(2, message));
    }

    public static void showCustomToast(String message, int duration) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(AppContext.getInstance(), message, duration);
        toast.show();
    }

}
