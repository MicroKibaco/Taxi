package com.github.microkibaco.taxi.account.view;


public interface ICreatePasswordDialogView extends IView {

    /**
     * 显示注册成功
     */
    void showRegisterSuc();

    /**
     * 显示登录成功
     */
    void showLoginSuc();

    /**
     * 显示密码为空
     */
    void showPasswordNull();

    /**
     * 显示两次密码输入不一致
     */
    void showPasswordNotEqual();

}
