package com.hansun.server.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatisticsForArea extends OrderStatistics {

    private String areaName;
    private int areaId;
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
}
