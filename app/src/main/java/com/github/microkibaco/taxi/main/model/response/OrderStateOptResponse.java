package com.github.microkibaco.taxi.main.model.response;

import com.github.microkibaco.taxi.common.http.biz.BaseBizResponse;
import com.github.microkibaco.taxi.main.model.bean.Order;

/**
 * 订单操作状态
 */

public class OrderStateOptResponse extends BaseBizResponse {
    // 创建订单
    public final static int ORDER_STATE_CREATE = 0;
    // 取消订单
    public static final int ORDER_STATE_CANCEL = -1;
    // 司机到达
    public static final int ORDER_STATE_ACCEPT = 1;
    // 司机开始行程
    public static final int ORDER_STATE_ARRIVE_START = 2;
    // 到达目的地
    public static final int ORDER_STATE_ARRIVE_END = 4;
    // 已支付
    public static final int PAY = 5;
    // 携带操作的订单
    private Order data;
    // 订单状态
    private int state;

    public Order getData() {
        return data;
    }

    public void setData(Order data) {
        this.data = data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
