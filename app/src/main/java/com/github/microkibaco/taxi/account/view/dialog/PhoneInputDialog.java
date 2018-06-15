package com.github.microkibaco.taxi.account.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.common.util.FormatUtil;
import com.github.microkibaco.taxi.main.view.MainActivity;

import static com.github.microkibaco.taxi.R.id.btn_next;

/**
 * 手机输入对话框
 */

public class PhoneInputDialog extends Dialog implements View.OnClickListener,
        TextWatcher, DialogInterface.OnDismissListener {

    private AppCompatEditText mPhone;
    private AppCompatButton mButton;
    private AppCompatImageView mClose;
    private MainActivity mainActivity;

    public PhoneInputDialog(MainActivity mainActivity) {
        this(mainActivity, R.style.Dialog);
        this.mainActivity = mainActivity;
    }

    private PhoneInputDialog(Context context, int theme) {
        super(context, theme);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(TaxiApplication.getInstance()
                .getInflateLayoutRoot(getContext(), R.layout.dialog_phone_input,
                        null));
        initView();
        initListener();
    }

    private void initView() {
        mButton = (AppCompatButton) findViewById(btn_next);
        mPhone = (AppCompatEditText) findViewById(R.id.phone);
        mClose = (AppCompatImageView) findViewById(R.id.close);
        mButton.setEnabled(false);
    }

    private void initListener() {

        //  手机号输入框组册监听检查手机号输入是否合法
        mPhone.addTextChangedListener(this);

        // 按钮注册监听
        mButton.setOnClickListener(this);

        // 关闭按钮注册监听事件
        mClose.setOnClickListener(this);
    }

    private void check() {
        final String phone = mPhone.getText().toString();
        final boolean legal = FormatUtil.checkMobile(phone);
        mButton.setEnabled(legal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                PhoneInputDialog.this.dismiss();
                break;
            case btn_next:
                final String phone = mPhone.getText().toString();
                final SmsCodeDialog dialog = new SmsCodeDialog(mainActivity, phone);
                dialog.show();
                dialog.setOnDismissListener(this);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        check();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        PhoneInputDialog.this.dismiss();
    }
}
