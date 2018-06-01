package com.github.microkibaco.taxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.github.microkibaco.taxi.R;
import com.github.microkibaco.taxi.common.util.FormatUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 手机输入对话框
 */

public class PhoneInputDialog extends Dialog implements TextWatcher {

    @Bind(R.id.close)
    AppCompatImageView mClose;
    @Bind(R.id.dialog_title)
    AppCompatTextView mDialogTitle;
    @Bind(R.id.phone)
    AppCompatEditText mPhone;
    @Bind(R.id.btn_next)
    AppCompatButton mButton;
    private View mRoot;

    public PhoneInputDialog(@NonNull Context context) {
        this(context, R.style.Dialog);
    }

    public PhoneInputDialog(@NonNull Context context, @StyleRes int themeResId) {

        super(context, themeResId);
    }

    protected PhoneInputDialog(@NonNull Context context, boolean cancelable,
                               @Nullable OnCancelListener cancelListener) {

        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.dialog_phone_input, null);
        mButton.setEnabled(false);
        mPhone.addTextChangedListener(this);
        setContentView(mRoot);
    }


    @OnClick({R.id.close, R.id.phone, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                PhoneInputDialog.this.dismiss();
                break;
            case R.id.phone:
                break;
            case R.id.btn_next:
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

    private void check() {
        String phone =  mPhone.getText().toString();
        boolean legal = FormatUtil.checkMobile(phone);
        mButton.setEnabled(legal);
    }
}
