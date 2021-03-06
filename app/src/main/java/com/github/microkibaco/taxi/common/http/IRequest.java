package com.github.microkibaco.taxi.common.http;

import java.util.Map;


/**
 * 定义请求数据的封装方式
 */

public interface IRequest {

    public static final String POST = "POST";
    public static final String GET = "GET";

    /**
     * 请求定义方式
     */
    void setMethod(String method);

    /**
     * 指定请求头部
     */
    void setHeader(String key, String value);

    /**
     * 指定请求体
     */
    void setBody(String key, String value);

    /**
     * 提供给执行库请求行URL
     */
    String getUrl();

    /**
     * 提供给执行库请求行头部
     */
    Map<String, String> getHeader();

    /**
     * 提供给执行库请求行参数
     */
    Object getBody();

}
