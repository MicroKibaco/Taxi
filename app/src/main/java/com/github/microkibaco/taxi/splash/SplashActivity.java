package com.github.microkibaco.taxi.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.github.microkibaco.taxi.MainActivity;
import com.github.microkibaco.taxi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.iv_logo)
    AppCompatImageView mLogo;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AnimatedVectorDrawable anim = (AnimatedVectorDrawable) getResources()
                    .getDrawable(R.drawable.anim);
            mLogo.setImageDrawable(anim);
            anim.start();
        }

        /*
         * 延时 3 秒然后跳转到 main 页面
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, 3000);
    }
}
