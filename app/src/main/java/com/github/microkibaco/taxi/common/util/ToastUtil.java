package com.github.microkibaco.taxi.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 工具类
 */

public class ToastUtil {

    public static void show(Context context, String str) {

        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

    }

}
