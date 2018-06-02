package com.github.microkibaco.taxi.account;

public interface ISmsCodeDialogView extends IView {
    /**
     * 显示倒计时
     */

    void showCountDownTimer();
    /**
     * 显示验证状态
     */
    void showSmsCodeCheckState(boolean b);

    /**
     * 用户是否存在
     */
    void showUserExist(boolean b);
}
