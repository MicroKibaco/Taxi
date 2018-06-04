package com.github.microkibaco.taxi.account.presenter;

import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.model.response.SmsCodeResponse;
import com.github.microkibaco.taxi.account.model.response.UserExistResponse;
import com.github.microkibaco.taxi.account.view.ISmsCodeDialogView;
import com.github.microkibaco.taxi.common.databus.RegisterBus;


public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter {

    private ISmsCodeDialogView view;
    private IAccountManager accountManager;

    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView view,
                                      IAccountManager accountManager) {
        this.view = view;
        this.accountManager = accountManager;
    }

    @RegisterBus
    public void onSmsCodeResponse(SmsCodeResponse smsResponse) {

        switch (smsResponse.getCode()) {

            case IAccountManager.SMS_SEND_SUC:
                view.showCountDownTimer();
                break;

            case IAccountManager.SMS_SEND_FAIL:
                view.showError(IAccountManager.SMS_SEND_FAIL, "");
                break;

            case IAccountManager.SMS_CHECK_SUC:
                view.showSmsCodeCheckState(true);
                break;

            case IAccountManager.SMS_CHECK_FAIL:
                view.showError(IAccountManager.SMS_CHECK_FAIL, "");
                break;

            case IAccountManager.SERVER_FAIL:
                view.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }

    }


    @RegisterBus
    public void onSmsCodeResponse(UserExistResponse userExistResponse) {

        switch (userExistResponse.getCode()) {

            case IAccountManager.USER_EXIST:

                view.showUserExist(true);

                break;

            case IAccountManager.USER_NOT_EXIST:

                view.showUserExist(false);

                break;

            case IAccountManager.SERVER_FAIL:

                view.showError(IAccountManager.SERVER_FAIL, "");

                break;

        }

    }


    /**
     * 获取验证码
     */
    @Override
    public void requestSendSmsCode(String phone) {
        accountManager.fetchSMSCode(phone);
    }


    /**
     * 验证码校验
     */
    @Override
    public void requestCheckSmsCode(String phone, String smsCode) {
        accountManager.checkSmsCode(phone, smsCode);
    }

    /**
     * 检查用户是否存在
     */
    @Override
    public void requestCheckUserExit(String phone) {
        accountManager.checkUserExit(phone);
    }
}
