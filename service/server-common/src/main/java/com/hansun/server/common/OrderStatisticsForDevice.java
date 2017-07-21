package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatisticsForDevice extends OrderStatistics {

    private String deviceName;
    private long deviceId;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
}
