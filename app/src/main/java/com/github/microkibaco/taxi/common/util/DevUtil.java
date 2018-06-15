package com.github.microkibaco.taxi.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 设备相关工具类
 */

@SuppressWarnings("ConstantConditions")
public class DevUtil {

    /**
     * 获取 UID
     */

    @SuppressLint("HardwareIds")
    public static String UUID(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = "";
        try {
            tm.getDeviceId();
        } catch (Exception e) {
            LogUtil.e("UUID", e.getMessage());
        }
        return deviceId + System.currentTimeMillis();
    }

    public static void closeInputMethod(Activity context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
