package com.github.microkibaco.taxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.AccountManagerImpl;
import com.github.microkibaco.taxi.account.presenter.ISmsCodeDialogPresenter;
import com.github.microkibaco.taxi.account.presenter.SmsCodeDialogPresenterImpl;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.impl.OkHttpClientImpl;
import com.github.microkibaco.taxi.common.storage.SharedPreferencesDao;
import com.github.microkibaco.taxi.main.MainActivity;


public class SmsCodeDialog extends Dialog implements ISmsCodeDialogView, View.OnClickListener, VerificationCodeInput.Listener {
    private static final String TAG = SmsCodeDialog.class.getSimpleName();

    private String mPhone;
    private AppCompatImageView mClose;
    private AppCompatTextView mDialogTitle;
    private AppCompatTextView mPhoneTv;
    private AppCompatButton mBtnResend;
    private VerificationCodeInput mVerificationCodeInput;
    private ContentLoadingProgressBar mLoading;
    private AppCompatTextView mError;

    private ISmsCodeDialogPresenter mPresenter;
    private MainActivity mainActivity;

    SmsCodeDialog(MainActivity context, String phone) {
        this(context, R.style.Dialog);
        // 上一个界面传来的手机号码
        this.mPhone = phone;
        final IHttpClient httpClient = new OkHttpClientImpl();
        final SharedPreferencesDao dao =
                new SharedPreferencesDao(TaxiApplication.getInstance(),
                        SharedPreferencesDao.FILE_ACCOUNT);
        final AccountManagerImpl iAccountManager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new SmsCodeDialogPresenterImpl(this, iAccountManager);
        this.mainActivity = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View root = inflater.inflate(R.layout.dialog_smscode_input, null);
        setContentView(root);
        initView();
        initListener();


    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();
    }

    private void initListener() {
        // 注册 Presenter
        RxBus.getInstance().register(mPresenter);

        // 关闭按钮组件监听器
        mClose.setOnClickListener(this);

        // 验证码输入完成监听
        mVerificationCodeInput.setOnCompleteListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 注销 Presenter
        RxBus.getInstance().unRegister(mPresenter);
    }

    private void initView() {
        mPhoneTv = (AppCompatTextView) findViewById(R.id.phone);
        mBtnResend = (AppCompatButton) findViewById(R.id.btn_resend);
        mVerificationCodeInput = (VerificationCodeInput) findViewById(R.id.verificationCodeInput);
        mLoading = (ContentLoadingProgressBar) findViewById(R.id.loading);
        mError = (AppCompatTextView) findViewById(R.id.error);
        mError.setVisibility(View.GONE);
        mPhoneTv.setText(String.format(getContext().getString(R.string.sending), mPhone));
        mClose = (AppCompatImageView) findViewById(R.id.close);
    }

    public SmsCodeDialog(@NonNull Context context) {
        super(context);
    }

    private SmsCodeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected SmsCodeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(int Code, String msg) {

    }

    @Override
    public void showCountDownTimer() {
        mPhoneTv.setText(String.format(getContext()
                .getString(R.string.sms_code_send_phone), mPhone));
        mCountDownTimer.start();
        mBtnResend.setEnabled(false);
    }

    @Override
    public void showSmsCodeCheckState(boolean b) {

    }

    @Override
    public void showUserExist(boolean b) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                dismiss();
                break;

            case R.id.btn_resend:
                resend();
                break;
        }
    }

    private void resend() {

    }

    @Override
    public void onComplete(String s) {

    }

    private CountDownTimer mCountDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mBtnResend.setEnabled(false);
            mBtnResend.setText(String.format(getContext()
                    .getString(R.string.after_time_resend,
                            millisUntilFinished / 1000)));
        }

        @Override
        public void onFinish() {
            mBtnResend.setEnabled(true);
            mBtnResend.setText(getContext().getString(R.string.resend));
            cancel();
        }
    };
}
