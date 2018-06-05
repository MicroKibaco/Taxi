package com.github.microkibaco.taxi.account.presenter.impl;


import android.text.TextUtils;

import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.model.response.LoginResponse;
import com.github.microkibaco.taxi.account.model.response.RegisterResponse;
import com.github.microkibaco.taxi.account.presenter.ICreatePasswordDialogPresenter;
import com.github.microkibaco.taxi.account.view.ICreatePasswordDialogView;
import com.github.microkibaco.taxi.common.databus.RegisterBus;

public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {


    private ICreatePasswordDialogView view;
    private IAccountManager accountManager;

    /**
     * 注入 view 和 AccountManager 对象
     */
    public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view,
                                             IAccountManager IAccountManager) {
        this.view = view;
        this.accountManager = IAccountManager;
    }

    @Override
    public boolean checkPw(String pw, String newPw) {
        if (TextUtils.isEmpty(pw)) {
            view.showPasswordNull();
            return false;
        } else if (!pw.equals(newPw)) {
            view.showPasswordNotEqual();
            return false;
        }
        return true;
    }

    /**
     * 注册
     */
    @Override
    public void requestRegister(String phone, String pw) {

        accountManager.register(phone, pw);

    }

    @Override
    public void requestLogin(String phone, String pw) {
        accountManager.login(phone, pw);
    }

    @RegisterBus
    public void onRegisterResponse(RegisterResponse registerResponse) {
        // 处理 UI 变化
        switch (registerResponse.getCode()) {
            case IAccountManager.REGISTER_SUC:
                // 注册成功
                view.showRegisterSuc();
                break;
            case IAccountManager.LOGIN_SUC:
                // 登录成功
                view.showLoginSuc();
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                view.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }
    }

    @RegisterBus
    public void onLoginResponse(LoginResponse loginResponse) {

        // 处理 UI 变化
        switch (loginResponse.getCode()) {

            case IAccountManager.LOGIN_SUC:
                // 登录成功
                view.showLoginSuc();
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                view.showError(IAccountManager.SERVER_FAIL, "");
                break;

        }

    }
}
