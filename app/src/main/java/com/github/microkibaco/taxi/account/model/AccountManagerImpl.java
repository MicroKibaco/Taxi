package com.github.microkibaco.taxi.account.model;


import com.google.gson.Gson;

import com.github.microkibaco.taxi.account.model.response.Account;
import com.github.microkibaco.taxi.account.model.response.SmsCodeResponse;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.IRequest;
import com.github.microkibaco.taxi.common.http.IResponse;
import com.github.microkibaco.taxi.common.http.api.API;
import com.github.microkibaco.taxi.common.http.biz.BaseBizResponse;
import com.github.microkibaco.taxi.common.http.impl.BaseRequest;
import com.github.microkibaco.taxi.common.http.impl.BaseResponse;
import com.github.microkibaco.taxi.common.storage.SharedPreferencesDao;
import com.github.microkibaco.taxi.common.util.LogUtil;

import rx.functions.Func1;

public class AccountManagerImpl implements IAccountManager {
    private static final String TAG = IAccountManager.class.getSimpleName();

    // 网络请求库
    private final IHttpClient mIHttpClient;

    // 数据存储
    private final SharedPreferencesDao mSharedPreferencesDao;

    public AccountManagerImpl(IHttpClient IHttpClient, SharedPreferencesDao sharedPreferencesDao) {
        this.mIHttpClient = IHttpClient;
        this.mSharedPreferencesDao = sharedPreferencesDao;
    }

    @Override
    public void fetchSMSCode(final String phone) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                final String url = API.Config.getDomain() + API.GET_SMS_CODE;
                final IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                final IResponse response = mIHttpClient.get(request, false);
                LogUtil.d(TAG, response.getData());
                final SmsCodeResponse smsResponse = new SmsCodeResponse();
                LogUtil.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);

                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        smsResponse.setCode(SMS_SEND_SUC);
                    } else {
                        smsResponse.setCode(SMS_SEND_FAIL);
                    }

                } else {
                    smsResponse.setCode(SMS_SEND_FAIL);
                }
                return smsResponse;
            }
        });

    }

    @Override
    public void checkSmsCode(String phone, String smsCode) {

    }

    @Override
    public void checkUserExit(String phone) {

    }

    @Override
    public void register(String phone, String password) {

    }

    @Override
    public void login(String phone, String password) {

    }

    @Override
    public void loginByToken() {

    }

    @Override
    public boolean isLogin() {

        // 获取本地信息
        final Account account = (Account)
                mSharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                        Account.class);

        // 登录是否过期
        boolean tokenValid = false;

        // 检查登录是否过期

        if (account != null) {

            if (account.getExpired() > System.currentTimeMillis()) {

                // token 有效
                tokenValid = true;

            }

        }

        return tokenValid;
    }
}
