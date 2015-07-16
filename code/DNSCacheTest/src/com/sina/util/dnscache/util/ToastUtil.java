package com.sina.util.dnscache.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtil {

    public static void showText(Context ctx, String msg) {
        showText(ctx, msg, false);
    }

    public static void showText(Context ctx, String msg, boolean longShow) {
        if (null == ctx || TextUtils.isEmpty(msg)) {
            return;
        }
        int mode = longShow ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(ctx, msg, mode).show();
    }
}
