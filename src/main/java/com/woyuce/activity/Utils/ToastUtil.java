package com.woyuce.activity.Utils;

/**
 * @author LeBang
 * @Description:Toast工具类
 * @date 2016-9-30
 */

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast = null;

    public static void showMessage(Context context, String msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(Context context, String msg, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, duration);
        } else {
            toast.setText(msg);
            toast.setDuration(duration);
        }
        toast.show();
    }
}