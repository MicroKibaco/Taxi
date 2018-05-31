package com.github.microkibaco.taxi.common.http.impl;

import com.github.microkibaco.taxi.common.http.IHttpClient;
import com.github.microkibaco.taxi.common.http.IRequest;
import com.github.microkibaco.taxi.common.http.IResponse;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientImpl implements IHttpClient {

    private final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .build();

    @Override
    public IResponse get(IRequest request, boolean forceCache) {
        final Map<String, String> header = request.getHeader(); // 解析头部
        // OkHttp 的 Request.Builder
        Request.Builder builder = new Request.Builder();
        for (String key : header.keySet()) {
            // 组装成 OkHttp 的 Header
            builder.header(key, header.get(key));
        }
        // 获取 url
        String url = request.getUrl();
        builder.url(url)
                .get();
        final Request oKRequest = builder.build();
        // 执行 oKRequest
        return execute(oKRequest);
    }

    /**
     * 请求执行过程
     */
    private IResponse execute(Request request) {
        final BaseResponse commonResponse = new BaseResponse();
        try {
            final Response response = mOkHttpClient.newCall(request).execute();

            // 设置状态码
            commonResponse.setCode(response.code());

            // 设置响应的数据体
            final String body = response.body().string();
            commonResponse.setData(body);
        } catch (IOException e) {
            e.printStackTrace();
            commonResponse.setCode(commonResponse.STATE_UNKNOWN_ERROR);
            commonResponse.setData(e.getMessage());
        }
        return commonResponse;
    }

    @Override
    public IResponse post(IRequest request, boolean forceCache) {
        return null;
    }
}
