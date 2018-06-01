package com.github.microkibaco.taxi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 1. 检查本地记录(登录状态检查)
 * 2. 若用户没有登录则登录
 * 3. 登录之前先校验手机号码
 * 4. todo: 地图初始化
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLoginState();
    }


    /**
     * 检查用户是否登录
     */
    private void checkLoginState() {
        // todo: 获取用户登录信息

        // 登录是否过期
        boolean tokenValid = false;

        // todo: 检查token是否过期

        if (!tokenValid) {

            showPhoneInputDialog();

        } else {

            // todo: 请求网络,完成自动登录

        }


    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {

    }
}
