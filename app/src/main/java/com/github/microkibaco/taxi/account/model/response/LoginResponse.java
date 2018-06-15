package com.github.microkibaco.taxi.account.model.response;

import com.github.microkibaco.taxi.common.http.biz.BaseBizResponse;

/**
 * 账户数据模型
 */

public class LoginResponse extends BaseBizResponse {
    private Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
