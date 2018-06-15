package com.github.microkibaco.taxi.main.model;

import com.google.gson.Gson;

import com.github.microkibaco.taxi.TaxiApplication;
import com.github.microkibaco.taxi.account.model.response.Account;
import com.github.microkibaco.taxi.common.databus.RxBus;
import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.IRequest;
import com.github.microkibaco.taxi.common.http.IResponse;
import com.github.microkibaco.taxi.common.http.api.API;
import com.github.microkibaco.taxi.common.http.biz.BaseBizResponse;
import com.github.microkibaco.taxi.common.http.impl.BaseRequest;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;
import com.github.microkibaco.taxi.common.storage.SharedPreferencesDao;
import com.github.microkibaco.taxi.common.util.LogUtil;
import com.github.microkibaco.taxi.main.model.response.NearDriversResponse;
import com.github.microkibaco.taxi.main.model.response.OrderStateOptResponse;

import rx.functions.Func1;


@SuppressWarnings("UnnecessaryLocalVariable")
public class MainMangerImpl implements IMainManager {
    private static final String TAG = MainMangerImpl.class.getSimpleName();

    private IHttpClient mHttpClient;

    public MainMangerImpl(IHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }


    @Override
    public void fetchNearDrivers(final double latitude, final double longitude) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.GET_NEAR_DRIVERS);
                request.setBody("latitude", Double.valueOf(latitude).toString());
                request.setBody("longitude", Double.valueOf(longitude).toString());
                IResponse response = mHttpClient.get(request, false);
                if (response.getCode() == BaseBizResponse.STATE_OK) {

                    try {
                        final NearDriversResponse nearDriversResponse =
                                new Gson().fromJson(response.getData(),
                                        NearDriversResponse.class);

                        return nearDriversResponse;
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void updateLocationToServer(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.UPLOAD_LOCATION);
                request.setBody("latitude",
                        Double.valueOf(locationInfo.getLatitude()).toString());
                request.setBody("longitude",
                        Double.valueOf(locationInfo.getLongitude()).toString());
                request.setBody("key", locationInfo.getKey());
                request.setBody("rotation",
                        Float.valueOf(locationInfo.getRotation()).toString());
                IResponse response = mHttpClient.post(request, false);
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    LogUtil.e(TAG, "位置上报成功");
                } else {
                    LogUtil.e(TAG, "位置上报失败");
                }
                return null;
            }
        });
    }

    @Override
    public void callDriver(final String key,
                           final float cost,
                           final LocationInfo startLocation,
                           final LocationInfo endLocation) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                /*
                 *  获取 uid,phone
                 */

                SharedPreferencesDao sharedPreferencesDao =
                        new SharedPreferencesDao(TaxiApplication.getInstance(),
                                SharedPreferencesDao.FILE_ACCOUNT);
                Account account =
                        (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                                Account.class);
                String uid = account.getUid();
                String phone = account.getAccount();
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.CALL_DRIVER);
                request.setBody("key", key);
                request.setBody("uid",uid);
                request.setBody("phone", phone);
                request.setBody("startLatitude",
                        Double.valueOf(startLocation.getLatitude()).toString() );
                request.setBody("startLongitude",
                        Double.valueOf(startLocation.getLongitude()).toString() );
                request.setBody("endLatitude",
                        Double.valueOf(endLocation.getLatitude()).toString() );
                request.setBody("endLongitude",
                        Double.valueOf(endLocation.getLongitude()).toString() );
                request.setBody("cost", Float.valueOf(cost).toString());

                IResponse response = mHttpClient.post(request, false);
                OrderStateOptResponse orderStateOptResponse =
                        new OrderStateOptResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    // 解析订单信息
                    orderStateOptResponse =
                            new Gson().fromJson(response.getData(),
                                    OrderStateOptResponse.class);
                }

                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CREATE);
                LogUtil.e(TAG, "call driver: " + response.getData());
                return orderStateOptResponse;
            }
        });
    }

    @Override
    public void cancelOrder(final String orderId) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.CANCEL_ORDER);
                request.setBody("id", orderId);

                IResponse response = mHttpClient.post(request, false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CANCEL);

                LogUtil.e(TAG, "cancel order: " + response.getData());
                return orderStateOptResponse;
            }
        });
    }

    @Override
    public void pay(final String orderId) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.PAY);
                request.setBody("id", orderId);

                IResponse response = mHttpClient.post(request, false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.PAY);

                LogUtil.e(TAG, "pay order: " + response.getData());
                return orderStateOptResponse;
            }
        });
    }

    @Override
    public void getProcessingOrder() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                /*
                 * 获取 uid
                 */

                SharedPreferencesDao sharedPreferencesDao =
                        new SharedPreferencesDao(TaxiApplication.getInstance(),
                                SharedPreferencesDao.FILE_ACCOUNT);
                Account account =
                        (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                                Account.class);
                String uid = account.getUid();
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.GET_PROCESSING_ORDER);
                request.setBody("uid", uid);

                IResponse response = mHttpClient.get(request, false);
                LogUtil.e(TAG, "getProcessingOrder order: " + response.getData());
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    /*
                     * 解析订单数据，封装到 OrderStateOptResponse
                     */
                    OrderStateOptResponse orderStateOptResponse =
                            new Gson().fromJson(response.getData(), OrderStateOptResponse.class);
                    if (orderStateOptResponse.getCode() == BaseBizResponse.STATE_OK) {
                        orderStateOptResponse.setState(orderStateOptResponse.getData().getState());
                        LogUtil.e(TAG, "getProcessingOrder order state=" + orderStateOptResponse.getState());
                        return orderStateOptResponse;
                    }

                }
                return null;
            }
        });
    }
}
