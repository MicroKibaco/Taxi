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
}
