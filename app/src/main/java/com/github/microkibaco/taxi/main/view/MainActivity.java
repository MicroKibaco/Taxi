package com.github.microkibaco.taxi.main.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.view.dialog.PhoneInputDialog;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.api.API;
import com.github.microkibaco.taxi.common.http.impl.OkHttpClientImpl;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;
import com.github.microkibaco.taxi.common.lbs.model.RouteInfo;
import com.github.microkibaco.taxi.common.lbs.presenter.AmapLbsLayerImpl;
import com.github.microkibaco.taxi.common.lbs.view.CommonLocationChangeListener;
import com.github.microkibaco.taxi.common.lbs.view.ILbsLayer;
import com.github.microkibaco.taxi.common.lbs.view.OnRouteCompleteListener;
import com.github.microkibaco.taxi.common.lbs.view.OnSearchedListener;
import com.github.microkibaco.taxi.common.util.DevUtil;
import com.github.microkibaco.taxi.common.util.LogUtil;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.model.IMainManager;
import com.github.microkibaco.taxi.main.model.MainMangerImpl;
import com.github.microkibaco.taxi.main.model.bean.Order;
import com.github.microkibaco.taxi.main.presenter.IMainPresenter;
import com.github.microkibaco.taxi.main.presenter.MainPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * 1. 检查本地记录(登录状态检查)
 * 2. 若用户没有登录则登录
 * 3. 登录之前先校验手机号码
 * 4. todo: 地图初始化
 */
public class MainActivity extends AppCompatActivity implements
        IMainView, CommonLocationChangeListener,
        TextWatcher, OnSearchedListener {

    @Bind(R.id.map_container)
    ContentFrameLayout mMapContainer;

    @Bind(R.id.im_user)
    AppCompatImageView mImUser;

    @Bind(R.id.city)
    AppCompatTextView mCity;

    @Bind(R.id.top)
    RelativeLayout mTop;

    @Bind(R.id.start)
    AppCompatAutoCompleteTextView mStart;

    @Bind(R.id.end)
    AppCompatAutoCompleteTextView mEnd;

    @Bind(R.id.tips_info)
    AppCompatTextView mTipsInfo;

    @Bind(R.id.loading_text)
    AppCompatTextView mLoadingText;

    @Bind(R.id.loading_area)
    LinearLayoutCompat mLoadingArea;

    @Bind(R.id.btn_call_driver)
    AppCompatButton mBtnCallDriver;

    @Bind(R.id.btn_cancel)
    AppCompatButton mBtnCancel;

    @Bind(R.id.btn_pay)
    AppCompatButton mBtnPay;
    @Bind(R.id.optArea)
    LinearLayoutCompat mOptArea;
    @Bind(R.id.select_area)
    LinearLayoutCompat mSelectArea;
    @Bind(R.id.activity_main)
    RelativeLayout mActivityMain;

    //  当前是否登录
    private boolean mIsLogin;
    private IMainPresenter mPresenter;
    private ILbsLayer mLbsLayer;
    private String mPushKey;
    private Bitmap mDriverBit;
    private Bitmap mStartBit;
    private Bitmap mEndBit;

    // 记录起点和终点
    private LocationInfo mStartLocation;
    private LocationInfo mEndLocation;
    private boolean mIsLocate;
    private float mCost;
    private Bitmap mLocationBit;
    private PoiAdapter mEndAdapter;
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final String LOCATION_END = "10000end";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListener();
        initAMap(savedInstanceState);
        // 推送服务
        initBmobSdk();
        initData();
    }

    private void initData() {
        final IMainManager mainManger = new MainMangerImpl(new OkHttpClientImpl());
        mPresenter = new MainPresenterImpl(this,
                TaxiApplication.getInstance().getAccountManager(),
                mainManger);

        mPresenter.loginByToken();
        mIsLogin = mPresenter.isLogin();
    }

    private void initBmobSdk() {
        // 初始化BmobSDK
        Bmob.initialize(this, API.Config.getAppId());
        final BmobInstallation installation =
                BmobInstallation.getCurrentInstallation(this);
        installation.save();
        mPushKey = installation.getInstallationId();

        // 启动推送服务
        BmobPush.startWork(this);
    }

    private void initListener() {
        mEnd.addTextChangedListener(this);
        RxBus.getInstance().register(mPresenter);
    }

    private void initAMap(@Nullable Bundle savedInstanceState) {
        // 地图服务
        mLbsLayer = new AmapLbsLayerImpl(this);
        mLbsLayer.onCreate(savedInstanceState);
        mLbsLayer.setLocationChangeListener(this);

        // 添加地图到容器
        final ViewGroup mapViewContainer =
                (ViewGroup) findViewById(R.id.map_container);
        mapViewContainer.addView(mLbsLayer.getMapView());
    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        final PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                RxBus.getInstance().register(mPresenter);
            }
        });
        RxBus.getInstance().unRegister(mPresenter);
    }

    @OnClick({R.id.btn_call_driver, R.id.btn_cancel, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_call_driver:
                callDriver();
                break;
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_pay:
                pay();
                break;
        }
    }

    /**
     * 支付
     */
    private void pay() {
        mLoadingArea.setVisibility(View.VISIBLE);
        mTipsInfo.setVisibility(View.GONE);
        mLoadingText.setText(R.string.paying);
        mPresenter.pay();
    }

    /**
     * 呼叫司机
     */
    private void callDriver() {
        mIsLogin = mPresenter.isLogin();
        if (mIsLogin) {

            showCalling();
            //   请求呼叫
            mPresenter.callDriver(mPushKey, mCost, mStartLocation, mEndLocation);
        } else {

            // 未登录，先登录
            ToastUtil.show(this, getString(R.string.pls_login));
            showPhoneInputDialog();

        }
    }

    /**
     * 取消
     */
    private void cancel() {
        if (!mBtnCallDriver.isEnabled()) {
            showCanceling();
            mPresenter.cancel();
        } else {
            restoreUI();
        }
    }

    /**
     * 知识显示了路径信息，还没点击呼叫，恢复 UI 即可
     */
    private void restoreUI() {
        // 清楚地图上所有标记：路径信息、起点、终点
        mLbsLayer.clearAllMarkers();
        // 添加定位标记
        addLocationMarker();
        // 恢复地图视野
        mLbsLayer.moveCameraToPoint(mStartLocation, 17);
        //  获取附近司机
        getNearDrivers(mStartLocation.getLatitude(), mStartLocation.getLongitude());
        // 隐藏操作栏
        hideOptArea();
    }

    private void hideOptArea() {
        mOptArea.setVisibility(View.GONE);
    }

    /**
     * 说明已经点了呼叫
     */
    private void showCanceling() {
        mTipsInfo.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.canceling));
        mBtnCancel.setEnabled(false);
    }

    /**
     * 已登录，直接呼叫
     */
    private void showCalling() {
        mTipsInfo.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.VISIBLE);
        mLoadingText.setText(getString(R.string.calling_driver));
        mBtnCancel.setEnabled(true);
        mBtnCancel.setEnabled(false);
    }

    @Override
    public void showLoading() {
        // TODO:  显示加载框

    }

    @Override
    public void showError(int Code, String msg) {
        switch (Code) {
            case IAccountManager.TOKEN_INVALID:
                // 登录过期
                ToastUtil.show(this, getString(R.string.token_invalid));
                showPhoneInputDialog();
                this.mIsLogin = false;
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                showPhoneInputDialog();
                break;
        }
    }

    @Override
    public void showLoginSuc() {
        ToastUtil.show(this, getString(R.string.login_suc));
        this.mIsLogin = true;
    }

    @Override
    public void showNears(List<LocationInfo> data) {

        for (LocationInfo locationInfo : data) {
            showLocationChange(locationInfo);
        }
    }

    @Override
    public void showLocationChange(LocationInfo locationInfo) {
        if (mDriverBit == null || mDriverBit.isRecycled()) {
            mDriverBit = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        }
        mLbsLayer.addOrUpdateMarker(locationInfo, mDriverBit);
    }

    @Override
    public void showCallDriverSuc(Order order) {
        mLoadingArea.setVisibility(View.GONE);
        mTipsInfo.setVisibility(View.VISIBLE);
        mTipsInfo.setText(getString(R.string.show_call_suc));
        // 显示操作区
        showOptArea();
        mBtnCallDriver.setEnabled(false);
        // 显示路径信息
        if (order.getEndLongitude() != 0 ||
                order.getDriverLatitude() != 0) {
            mEndLocation = new LocationInfo(order.getEndLatitude(), order.getEndLongitude());
            mEndLocation.setKey(LOCATION_END);
            // 绘制路径
            showRoute(mStartLocation, mEndLocation, new OnRouteCompleteListener() {
                @Override
                public void onComplete(RouteInfo result) {
                    LogUtil.e(TAG, "driverRoute: " + result);


                    mLbsLayer.moveCamera(mStartLocation, mEndLocation);
                    mCost = result.getTaxiCost();
                    String infoString = getString(R.string.route_info_calling);
                    infoString = String.format(infoString,
                            Float.valueOf(result.getDistance()).intValue(),
                            mCost,
                            result.getDuration());
                    mTipsInfo.setVisibility(View.VISIBLE);
                    mTipsInfo.setText(infoString);

                }
            });
        }
        LogUtil.e(TAG, "showCallDriverSuc: " + order);
    }

    @Override
    public void showCallDriverFail() {
        mLoadingArea.setVisibility(View.GONE);
        mTipsInfo.setVisibility(View.VISIBLE);
        mTipsInfo.setText(getString(R.string.show_call_fail));
        mBtnCallDriver.setEnabled(true);
    }

    @Override
    public void showCancelSuc() {
        ToastUtil.show(this, getString(R.string.order_cancel_suc));
        restoreUI();
    }

    @Override
    public void showCancelFail() {
        ToastUtil.show(this, getString(R.string.order_cancel_error));
        mBtnCancel.setEnabled(true);
    }

    @Override
    public void showDriverAcceptOrder(final Order order) {
// 提示信息
        ToastUtil.show(this, getString(R.string.driver_accept_order));

        // 清除地图标记
        mLbsLayer.clearAllMarkers();
        /*
         * 添加司机标记
         */

        final LocationInfo driverLocation =
                new LocationInfo(order.getDriverLatitude(),
                        order.getDriverLongitude());
        driverLocation.setKey(order.getKey());
        showLocationChange(driverLocation);
        // 显示我的位置
        addLocationMarker();
        /*
         * 显示司机到乘客的路径
         */
        mLbsLayer.driverRoute(driverLocation,
                mStartLocation,
                Color.BLUE,
                new OnRouteCompleteListener() {
                    @Override
                    public void onComplete(RouteInfo result) {
                        // 地图聚焦到司机和我的位置
                        mLbsLayer.moveCamera(mStartLocation, driverLocation);
                        // 显示司机、路径信息
                        String stringBuilder = "司机：" +
                                order.getDriverName() +
                                ", 车牌：" +
                                order.getCarNo() +
                                "，预计" +
                                result.getDuration() +
                                "分钟到达";


                        mTipsInfo.setText(stringBuilder);
                        // 显示操作区
                        showOptArea();
                        // 呼叫不可点击
                        mBtnCallDriver.setEnabled(false);

                    }
                });

    }

    @Override
    public void showDriverArriveStart(Order mCurrentOrder) {
        showOptArea();
        final String arriveTemp = getString(R.string.driver_arrive);
        mTipsInfo.setText(String.format(arriveTemp,
                mCurrentOrder.getDriverName(),
                mCurrentOrder.getCarNo()));
        mBtnCallDriver.setEnabled(false);
        mBtnCancel.setEnabled(true);
        // 清除地图标记
        mLbsLayer.clearAllMarkers();
        /*
         * 添加司机标记
         */
        final LocationInfo driverLocation =
                new LocationInfo(mCurrentOrder.getDriverLatitude(),
                        mCurrentOrder.getDriverLongitude());
        driverLocation.setKey(mCurrentOrder.getKey());
        showLocationChange(driverLocation);
        // 显示我的位置
        addLocationMarker();
    }

    @Override
    public void updateDriver2StartRoute(LocationInfo locationInfo, final Order order) {
        mLbsLayer.clearAllMarkers();
        addLocationMarker();
        showLocationChange(locationInfo);
        mLbsLayer.driverRoute(locationInfo, mStartLocation, Color.BLUE, new OnRouteCompleteListener() {
            @Override
            public void onComplete(RouteInfo result) {

                String tipsTemp = getString(R.string.accept_info);
                mTipsInfo.setText(String.format(tipsTemp,
                        order.getDriverName(),
                        order.getCarNo(),
                        result.getDistance(),
                        result.getDuration()));
            }
        });
        // 聚焦
        mLbsLayer.moveCamera(locationInfo, mStartLocation);
    }

    @Override
    public void showStartDrive(Order order) {
        final LocationInfo locationInfo =
                new LocationInfo(order.getDriverLatitude(), order.getDriverLongitude());
        locationInfo.setKey(order.getKey());
        // 路径规划绘制
        updateDriver2EndRoute(locationInfo, order);
        // 隐藏按钮
        mBtnCancel.setVisibility(View.GONE);
        mBtnCallDriver.setVisibility(View.GONE);
    }

    @Override
    public void showArriveEnd(Order order) {
        final String tipsTemp = getString(R.string.pay_info);
        final String tips = String.format(tipsTemp,
                order.getCost(),
                order.getDriverName(),
                order.getCarNo());
        // 显示操作区
        showOptArea();
        mBtnCancel.setVisibility(View.GONE);
        mBtnCallDriver.setVisibility(View.GONE);
        mTipsInfo.setText(tips);
        mBtnPay.setVisibility(View.VISIBLE);
    }

    /**
     * 终点位置从 order 中获取
     */
    @Override
    public void updateDriver2EndRoute(LocationInfo locationInfo, final Order order) {
        if (order.getEndLongitude() != 0 ||
                order.getEndLatitude() != 0) {
            mEndLocation = new LocationInfo(order.getEndLatitude(), order.getEndLongitude());
            mEndLocation.setKey(LOCATION_END);
        }
        mLbsLayer.clearAllMarkers();
        addEndMarker();
        showLocationChange(locationInfo);
        addLocationMarker();
        mLbsLayer.driverRoute(locationInfo, mEndLocation, Color.GREEN, new OnRouteCompleteListener() {
            @Override
            public void onComplete(RouteInfo result) {

                String tipsTemp = getString(R.string.driving_info);
                mTipsInfo.setText(String.format(tipsTemp,
                        order.getDriverName(),
                        order.getCarNo(),
                        result.getDistance(),
                        result.getDuration()));
                // 显示操作区
                showOptArea();
                mBtnCancel.setEnabled(false);
                mBtnCallDriver.setEnabled(false);
            }
        });
        // 聚焦
        mLbsLayer.moveCamera(locationInfo, mEndLocation);
    }

    @Override
    public void showPaySuc(Order mCurrentOrder) {
        restoreUI();
        ToastUtil.show(this, getString(R.string.pay_suc));
    }

    @Override
    public void showPayFail() {
        restoreUI();
        ToastUtil.show(this, getString(R.string.pay_fail));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onLocationChanged(LocationInfo locationInfo) {

    }

    @Override
    public void onLocation(LocationInfo locationInfo) {
        // 记录起点
        mStartLocation = locationInfo;
        // 设置标题
        mCity.setText(mLbsLayer.getCity());
        // 设置起点
        mStart.setText(locationInfo.getName());
        getNearDrivers(locationInfo.getLatitude(),
                locationInfo.getLongitude());
        updateLocationToServer(locationInfo);
        addLocationMarker();
        this.mIsLocate = true;
        getProcessingOrder();
    }

    /**
     * 首次定位，添加当前位置的标记
     */
    private void addLocationMarker() {
        if (mLocationBit == null || mLocationBit.isRecycled()) {
            mLocationBit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.navi_map_gps_locked);
        }
        mLbsLayer.addOrUpdateMarker(mStartLocation, mLocationBit);
    }

    /**
     * 上报当前位置
     */
    private void updateLocationToServer(LocationInfo locationInfo) {
        locationInfo.setKey(mPushKey);
        mPresenter.updateLocationToServer(locationInfo);
    }

    /**
     * 获取附近司机
     */
    private void getNearDrivers(double latitude, double longitude) {
        mPresenter.fetchNearDrivers(latitude, longitude);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //  关键搜索推荐地点
        mLbsLayer.poiSearch(s.toString(), new  OnSearchedListener() {
            @Override
            public void onSearched(List<LocationInfo> results) {
                // 更新列表
                updatePoiList(results);
            }

            @Override
            public void onError(int rCode) {

            }
        });
    }

    /**
     * 获取正在进行中的订单
     */
    public void getProcessingOrder() {
        // 满足： 已经登录、已经定位两个条件，
        // 执行 getProcessingOrder
        if (mIsLogin && mIsLocate) {
            mPresenter.getProcessingOrder();
        }

    }

    /**
     * 更新列表
     */
    @Override
    public void onSearched(List<LocationInfo> results) {
        updatePoiList(results);
    }

    private void updatePoiList(final List<LocationInfo> results) {
        final List<String> listString = new ArrayList<String>();
        for (int i = 0; i < results.size(); i++) {
            listString.add(results.get(i).getName());
        }
        if (mEndAdapter == null) {
            mEndAdapter = new PoiAdapter(getApplicationContext(), listString);
            mEnd.setAdapter(mEndAdapter);

        } else {

            mEndAdapter.setData(listString);
        }
        mEnd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ToastUtil.show(MainActivity.this, results.get(position).getName());
                DevUtil.closeInputMethod(MainActivity.this);
                //  记录终点
                mEndLocation = results.get(position);
                mEndLocation.setKey(LOCATION_END);
                // 绘制路径
                showRoute(mStartLocation, mEndLocation, new OnRouteCompleteListener() {
                    @SuppressLint("UseValueOf")
                    @Override
                    public void onComplete(RouteInfo result) {
                        LogUtil.e(TAG, "driverRoute: " + result);


                        mLbsLayer.moveCamera(mStartLocation, mEndLocation);
                        showOptArea();
                        mCost = result.getTaxiCost();
                        String infoString = getString(R.string.route_info);
                        infoString = String.format(infoString,
                                new Float(result.getDistance()).intValue(),
                                mCost,
                                result.getDuration());
                        mTipsInfo.setVisibility(View.VISIBLE);
                        mTipsInfo.setText(infoString);
                    }
                });
            }
        });
        mEndAdapter.notifyDataSetChanged();
    }

    /**
     * 显示操作区
     */
    private void showOptArea() {
        mOptArea.setVisibility(View.VISIBLE);
        mLoadingArea.setVisibility(View.GONE);
        mTipsInfo.setVisibility(View.VISIBLE);
        mBtnCallDriver.setEnabled(true);
        mBtnCancel.setEnabled(true);
        mBtnCancel.setVisibility(View.VISIBLE);
        mBtnCallDriver.setVisibility(View.VISIBLE);
        mBtnPay.setVisibility(View.GONE);
    }

    @Override
    public void onError(int rCode) {

    }

    private void showRoute(final LocationInfo mStartLocation,
                           final LocationInfo mEndLocation,
                           OnRouteCompleteListener listener) {

        mLbsLayer.clearAllMarkers();
        addStartMarker();
        addEndMarker();
        mLbsLayer.driverRoute(mStartLocation,
                mEndLocation,
                Color.GREEN,
                listener);
    }


    private void addStartMarker() {
        if (mStartBit == null || mStartBit.isRecycled()) {
            mStartBit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.start);
        }
        mLbsLayer.addOrUpdateMarker(mStartLocation, mStartBit);
    }

    private void addEndMarker() {
        if (mEndBit == null || mEndBit.isRecycled()) {
            mEndBit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.end);
        }
        mLbsLayer.addOrUpdateMarker(mEndLocation, mEndBit);
    }


}
