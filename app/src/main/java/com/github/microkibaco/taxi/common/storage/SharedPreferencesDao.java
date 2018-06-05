package com.github.microkibaco.taxi.common.storage;

import com.google.gson.Gson;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.microkibaco.taxi.common.util.LogUtil;

/**
 * ShraredPreference 数据访问对象
 */

public class SharedPreferencesDao {

    private static final String TAG = "SharedPreferencesDao";
    public static final String FILE_ACCOUNT = "FILE_ACCOUNT";
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private final SharedPreferences sharedPreferences;


    /**
     * init
     */
    public SharedPreferencesDao(Application application, String fileName) {
        sharedPreferences =
                application.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }


    /**
     * 保存 k-v
     */

    public void save(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 读取 k-v
     */

    public String get(String key) {
        return sharedPreferences.getString(key, null);
    }

    /**
     * 保存对象
     */

    public void save(String key, Object object) {
        final String value = new Gson().toJson(object);
        save(key, value);
    }

    /**
     * 读取对象
     */

    public Object get(String key, Class cls) {
        final String value = get(key);

        try {
            if (value != null) {
                final Object o = new Gson().fromJson(value, cls);
                return o;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
        return null;
    }
}
