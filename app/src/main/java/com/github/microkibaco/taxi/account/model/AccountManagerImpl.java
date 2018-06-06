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
    private final IHttpClient httpClient;
    private final SharedPreferencesDao sharedPreferencesDao;


    public AccountManagerImpl(IHttpClient httpClient,
                              SharedPreferencesDao sharedPreferencesDao) {
        this.httpClient = httpClient;
        this.sharedPreferencesDao = sharedPreferencesDao;
    }


    /**
     * 获取验证码
     */
    @Override
    public void fetchSMSCode(final String phone) {


        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                final String url = API.Config.getDomain() + API.GET_SMS_CODE;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                final IResponse response = httpClient.get(request, false);
                Log.e(TAG, response.getData());
                final SmsCodeResponse smsCodeResponse = new SmsCodeResponse();
                LogUtil.e(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        smsCodeResponse.setCode(SMS_SEND_SUC);
                    } else {
                        smsCodeResponse.setCode(SMS_SEND_FAIL);
                    }
                } else {
                    smsCodeResponse.setCode(SMS_SEND_FAIL);
                }
                return smsCodeResponse;
            }
        });
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                final String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                request.setBody(FLAG_CODE, smsCode);
                final IResponse response = httpClient.get(request, false);
                final SmsCodeResponse smsCodeResponse = new SmsCodeResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        smsCodeResponse.setCode(SMS_CHECK_SUC);
                    } else {
                        smsCodeResponse.setCode(SMS_CHECK_FAIL);
                    }
                } else {
                    smsCodeResponse.setCode(SMS_CHECK_FAIL);
                }
                return smsCodeResponse;
            }
        });
    }

    /**
     * 检查用户是否存在
     */
    @Override
    public void checkUserExit(final String phone) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                final String url = API.Config.getDomain() + API.CHECK_USER_EXIST;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                final IResponse response = httpClient.get(request, false);
                Log.e(TAG, response.getData());
                final UserExistResponse existResponse = new UserExistResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    final BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST) {
                        existResponse.setCode(USER_EXIST);
                    } else if (bizRes.getCode() ==
                            BaseBizResponse.STATE_USER_NOT_EXIST) {
                        existResponse.setCode(USER_NOT_EXIST);
                    }
                } else {
                    existResponse.setCode(SERVER_FAIL);
                }
                return existResponse;
            }
        });
    }

    /**
     * 注册
     */
    @Override
    public void register(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                final String url = API.Config.getDomain() + API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                request.setBody(FLAG_PASSWORD, password);
                request.setBody(FLAG_UID, DevUtil.UUID(TaxiApplication.getInstance()));

                final IResponse response = httpClient.post(request, false);
                Log.e(TAG, response.getData());

                final RegisterResponse registerResponse = new RegisterResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
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

    /**
     * 登录
     */
    @Override
    public void login(final String phone, final String password) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                final String url = API.Config.getDomain() + API.LOGIN;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_PHONE, phone);
                request.setBody(FLAG_PASSWORD, password);


                final IResponse response = httpClient.post(request, false);
                Log.e(TAG, response.getData());

                LoginResponse bizRes = new LoginResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        final Account account = bizRes.getData();
                        sharedPreferencesDao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
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

    /**
     * token 自动登录
     */
    @Override
    public void loginByToken() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                // 获取本地登录信息
                Account account =
                        (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                                Account.class);


                // 登录是否过期
                boolean tokenValid = false;

                // 检查token是否过期

                if (account != null) {
                    if (account.getExpired() > System.currentTimeMillis()) {
                        // token 有效
                        tokenValid = true;
                    }
                }

                LoginResponse loginResponse = new LoginResponse();
                if (!tokenValid) {
                    loginResponse.setCode(TOKEN_INVALID);
                    return loginResponse;
                }


                // 请求网络，完成自动登录
                final String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                final IRequest request = new BaseRequest(url);
                request.setBody(FLAG_TOKEN, account.getToken());
                final IResponse response = httpClient.post(request, false);
                Log.e(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {

                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        account = loginResponse.getData();
                        // todo: 加密存储
                        sharedPreferencesDao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
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

        // 获取本地登录信息
        Account account =
                (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                        Account.class);


        // 登录是否过期
        boolean tokenValid = false;

        // 检查token是否过期

        if (account != null) {
            if (account.getExpired() > System.currentTimeMillis()) {
                // token 有效
                tokenValid = true;
            }
        }
        return tokenValid;
    }
}
