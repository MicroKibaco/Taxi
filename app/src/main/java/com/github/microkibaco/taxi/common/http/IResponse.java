package com.github.microkibaco.taxi.common.http;



public interface IResponse {

    // 状态码
    int getCode();

    // 数据体
    String getData();

}
