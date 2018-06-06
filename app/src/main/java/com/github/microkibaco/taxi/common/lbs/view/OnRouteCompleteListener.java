package com.github.microkibaco.taxi.common.lbs.view;

import com.github.microkibaco.taxi.common.lbs.model.RouteInfo;

/**
 * 路径规划完成监听
 */

public interface OnRouteCompleteListener {
    void onComplete(RouteInfo result);
}
