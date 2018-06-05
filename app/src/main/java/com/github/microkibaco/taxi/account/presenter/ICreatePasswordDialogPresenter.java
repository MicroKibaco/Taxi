package com.github.microkibaco.taxi.account.presenter;


public interface ICreatePasswordDialogPresenter {

    /**
     * 校验密码输入合法性
     */
    boolean checkPw(String pw, String newPw);

    /**
     * 提交注册
     */
    void requestRegister(String phone, String pw);

    /**
     * 登录
     */
    void requestLogin(String phone, String pw);

}
