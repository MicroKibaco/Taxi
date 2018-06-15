package com.github.microkibaco.taxi.main.view;

import com.github.microkibaco.taxi.account.view.IView;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;
import com.github.microkibaco.taxi.main.model.bean.Order;

import java.util.List;


public interface IMainView extends IView {

    void showLoginSuc();

    /**
     * 附近司机
     */
    void showNears(List<LocationInfo> data);


    /**
     * 显示位置变化
     */
    void showLocationChange(LocationInfo locationInfo);

    /**
     * 显示呼叫成功发出
     */
    void showCallDriverSuc(Order order);

    /**
     * 显示呼叫未成功发出
     */
    void showCallDriverFail();


    /**
     * 取消订单成功
     */
    void showCancelSuc();

    /**
     * 显示取消定失败
     */
    void showCancelFail();


    /**
     * 显示司机接单
     */
    void showDriverAcceptOrder(Order mCurrentOrder);

    /**
     * 司机到达上车地点
     */
    void showDriverArriveStart(Order mCurrentOrder);

    /**
     * 更新司机到上车点的路径
     */
    void updateDriver2StartRoute(LocationInfo locationInfo, Order order);

    /**
     * 更新司机到上车点的路径
     */
    void showStartDrive(Order order);

    /**
     * 显示到达终点
     */
    void showArriveEnd(Order order);

    /**
     * 更新司机到终点的路径
     */
    void updateDriver2EndRoute(LocationInfo locationInfo, Order order);


    /**
     * 支付成功
     */
    void showPaySuc(Order mCurrentOrder);

    /**
     * 显示支付失败
     */
    void showPayFail();
}
