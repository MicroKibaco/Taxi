package com.github.microkibaco.taxi;

import android.app.Application;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.microkibaco.taxi.account.model.AccountManagerImpl;
import com.github.microkibaco.taxi.account.model.IAccountManager;
import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.impl.OkHttpClientImpl;
import com.github.microkibaco.taxi.common.storage.SharedPreferencesDao;


public class TaxiApplication extends Application {
    private static TaxiApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static TaxiApplication getInstance() {
        return instance;
    }

    public IAccountManager getAccountManager() {
        final IHttpClient httpClient = new OkHttpClientImpl();
        final SharedPreferencesDao dao =
                new SharedPreferencesDao(instance,
                        SharedPreferencesDao.FILE_ACCOUNT);
        return new AccountManagerImpl(httpClient, dao);
    }


    public View getInflateLayoutRoot(Context context, @LayoutRes int resource, @Nullable ViewGroup root) {
        final LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resource, root);
    }
}
