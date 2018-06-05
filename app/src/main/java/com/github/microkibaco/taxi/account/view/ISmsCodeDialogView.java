package com.github.microkibaco.taxi.account.view;

public interface ISmsCodeDialogView extends IView {
    /**
     * 显示倒计时
     */

    void showCountDownTimer();
    /**
     * 显示验证状态
     */
    void showSmsCodeCheckState(boolean suc);

    /**
     * 用户是否存在
     */
    void showUserExist(boolean exist);
}
