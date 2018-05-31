package com.github.microkibaco.taxi.common.http;

/**
 * 抽象接口
 */
public interface IHttpClient {

    IResponse get(IRequest request, boolean forceCache);

    IResponse post(IRequest request, boolean forceCache);

}
