package com.github.microkibaco.taxi.common.lbs.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;
import com.github.microkibaco.taxi.common.lbs.model.RouteInfo;
import com.github.microkibaco.taxi.common.lbs.view.CommonLocationChangeListener;
import com.github.microkibaco.taxi.common.lbs.view.ILbsLayer;
import com.github.microkibaco.taxi.common.lbs.view.OnRouteCompleteListener;
import com.github.microkibaco.taxi.common.lbs.view.OnSearchedListener;
import com.github.microkibaco.taxi.common.util.LogUtil;
import com.github.microkibaco.taxi.common.util.SensorEventHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 高德地图实现基类
 */

public class AmapLbsLayerImpl implements ILbsLayer,
        LocationSource, AMapLocationListener {

    private static final String TAG = AmapLbsLayerImpl.class.getSimpleName();
    private static final String KEY_MY_MARKER = "1000";

    private final Context mContext;
    private final MapView mMapView;
    private final AMap mAMap;
    private AMapLocationClient mLocationClient;
    private final AMapLocationClientOption mLocationOption;
    private final SensorEventHelper mSensorEventHelper;
    private CommonLocationChangeListener mLocationChangeListener;
    private LocationSource.OnLocationChangedListener mMapLocationChangeListener;
    private boolean firstLocation = true;
    private MyLocationStyle mMyLocationStyle;
    private Map<String, Marker> mMarkerMap;
    private String mCity;
    private RouteSearch mRouteSearch;

    public AmapLbsLayerImpl(Context context) {
        this.mContext = context;
        //  创建地图对象
        mMapView = new MapView(context);
        // 获取地图管理器
        mAMap = mMapView.getMap();
        // 创建定位对象
        mLocationClient = new AMapLocationClient(context);
        // 设置为高精度定位模式
        mLocationOption = new AMapLocationClientOption();
        // 设置定位参数
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 传感器对象
        mSensorEventHelper = new SensorEventHelper(context);
        mSensorEventHelper.registerSensorListener();
        // 管理地图标记集合
        mMarkerMap = new HashMap<>();
    }

    @Override
    public View getMapView() {
        return mMapView;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener locationChangeListener) {
        this.mLocationChangeListener = locationChangeListener;
    }

    @Override
    public void setLocationRes(int res) {
        // 设置小蓝点的图标
        mMyLocationStyle = new MyLocationStyle();
        // 设置圆形的边框颜色
        mMyLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(res));
        // 设置圆形的填充颜色
        mMyLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
        // 设置圆形的边框粗细
        mMyLocationStyle.strokeWidth(1.0f);
    }

    @Override
    public void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap) {


        if (mMarkerMap == null) {
            mMarkerMap = new HashMap<>();
        }

        final Marker storedMarker = mMarkerMap.get(locationInfo.getKey());
        final LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());

        if (storedMarker != null) {

            // 如果已经存在则更新角度、位置
            storedMarker.setPosition(latLng);
            storedMarker.setRotateAngle(locationInfo.getRotation());

        } else {

            // 如果不存在则创建
            final MarkerOptions options = new MarkerOptions();
            final BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bitmap);
            options.icon(des);
            options.anchor(0.5f, 0.5f);
            options.position(latLng);
            final Marker marker = mAMap.addMarker(options);
            marker.setRotateAngle(android.R.attr.rotation);
            mMarkerMap.put(locationInfo.getKey(), marker);

            if (KEY_MY_MARKER.equals(locationInfo.getKey())) {

                // 传感器控制我的位置标记的旋转角度
                mSensorEventHelper.setCurrentMarker(marker);
            }

        }


    }

    @Override
    public String getCity() {
        return mCity;
    }

    /**
     * Amap POI 搜索接口
     */
    @Override
    public void poiSearch(String key, final OnSearchedListener listener) {

        if (TextUtils.isEmpty(key)) {
            return;
        }
        // 组装关键字
        final InputtipsQuery inputQuery = new InputtipsQuery(key, "");
        final Inputtips inputTips = new Inputtips(mContext, inputQuery);

        // 开始异步搜索
        inputTips.requestInputtipsAsyn();

        // 监听处理搜索结果
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {

                if (rCode == AMapException.CODE_AMAP_SUCCESS) {

                    // 正确返回解析结果

                    final List<LocationInfo> locationInfos = new ArrayList<>();

                    for (int i = 0; i < tipList.size(); i++) {
                        final Tip tip = tipList.get(i);
                        if (tip.getPoint() != null) {

                            final LocationInfo locationInfo =
                                    new LocationInfo(tip.getPoint().getLatitude(),
                                            tip.getPoint().getLongitude());

                            locationInfo.setName(tip.getName());
                            locationInfos.add(locationInfo);
                        }
                    }

                    listener.onSearched(locationInfos);

                } else {

                    listener.onError(rCode);

                }

            }
        });

    }

    /**
     * 两点之间行车路径
     */
    @Override
    public void driverRoute(LocationInfo start,
                            LocationInfo end,
                            final int color,
                            final OnRouteCompleteListener listener) {

        // 组装起点和终点信息
        final LatLonPoint startLatLng =
                new LatLonPoint(start.getLatitude(), start.getLongitude());

        final LatLonPoint endLatLng =
                new LatLonPoint(end.getLatitude(), end.getLongitude());

        final RouteSearch.FromAndTo fromAndTo =
                new RouteSearch.FromAndTo(startLatLng, endLatLng);

        // 创建路径查询参数
        // 第一个参数表示路径规划的起点和终点，
        // 第二个参数表示驾车模式，
        // 第三个参数表示途经点，
        // 第四个参数表示避让区域，
        // 第五个参数表示避让道路
        final RouteSearch.DriveRouteQuery query =
                new RouteSearch.DriveRouteQuery(fromAndTo,
                        RouteSearch.DrivingDefault,
                        null,
                        null,
                        "");

        // 创建搜索对象，异步路径规划驾车模式查询
        if (mRouteSearch == null) {
            mRouteSearch = new RouteSearch(mContext);
        }

        // 执行搜索
        mRouteSearch.calculateDriveRouteAsyn(query);

        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int color) {
                // 1. 获取第一条路径
                final DrivePath drivePath = driveRouteResult.getPaths().get(0);

                // 2. 获取这条路径上所有的点，使用 Polyline 绘制路径
                final PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(color);

                // 起点
                final LatLonPoint startPoint = driveRouteResult.getStartPos();

                // 路径中间步骤
                final List<DriveStep> drivePaths = drivePath.getSteps();

                // 路径终点
                final LatLonPoint endPoint = driveRouteResult.getTargetPos();

                // 添加起点
                polylineOptions.add(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()));

                // 添加中间节点

                for (DriveStep step : drivePaths) {
                    final List<LatLonPoint> latlonPoints = step.getPolyline();
                    for (LatLonPoint latlonpoint : latlonPoints) {
                        final LatLng latLng =
                                new LatLng(latlonpoint.getLatitude(), latlonpoint.getLongitude());
                        polylineOptions.add(latLng);

                    }
                }

                // 添加终点

                polylineOptions.add(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()));

                // 执行绘制

                mAMap.addPolyline(polylineOptions);

                // 3. 回调业务

                if (listener != null) {

                    final RouteInfo info = new RouteInfo();
                    info.setTaxiCost(driveRouteResult.getTaxiCost());
                    info.setDuration(10 + Long.valueOf(drivePath.getDuration() / 1000 * 60).intValue());
                    info.setDistance(0.5f + drivePath.getDistance() / 1000);
                    listener.onComplete(info);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });


    }

    @Override
    public void onCreate(Bundle state) {
        mMapView.onCreate(state);
        setUpMap();
    }

    private void setUpMap() {

        if (mMyLocationStyle != null) {

            mAMap.setMyLocationStyle(mMyLocationStyle);

        }

        // 设置地图激活（加载监听）
        mAMap.setLocationSource(this);

    }

    @Override
    public void onResume() {
        mMapView.onResume();
        setUpLocation();
    }


    private void setUpLocation() {
        //设置监听器
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        mMapView.onPause();
        mLocationClient.stopLocation();

    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mLocationClient.onDestroy();
    }

    @Override
    public void clearAllMarkers() {
        mAMap.clear();
        mMarkerMap.clear();
    }

    @Override
    public void moveCamera(LocationInfo locationFrom, LocationInfo locationTo) {

        try {
            final LatLng latLngFrom =
                    new LatLng(locationFrom.getLatitude(),
                            locationFrom.getLongitude());

            final LatLng latLngTo =
                    new LatLng(locationTo.getLatitude(),
                            locationTo.getLongitude());

            final LatLngBounds.Builder latLngBuilder =
                    LatLngBounds.builder();

            latLngBuilder.include(latLngFrom);
            latLngBuilder.include(latLngTo);

            final LatLngBounds latLngBounds =
                    latLngBuilder.build();

            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

        } catch (Exception e) {
            LogUtil.e(TAG, "moveCamera: " + e.getMessage());
        }
    }

    @Override
    public void moveCameraToPoint(LocationInfo locationInfo, int scale) {

        final LatLng latLng = new LatLng(locationInfo.getLatitude(),
                locationInfo.getLongitude());

        final CameraUpdate up = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                latLng, scale, 30, 30));

        mAMap.moveCamera(up);
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mMapLocationChangeListener = onLocationChangedListener;
        LogUtil.e(TAG, "activate");
    }

    @Override
    public void deactivate() {

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        // 定位变化位置
        if (mLocationChangeListener != null) {

            // 当前城市
            mCity = aMapLocation.getCity();
            // 地图已经激活，通知蓝点实时更新
            mMapLocationChangeListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            LogUtil.e(TAG, "onLocationChanged");

            final LocationInfo locationInfo = new LocationInfo(aMapLocation.getLatitude(),
                    aMapLocation.getLongitude());
            locationInfo.setName(aMapLocation.getPoiName());
            locationInfo.setKey(KEY_MY_MARKER);

            if (firstLocation) {
                firstLocation = false;
                moveCameraToPoint(locationInfo, 17);

                if (mLocationChangeListener != null) {

                    mLocationChangeListener.onLocation(locationInfo);
                }

            }

            if (mLocationChangeListener != null) {

                mLocationChangeListener.onLocationChanged(locationInfo);
            }

        }
    }
}
