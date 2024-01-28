package com.witeam.service.common.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

//geo点
public class GeoPoint {

    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private Double altitude = 0.0;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        if (longitude == null || longitude < -180.0 || longitude > 180.0) {
            longitude = 0.0;
        }
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        if (latitude == null || latitude < -90.0 || latitude > 90.0) {
            latitude = 0.0;
        }
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        if (altitude == null) {
            altitude = 0.0;
        }
        this.altitude = altitude;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isEmpty() {
        if (this.latitude == null || this.latitude == 0.0) {
            return true;
        }

        if (this.longitude == null || this.longitude == 0.0) {
            return true;
        }


        //海拔不作为必须
        return false;
    }

    public static boolean isEmpty(GeoPoint point) {
        if (point == null) {
            return true;
        }

        return point.isEmpty();
    }
}
