package com.github.microkibaco.taxi.account.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.AccountManagerImpl;
import com.github.microkibaco.taxi.account.presenter.ICreatePasswordDialogPresenter;
import com.github.microkibaco.taxi.account.presenter.impl.CreatePasswordDialogPresenterImpl;
import com.github.microkibaco.taxi.account.view.ICreatePasswordDialogView;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.impl.OkHttpClientImpl;
import com.github.microkibaco.taxi.common.storage.SharedPreferencesDao;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.view.MainActivity;

/**
 * 密码创建/修改
 */
public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView, View.OnClickListener {

    private AppCompatTextView mPhone;
    private AppCompatEditText mPw;
    private AppCompatEditText mConfirmPw;
    private AppCompatButton mBtnConfirm;
    private ContentLoadingProgressBar mLoading;
    private AppCompatTextView mErrorTips;
    private AppCompatImageView mClose;

    private Context mContext;
    private String mPhoneStr;
    private ICreatePasswordDialogPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View root = inflater.inflate(R.layout.dialog_create_pw, null);
        setContentView(root);

        initView();
        initListener();


    }


    public CreatePasswordDialog(@NonNull Activity context, String phone) {
        this(context, R.style.Dialog);
        // 上一个页面传来的机号码
        this.mContext = context;
        this.mPhoneStr = phone;
        final IHttpClient httpClient = new OkHttpClientImpl();
        final SharedPreferencesDao dao =
                new SharedPreferencesDao(TaxiApplication.getInstance(),
                        SharedPreferencesDao.FILE_ACCOUNT);
        final AccountManagerImpl manager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new CreatePasswordDialogPresenterImpl(this, manager);
    }

    public CreatePasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private void initListener() {
        mClose.setOnClickListener(this);
        //注册 presenter
        RxBus.getInstance().register(mPresenter);
    }

    private void initView() {
        mPhone = (AppCompatTextView) findViewById(R.id.phone);
        mPw = (AppCompatEditText) findViewById(R.id.pw);
        mConfirmPw = (AppCompatEditText) findViewById(R.id.pw1);
        mBtnConfirm = (AppCompatButton) findViewById(R.id.btn_confirm);
        mLoading = (ContentLoadingProgressBar) findViewById(R.id.loading);
        mErrorTips = (AppCompatTextView) findViewById(R.id.tips);
        mClose = (AppCompatImageView) findViewById(R.id.close);
        mPhone.setText(mPhoneStr);
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

    }

    @Override
    public void showError(int Code, String msg) {

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
        dismiss();
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
                dismiss();
                break;
            case R.id.btn_confirm:
                submit();
                break;
        }
    }


}
