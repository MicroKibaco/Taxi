package com.github.microkibaco.taxi.account.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.account.presenter.ISmsCodeDialogPresenter;
import com.github.microkibaco.taxi.account.presenter.impl.SmsCodeDialogPresenterImpl;
import com.github.microkibaco.taxi.account.view.ISmsCodeDialogView;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.util.ToastUtil;
import com.github.microkibaco.taxi.main.view.MainActivity;


public class SmsCodeDialog extends Dialog implements ISmsCodeDialogView, View.OnClickListener, VerificationCodeInput.Listener {
    private static final String TAG = SmsCodeDialog.class.getSimpleName();

    private String mPhone;
    private AppCompatImageView mClose;
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
        mPresenter = new SmsCodeDialogPresenterImpl(this,
                TaxiApplication.getInstance().getAccountManager());
        this.mainActivity = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(TaxiApplication.getInstance()
                .getInflateLayoutRoot(getContext(), R.layout.dialog_smscode_input,
                        null));
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
        mClose = (AppCompatImageView) findViewById(R.id.close);
        mError.setVisibility(View.GONE);
        mPhoneTv.setText(String.format(getContext().getString(R.string.sending), mPhone));
    }


    private SmsCodeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(int Code, String msg) {
        mLoading.setVisibility(View.GONE);
        switch (Code) {

            case IAccountManager.SMS_SEND_FAIL:
                ToastUtil.show(getContext(),
                        getContext().getString(R.string.sms_send_fail));
                break;

            case IAccountManager.SMS_CHECK_FAIL:
                // 提示验证码错误
                mError.setVisibility(View.VISIBLE);
                mVerificationCodeInput.setEnabled(true);
                break;

            case IAccountManager.SERVER_FAIL:
                ToastUtil.show(getContext(),
                        getContext().getString(R.string.error_server));
                break;


        }
    }

    @Override
    public void showCountDownTimer() {
        mPhoneTv.setText(String.format(getContext()
                .getString(R.string.sms_code_send_phone), mPhone));
        mCountDownTimer.start();
        mBtnResend.setEnabled(false);
    }

    @Override
    public void showSmsCodeCheckState(boolean suc) {
        if (suc) {
            mError.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            mPresenter.requestCheckUserExit(mPhone);
        } else {
            // 提示验证码错误
            mError.setVisibility(View.VISIBLE);
            mVerificationCodeInput.setEnabled(true);
            mLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void showUserExist(boolean exist) {
        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        SmsCodeDialog.this.dismiss();
        if (exist) {
            // 用户存在, 进入登录
            final LoginDialog dialog =
                    new LoginDialog(mainActivity, mPhone);
            dialog.show();
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    SmsCodeDialog.this.dismiss();
                }
            });

        } else {
            // 用户不存在,进入注册
            final CreatePasswordDialog dialog =
                    new CreatePasswordDialog(mainActivity, mPhone);
            dialog.show();
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    SmsCodeDialog.this.dismiss();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                SmsCodeDialog.this.dismiss();
                break;

            case R.id.btn_resend:
                resend();
                break;
        }
    }

    private void resend() {
        mPhoneTv.setText(String.format(getContext().getString(R.string.sending), mPhone));
    }

    @Override
    public void onComplete(String code) {
        mPresenter.requestCheckSmsCode(mPhone, code);
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
