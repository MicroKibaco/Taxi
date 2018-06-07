package com.github.microkibaco.taxi.main.model;

import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;

/**
 * Created by MicroKibaco on 07/06/2018.
 */

public interface IMainManager {
    /**
     * 获取附近司机
     */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报位置
     */
    void updateLocationToServer(LocationInfo locationInfo);

    /**
     * 呼叫司机
     */
    void callDriver(String key,
                    float cost,
                    LocationInfo startLocation,
                    LocationInfo endLocation);

    /**
     * 取消订单
     */
    void cancelOrder(String orderId);

    /**
     * 支付
     */
    void pay(String orderId);

    /**
     * 获取正在处理中的订单
     */
    void getProcessingOrder();
}
