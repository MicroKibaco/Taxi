package com.github.microkibaco.taxi.main.view;

import android.content.DialogInterface;
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
import android.widget.RelativeLayout;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.view.dialog.PhoneInputDialog;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.api.API;
import com.github.microkibaco.taxi.common.http.impl.OkHttpClientImpl;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;
import com.github.microkibaco.taxi.common.lbs.presenter.AmapLbsLayerImpl;
import com.github.microkibaco.taxi.common.lbs.view.CommonLocationChangeListener;
import com.github.microkibaco.taxi.common.lbs.view.ILbsLayer;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.model.IMainManager;
import com.github.microkibaco.taxi.main.model.MainMangerImpl;
import com.github.microkibaco.taxi.main.model.bean.Order;
import com.github.microkibaco.taxi.main.presenter.IMainPresenter;
import com.github.microkibaco.taxi.main.presenter.MainPresenterImpl;

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
        IMainView, CommonLocationChangeListener, TextWatcher {

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

    // 记录起点和终点
    private LocationInfo mStartLocation;
    private LocationInfo mEndLocation;
    private boolean mIsLocate;


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

    @OnClick({R.id.start, R.id.end, R.id.btn_call_driver, R.id.btn_cancel, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start:
                break;
            case R.id.end:
                break;
            case R.id.btn_call_driver:
                break;
            case R.id.btn_cancel:
                break;
            case R.id.btn_pay:
                break;
        }
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
                mIsLogin = false;
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

    }

    @Override
    public void showLocationChange(LocationInfo locationInfo) {

    }

    @Override
    public void showCallDriverSuc(Order order) {

    }

    @Override
    public void showCallDriverFail() {

    }

    @Override
    public void showCancelSuc() {

    }

    @Override
    public void showCancelFail() {

    }

    @Override
    public void showDriverAcceptOrder(Order mCurrentOrder) {

    }

    @Override
    public void showDriverArriveStart(Order mCurrentOrder) {

    }

    @Override
    public void updateDriver2StartRoute(LocationInfo locationInfo, Order order) {

    }

    @Override
    public void showStartDrive(Order order) {

    }

    @Override
    public void showArriveEnd(Order order) {

    }

    @Override
    public void updateDriver2EndRoute(LocationInfo locationInfo, Order order) {

    }

    @Override
    public void showPaySuc(Order mCurrentOrder) {

    }

    @Override
    public void showPayFail() {

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
        mIsLocate = true;
        getProcessingOrder();
    }

    /**
     * 首次定位，添加当前位置的标记
     */
    private void addLocationMarker() {

    }

    /**
     * 上报当前位置
     */
    private void updateLocationToServer(LocationInfo locationInfo) {

    }

    /**
     * 获取附近司机
     */
    private void getNearDrivers(double latitude, double longitude) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 获取正在进行中的订单
     */
    public void getProcessingOrder() {
    }
}
