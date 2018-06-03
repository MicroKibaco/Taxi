package com.github.microkibaco.taxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.github.microkibaco.taxi.main.MainActivity;
import com.github.microkibaco.taxi.R;

import butterknife.Bind;


public class SmsCodeDialog extends Dialog implements ISmsCodeDialogView {
    private static final String TAG = "SmsCodeDialog";

    private String mPhone;
    @Bind(R.id.close)
    AppCompatImageView mClose;
    @Bind(R.id.dialog_title)
    AppCompatTextView mDialogTitle;
    @Bind(R.id.phone)
    AppCompatTextView mPhoneTv;
    @Bind(R.id.btn_resend)
    AppCompatButton mBtnResend;
    @Bind(R.id.verificationCodeInput)
    VerificationCodeInput mVerificationCodeInput;
    @Bind(R.id.loading)
    ContentLoadingProgressBar mLoading;
    @Bind(R.id.error)
    AppCompatTextView mError;

    public SmsCodeDialog(MainActivity context, String phone) {
        this(context, R.style.Dialog);
        // 上一个界面传来的手机号码


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_smscode_input, null);
        setContentView(root);
        final String template = getContext().getString(R.string.sending);
        mPhoneTv.setText(String.format(template, mPhone));

    }

    public SmsCodeDialog(@NonNull Context context) {
        super(context);
    }

    public SmsCodeDialog(@NonNull Context context, @StyleRes int themeResId) {
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

    }

    @Override
    public void showSmsCodeCheckState(boolean b) {

    }

    @Override
    public void showUserExist(boolean b) {

    }
}
