package com.github.microkibaco.taxi.account;



public interface IView {

    /**
     * 显示loading
     */
    void showLoading();

    /**
     * 显示错误
     */
    void showError(int Code, String msg);

}
