package com.github.microkibaco.taxi.main.model;

import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;



public class MainMangerImpl implements IMainManager {
    private static final String TAG = MainMangerImpl.class.getSimpleName();

    private IHttpClient mHttpClient;

    public MainMangerImpl(IHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }


    @Override
    public void fetchNearDrivers(double latitude, double longitude) {

    }

    @Override
    public void updateLocationToServer(LocationInfo locationInfo) {

    }

    @Override
    public void callDriver(String key, float cost, LocationInfo startLocation, LocationInfo endLocation) {

    }

    @Override
    public void cancelOrder(String orderId) {

    }

    @Override
    public void pay(String orderId) {

    }

    @Override
    public void getProcessingOrder() {

    }
}
