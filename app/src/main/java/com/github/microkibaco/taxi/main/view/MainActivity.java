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
import android.view.View;
import android.widget.RelativeLayout;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.view.dialog.PhoneInputDialog;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.presenter.IMainPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 1. 检查本地记录(登录状态检查)
 * 2. 若用户没有登录则登录
 * 3. 登录之前先校验手机号码
 * 4. todo: 地图初始化
 */
public class MainActivity extends AppCompatActivity implements
        IMainView {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLoginState();
    }


    /**
     * 检查用户是否登录
     */
    private void checkLoginState() {
        // todo: 获取用户登录信息

        // 登录是否过期
        boolean tokenValid = false;

        // todo: 检查token是否过期

        if (!tokenValid) {

            showPhoneInputDialog();

        } else {

            // todo: 请求网络,完成自动登录

        }


    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
