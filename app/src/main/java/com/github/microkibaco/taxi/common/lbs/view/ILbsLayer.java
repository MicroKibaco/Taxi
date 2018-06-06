package com.github.microkibaco.taxi.common.lbs.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;

/**
 * 定义地图服务通用抽象接口
 */

public interface ILbsLayer {

    /**
     * 获取地图
     */
    View getMapView();

    /**
     * 设置位置变化监听
     */
    void setLocationChangeListener(CommonLocationChangeListener locationChangeListener);

    /**
     * 设置定位图标
     */
    void setLocationRes(int res);

    /**
     * 添加，更新标记点，包括位置、角度（通过 id 识别）
     */
    void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap);

    /**
     * 获取当前城市
     */
    String getCity();

    /**
     * 联动搜索附近的位置
     */
    void poiSearch(String key, OnSearchedListener listener);

    /**
     * 绘制两点之间行车路径
     */
    void driverRoute(LocationInfo start,
                     LocationInfo end,
                     int color,
                     OnRouteCompleteListener listener);

    /**
     * 生命周期函数
     */
    void onCreate(Bundle state);

    void onResume();

    void onSaveInstanceState(Bundle outState);

    void onPause();

    void onDestroy();

    void clearAllMarkers();

    /**
     * 移动相机到两点之间的视野范围
     */
    void moveCamera(LocationInfo locationFrom,
                    LocationInfo locationTo);

    /**
     * 移动动相机到某个点
     */
    void moveCameraToPoint(LocationInfo locationInfo, int scale);

}
