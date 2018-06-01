package com.github.microkibaco.taxi.common.http.impl;

import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.IRequest;
import com.github.microkibaco.taxi.common.http.IResponse;
import com.github.microkibaco.taxi.common.http.api.API;

import org.junit.Before;
import org.junit.Test;

public class OkHttpClientImplTest {

    private IHttpClient mIHttpClient;

    @Before
    public void setUp() throws Exception {
        mIHttpClient = new OkHttpClientImpl();
        API.Config.setDebug(false);
    }

    @Test
    public void get() throws Exception {
        // request 参数
        final String url = API.Config.getDomain() + API.TEST_GET;
        final IRequest request = new BaseRequest(url);
        request.setBody("uid", "123456");
        request.setHeader("testHeader", "test header");
        final IResponse response = mIHttpClient.get(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("body = " + response.getData());
    }

    @Test
    public void post() throws Exception {
        // request 参数
        final String url = API.Config.getDomain() + API.TEST_POST;
        final IRequest request = new BaseRequest(url);
        request.setBody("uid", "123456");
        request.setHeader("testHeader", "test header");
        final IResponse response = mIHttpClient.post(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("body = " + response.getData());
    }

}