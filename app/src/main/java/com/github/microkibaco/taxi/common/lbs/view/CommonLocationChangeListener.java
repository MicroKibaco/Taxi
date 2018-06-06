package com.github.microkibaco.taxi.common.lbs.view;


import com.github.microkibaco.taxi.common.lbs.model.LocationInfo;

public interface CommonLocationChangeListener {
    void onLocationChanged(LocationInfo locationInfo);

    void onLocation(LocationInfo locationInfo);
}
