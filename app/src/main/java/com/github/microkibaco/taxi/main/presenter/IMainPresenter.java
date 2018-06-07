package com.github.microkibaco.taxi.main.presenter;

import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;

public interface IMainPresenter {

    void loginByToken();

    /**
     * 获取附近的司机
     */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报当前位置
     */
    void updateLocationToServer(LocationInfo locationInfo);

    /**
     * 呼叫司机
     */
    void callDriver(String key,
                    float cost,
                    LocationInfo mStartLocation,
                    LocationInfo mEndLocation);

    boolean isLogin();

    /**
     * 取消呼叫
     */
    void cancel();

    /**
     * 支付
     */
    void pay();

    /**
     * 获取正在处理中的订单
     */
    void getProcessingOrder();

}
