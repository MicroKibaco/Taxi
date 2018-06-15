package com.github.microkibaco.taxi.account.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ProgressBar;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.presenter.ICreatePasswordDialogPresenter;
import com.github.microkibaco.taxi.account.presenter.impl.CreatePasswordDialogPresenterImpl;
import com.github.microkibaco.taxi.account.view.ICreatePasswordDialogView;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.view.MainActivity;

/**
 * 密码创建/修改
 * todo: Fix there is an error:ReferenceError: objectId is not defined
 */
public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView,
        View.OnClickListener {

    private AppCompatTextView mPhone;
    private AppCompatEditText mPw;
    private AppCompatEditText mConfirmPw;
    private AppCompatButton mBtnConfirm;
    private ProgressBar mLoading;
    private AppCompatTextView mErrorTips;
    private AppCompatImageView mClose;

    private Activity mContext;
    private String mPhoneStr;
    private ICreatePasswordDialogPresenter mPresenter;
    private static final String TAG = CreatePasswordDialog.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(TaxiApplication.getInstance()
                .getInflateLayoutRoot(getContext(), R.layout.dialog_create_pw,
                        null));
        initView();
        initListener();


    }


    CreatePasswordDialog(@NonNull Activity context, String phone) {
        this(context, R.style.Dialog);
        // 上一个页面传来的机号码
        this.mContext = context;
        this.mPhoneStr = phone;
        mPresenter = new CreatePasswordDialogPresenterImpl(this,
                TaxiApplication.getInstance().getAccountManager());
    }

    private CreatePasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private void initListener() {
        mClose.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        //注册 presenter
        RxBus.getInstance().register(mPresenter);
    }

    private void initView() {
        mPhone = (AppCompatTextView) findViewById(R.id.phone);
        mPw = (AppCompatEditText) findViewById(R.id.pw);
        mConfirmPw = (AppCompatEditText) findViewById(R.id.pw1);
        mBtnConfirm = (AppCompatButton) findViewById(R.id.btn_confirm);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mErrorTips = (AppCompatTextView) findViewById(R.id.tips);
        mClose = (AppCompatImageView) findViewById(R.id.close);
        mPhone.setText(mPhoneStr);
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
     * 提交注册
     */
    private void submit() {
        final String password = mPw.getText().toString();
        final String confirmPassword = mConfirmPw.getText().toString();
        final boolean checkPw = mPresenter.checkPw(password, confirmPassword);
        if (checkPw) {
            mPresenter.requestRegister(mPhoneStr, confirmPassword);
        }
    }

    @Override
    public void showLoading() {
        showOrHideLoading(true);
    }

    @Override
    public void showError(int Code, String msg) {
        showOrHideLoading(false);
        switch (Code) {
            case IAccountManager.PW_ERROR:
                showLoginFail(msg);
                break;
            case IAccountManager.SERVER_FAIL:
                showServerError(msg);
                break;
        }
    }

    private void showLoginFail(String msg) {
        CreatePasswordDialog.this.dismiss();
        ToastUtil.show(getContext(), msg);
    }

    private void showServerError(String msg) {
        mErrorTips.setTextColor(getContext()
                .getResources().getColor(R.color.error_red));
        mErrorTips.setText(msg);
    }

    @Override
    public void showRegisterSuc() {
        mLoading.setVisibility(View.VISIBLE);
        mBtnConfirm.setVisibility(View.GONE);
        mErrorTips.setVisibility(View.VISIBLE);
        mErrorTips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.color_text_normal));
        mErrorTips.setText(getContext()
                .getString(R.string.register_suc_and_loging));
        // 请求网络,完成自动登录
        mPresenter.requestLogin(mPhoneStr, mConfirmPw.getText().toString());
    }

    @Override
    public void showLoginSuc() {
        CreatePasswordDialog.this.dismiss();
        ToastUtil.show(getContext(),
                getContext().getString(R.string.login_suc));
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).showLoginSuc();
        }
    }

    @Override
    public void showPasswordNull() {
        mErrorTips.setVisibility(View.VISIBLE);
        mErrorTips.setText(getContext().getString(R.string.password_is_null));
        mErrorTips.setTextColor(getContext().
                getResources().getColor(R.color.error_red));
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public void showPasswordNotEqual() {
        mErrorTips.setVisibility(View.VISIBLE);
        mErrorTips.setText(getContext()
                .getString(R.string.password_is_not_equal));
        mErrorTips.setTextColor(getContext()
                .getResources().getColor(R.color.error_red));
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 注销 presenter
        RxBus.getInstance().unRegister(mPresenter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                CreatePasswordDialog.this.dismiss();
                break;
            case R.id.btn_confirm:
                submit();
                break;
        }
    }


}
