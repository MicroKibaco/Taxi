package com.github.microkibaco.taxi.common.lbs.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;
import com.github.microkibaco.taxi.common.lbs.view.CommonLocationChangeListener;
import com.github.microkibaco.taxi.common.lbs.view.ILbsLayer;
import com.github.microkibaco.taxi.common.lbs.view.OnRouteCompleteListener;
import com.github.microkibaco.taxi.common.lbs.view.OnSearchedListener;

/**
 * 高德地图实现基类
 */

public class AmapLbsLayerImpl implements ILbsLayer {

    private static final String TAG = AmapLbsLayerImpl.class.getSimpleName();
    private static final String KEY_MY_MARKER = "1000";

    private Context mContext;

    public AmapLbsLayerImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public View getMapView() {
        return null;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener locationChangeListener) {

    }

    @Override
    public void setLocationRes(int res) {

    }

    @Override
    public void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap) {

    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public void poiSearch(String key, OnSearchedListener listener) {

    }

    @Override
    public void driverRoute(LocationInfo start, LocationInfo end, int color, OnRouteCompleteListener listener) {

    }

    @Override
    public void onCreate(Bundle state) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void clearAllMarkers() {

    }

    @Override
    public void moveCamera(LocationInfo locationFrom, LocationInfo locationTo) {

    }

    @Override
    public void moveCameraToPoint(LocationInfo locationInfo, int scale) {

    }
}
