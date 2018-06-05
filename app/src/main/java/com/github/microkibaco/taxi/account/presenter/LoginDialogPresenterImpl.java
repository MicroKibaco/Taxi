package com.github.microkibaco.taxi.account.presenter;


import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.model.response.LoginResponse;
import com.github.microkibaco.taxi.account.view.ILoginView;
import com.github.microkibaco.taxi.common.databus.RegisterBus;

public class LoginDialogPresenterImpl implements ILoginDialogPresenter {

    private ILoginView mView;
    private IAccountManager mAccountManager;

    public LoginDialogPresenterImpl(ILoginView view, IAccountManager accountManager) {
        this.mView = view;
        this.mAccountManager = accountManager;
    }

    @Override
    public void requestLogin(String phone, String password) {
        mAccountManager.login(phone, password);
    }

    @RegisterBus
    public void onLoginResponse(LoginResponse onLoginResponse) {

        switch (onLoginResponse.getCode()) {

            case IAccountManager.LOGIN_SUC:
                // 登录成功
                mView.showLoginSuc();
                break;

            case IAccountManager.PW_ERROR:
                // 密码错误
                mView.showError(IAccountManager.PW_ERROR, "");
                break;

            case IAccountManager.SERVER_FAIL:
                // 服务错误
                mView.showError(IAccountManager.SERVER_FAIL, "");
                break;

        }

    }
}
