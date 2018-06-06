package com.github.microkibaco.taxi.common.lbs.view;

import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;

import java.util.List;

/**
 * POI 搜索结果监听器
 */

public interface OnSearchedListener {
    void onSearched(List<LocationInfo> results);

    void onError(int rCode);
}
