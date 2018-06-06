package com.github.microkibaco.taxi.common.http.impl;

import com.google.gson.Gson;

import com.github.microkibaco.taxi.common.http.IRequest;
import com.github.microkibaco.taxi.common.http.api.API;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装参数的实现
 */
public class BaseRequest implements IRequest {

    private String method = POST;
    private String url;
    private final Map<String, String> header;
    private final Map<String, String> body;

    /**
     * 公共参数及头部信息
     */
    public BaseRequest(String url) {
        this.url = url;
        this.header = new HashMap<>();
        this.body = new HashMap<>();
        header.put("X-Bmob-Application-Id", API.Config.getAppId());
        header.put("X-Bmob-REST-API-Key", API.Config.getAppKey());
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    @Override
    public void setBody(String key, String value) {
        body.put(key, value);
    }

    @Override
    public String getUrl() {

        if (GET.equals(method)) {
            // 组装 Get 请求参数
            for (String key : body.keySet()) {
                url = url.replace("${" + key + "}", body.get(key).toString());
            }
        }
        return url;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public Object getBody() {
        if (body != null) {
            // 组装 POST 方法请求参数
            return new Gson().toJson(this.body, HashMap.class);
        } else {
            return "{}";
        }
    }
}
