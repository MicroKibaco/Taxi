package com.github.microkibaco.taxi.account.model;


import com.google.gson.Gson;

import android.util.Log;

import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.response.Account;
import com.github.microkibaco.taxi.account.model.response.LoginResponse;
import com.github.microkibaco.taxi.account.model.response.RegisterResponse;
import com.github.microkibaco.taxi.account.model.response.SmsCodeResponse;
import com.github.microkibaco.taxi.account.model.response.UserExistResponse;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.IRequest;
import com.github.microkibaco.taxi.common.http.IResponse;
import com.github.microkibaco.taxi.common.http.api.API;
import com.github.microkibaco.taxi.common.http.biz.BaseBizResponse;
import com.github.microkibaco.taxi.common.http.impl.BaseRequest;
import com.github.microkibaco.taxi.common.http.impl.BaseResponse;
import com.github.microkibaco.taxi.common.storage.SharedPreferencesDao;
import com.github.microkibaco.taxi.common.util.DevUtil;
import com.github.microkibaco.taxi.common.util.LogUtil;

import rx.functions.Func1;

public class AccountManagerImpl implements IAccountManager {
    private static final String TAG = AccountManagerImpl.class.getSimpleName();
    private static final String FLAG_PHONE = "phone";
    private static final String FLAG_CODE = "code";
    private static final String FLAG_PASSWORD = "password";
    private static final String FLAG_UID = "uid";
    private static final String FLAG_TOKEN = "token";

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
                request.setBody(FLAG_PHONE, phone);
                final IResponse response = mIHttpClient.get(request, false);
                LogUtil.e(TAG, response.getData());
                final SmsCodeResponse smsResponse = new SmsCodeResponse();
                LogUtil.e(TAG, response.getData());
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
    public void checkSmsCode(final String phone, final String smsCode) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                final String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                request.setBody(FLAG_CODE, smsCode);
                final IResponse response = mIHttpClient.get(request, false);
                LogUtil.e(TAG, response.getData());
                final SmsCodeResponse codeResponse = new SmsCodeResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseResponse.STATE_OK) {
                        codeResponse.setCode(SMS_CHECK_SUC);
                    } else {
                        codeResponse.setCode(SMS_CHECK_FAIL);
                    }

                } else {
                    codeResponse.setCode(SMS_CHECK_FAIL);

                }
                return codeResponse;
            }
        });

    }

    @Override
    public void checkUserExit(final String phone) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                final String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                final IResponse response = mIHttpClient.get(request, false);
                Log.e(TAG, response.getData());
                final UserExistResponse existResponse = new UserExistResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);

                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST) {

                        existResponse.setCode(USER_EXIST);

                    } else if (bizRes.getCode() ==
                            BaseBizResponse.STATE_USER_NOT_EXIST) {
                        existResponse.setCode(USER_NOT_EXIST);
                    } else {
                        existResponse.setCode(USER_NOT_EXIST);
                    }

                }
                return existResponse;
            }
        });

    }

    @Override
    public void register(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                final String url = API.Config.getDomain() + API.REGISTER;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                request.setBody(FLAG_PASSWORD, password);
                request.setBody(FLAG_UID, DevUtil.UUID(TaxiApplication.getInstance()));

                final IResponse response = mIHttpClient.post(request, false);
                Log.e(TAG, response.getData());

                final RegisterResponse registerResponse = new RegisterResponse();

                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        registerResponse.setCode(REGISTER_SUC);
                    } else {
                        registerResponse.setCode(SERVER_FAIL);
                    }
                } else {
                    registerResponse.setCode(SERVER_FAIL);
                }
                return registerResponse;
            }
        });
    }

    @Override
    public void login(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                final String url = API.Config.getDomain() + API.LOGIN;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                request.setBody(FLAG_PASSWORD, password);

                final IResponse response = mIHttpClient.post(request, false);
                LogUtil.e(TAG, response.getData());

                LoginResponse bizRes = new LoginResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        final Account account = bizRes.getData();
                        mSharedPreferencesDao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
                        // 通知 UI
                        bizRes.setCode(LOGIN_SUC);
                    } else if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        bizRes.setCode(PW_ERROR);

                    } else {
                        bizRes.setCode(SERVER_FAIL);
                    }
                } else {
                    bizRes.setCode(SERVER_FAIL);
                }
                return bizRes;
            }
        });
    }

    @Override
    public void loginByToken() {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                // 获取登录信息
                Account account =
                        (Account) mSharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                                Account.class);

                // 登录是否过期
                boolean tokenValid = false;

                // 检查token 是否过期
                if (account != null) {
                    if (account.getExpired() > System.currentTimeMillis()) {

                        // token 有效

                        tokenValid = true;
                    }
                }

                final LoginResponse loginResponse = new LoginResponse();
                if (!tokenValid) {
                    loginResponse.setCode(TOKEN_INVALID);
                    return loginResponse;
                }

                // 请求网络完成自动登录
                final String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_TOKEN, account.getToken());
                final IResponse response = mIHttpClient.post(request, false);
                LogUtil.e(TAG, response.getData());


                if (response.getCode() == BaseResponse.STATE_OK) {


                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {

                        // 保存登录信息
                        account = loginResponse.getData();

                        // todo: 加密存储
                        mSharedPreferencesDao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                        loginResponse.setCode(LOGIN_SUC);
                    } else if (loginResponse.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {

                        loginResponse.setCode(TOKEN_INVALID);
                    }


                } else {
                    loginResponse.setCode(SERVER_FAIL);

                }

                return loginResponse;
            }
        });

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
