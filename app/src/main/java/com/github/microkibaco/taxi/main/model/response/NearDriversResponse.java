package com.github.microkibaco.taxi.main.model.response;

import com.github.microkibaco.taxi.common.http.biz.BaseBizResponse;
import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;

import java.util.List;


public class NearDriversResponse extends BaseBizResponse {
    List<LocationInfo> data;

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }
}
