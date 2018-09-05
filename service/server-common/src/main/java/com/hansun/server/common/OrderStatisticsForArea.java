package com.hansun.server.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatisticsForArea extends OrderStatistics {

    private String areaName;
    private int areaId;
    private String address;
    private String province;
    private String city;
    private List<OrderStatistics> orderStatistics = new ArrayList<>();
    private List<OrderStatisticsForDevice> orderStatisticsForDevices = new ArrayList<>();

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public List<OrderStatisticsForDevice> getOrderStatisticsForDevices() {
        return orderStatisticsForDevices;
    }

    public void addOrderStatisticsForDevices(OrderStatisticsForDevice orderStatisticsForDevice) {
        this.orderStatisticsForDevices.add(orderStatisticsForDevice);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
                .append(areaName)
                .append("\nprovince = ").append(province)
                .append("\ncity = ").append(city)
                .append("\n").append(super.toString()).append("\n");
        getOrderStatistics().stream().forEach(k -> builder.append(k).append("\n"));
        getOrderStatisticsForDevices().stream().forEach(k -> builder.append(k).append("\n"));
        return builder.toString();
    }
}
