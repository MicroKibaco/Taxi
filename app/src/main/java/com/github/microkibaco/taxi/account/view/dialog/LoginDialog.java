package com.github.microkibaco.taxi.account.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.presenter.ILoginDialogPresenter;
import com.github.microkibaco.taxi.account.presenter.impl.LoginDialogPresenterImpl;
import com.github.microkibaco.taxi.account.view.ILoginView;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.view.MainActivity;

/**
 * 登录框
 */
public class LoginDialog extends Dialog implements ILoginView,
        View.OnClickListener {

    private static final String TAG = LoginDialog.class.getSimpleName();

    private ContentLoadingProgressBar mLoading;
    private AppCompatTextView mPhone;
    private AppCompatEditText mConfirmPw;
    private AppCompatButton mBtnConfirm;
    private AppCompatImageView mClose;
    private AppCompatTextView mErrorTips;

    private String mPhoneStr;
    private ILoginDialogPresenter mPresenter;
    private MainActivity mainActivity;

    public LoginDialog(@NonNull MainActivity context, @NonNull String phone) {
        this(context, R.style.Dialog);
        this.mPhoneStr = phone;
        this.mainActivity = context;
        mPresenter = new LoginDialogPresenterImpl(this,
                TaxiApplication.getInstance().getAccountManager());
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(TaxiApplication.getInstance()
                .getInflateLayoutRoot(getContext(), R.layout.dialog_login_input,
                        null));
        initView();
        initListener();
        mPhone.setText(mPhoneStr);
    }


    /**
     * 处理登录成功 UI
     */
    @Override
    public void showLoginSuc() {
        showOrHideLoading(false);
        mErrorTips.setVisibility(View.VISIBLE);
        mErrorTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        mErrorTips.setText(getContext().getString(R.string.login_suc));
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
        mainActivity.showLoginSuc();
        LoginDialog.this.dismiss();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.close:
                LoginDialog.this.dismiss();
                break;
            case R.id.btn_confirm:
                submit();
                break;
        }

    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void showLoading() {
        showOrHideLoading(true);
    }

    @Override
    public void showError(int Code, String msg) {

        switch (Code) {
            case IAccountManager.PW_ERROR:
                // 密码错误
                showPasswordError(msg);
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
                showServerError(msg);
                break;
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 注销 Presenter
        RxBus.getInstance().unRegister(mPresenter);
    }


    private void initView() {
        mPhone = (AppCompatTextView) findViewById(R.id.phone);
        mConfirmPw = (AppCompatEditText) findViewById(R.id.password);
        mBtnConfirm = (AppCompatButton) findViewById(R.id.btn_confirm);
        mClose = (AppCompatImageView) findViewById(R.id.close);
        mLoading = (ContentLoadingProgressBar) findViewById(R.id.loading);
        mErrorTips = (AppCompatTextView) findViewById(R.id.tips);

    }

    private void initListener() {
        // 注册 Presenter
        RxBus.getInstance().register(mPresenter);
        mClose.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }


    /**
     * 显示服服务器出错
     */
    private void showServerError(String msg) {
        showOrHideLoading(false);
        mErrorTips.setVisibility(View.VISIBLE);
        mErrorTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        mErrorTips.setText(getContext().getString(R.string.login_suc));
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
        mainActivity.showLoginSuc();
        LoginDialog.this.dismiss();
    }

    /**
     * 密码错误
     */
    private void showPasswordError(String msg) {
        showOrHideLoading(false);
        mErrorTips.setVisibility(View.VISIBLE);
        mErrorTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mErrorTips.setText(getContext().getString(R.string.password_error));
    }


    private void showOrHideLoading(boolean show) {
        if (show) {
            mLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 提交登录
     */
    private void submit() {
        final String password = mConfirmPw.getText().toString();
        //  网络请求登录
        mPresenter.requestLogin(mPhoneStr, password);
    }


}
