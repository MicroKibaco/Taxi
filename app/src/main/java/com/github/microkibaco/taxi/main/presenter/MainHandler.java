package com.github.microkibaco.taxi.main.presenter;

import android.os.Handler;
import android.os.Message;

import com.github.microkibaco.taxi.account.model.IAccountManager;

import java.lang.ref.WeakReference;

/**
 * 接收子线程消息的 Handler
 */
public class MainHandler extends Handler {

    // 弱引用
    private WeakReference<MainPresenterImpl> dialogRef;

    public MainHandler(MainPresenterImpl presenter) {
        dialogRef = new WeakReference<>(presenter);
    }

    /**
     * 处理UI 变化
     */
    @Override
    public void handleMessage(Message msg) {
        MainPresenterImpl presenter = dialogRef.get();
        if (presenter == null) {
            return;
        }
        switch (msg.what) {
            case IAccountManager.LOGIN_SUC:
                // 登录成功
                presenter.view.showLoginSuc();
                break;
            case IAccountManager.TOKEN_INVALID:
                // 登录过期
                presenter.view.showError(IAccountManager.TOKEN_INVALID, "");
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                presenter.view.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }

    }
}
