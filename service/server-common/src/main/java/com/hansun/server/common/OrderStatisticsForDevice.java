package com.hansun.server.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatisticsForDevice extends OrderStatistics {

    private String deviceName;
    private long deviceId;

    private List<OrderStatistics> orderStatistics = new ArrayList<>();

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

    public List<OrderStatistics> getOrderStatistics() {
        return orderStatistics;
    }

    public void addOrderStatistics(OrderStatistics orderStatisticsForDevice) {
        this.orderStatistics.add(orderStatisticsForDevice);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderStatisticsForArea = ")
                .append(deviceId)
                .append("\ndeviceName = ").append(deviceName)
                .append("\n").append(super.toString()).append("\n");
        getOrderStatistics().stream().forEach(k -> builder.append(k).append("\n"));
        return builder.toString();
    }
}
