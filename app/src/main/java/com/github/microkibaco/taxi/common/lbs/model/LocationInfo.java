package com.github.microkibaco.taxi.common.lbs.model;

/**
 * 位置信息实体类
 */

public class LocationInfo {

    private String key;

    private String name;

    private String latitude;

    private String longitude;

    private float rotation;

    @Override
    public String toString() {
        return "LocationInfo{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", rotation=" + rotation +
                '}';
    }

    public LocationInfo(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
