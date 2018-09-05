package com.hansun.server.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatisticsForUser extends OrderStatistics {

    private String user;
    private int userId;
    private List<OrderStatistics> orderStatistics = new ArrayList<>();
    private List<OrderStatisticsForArea> orderStatisticsForAreas = new ArrayList<>();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<OrderStatisticsForArea> getOrderStatisticsForAreas() {
        return orderStatisticsForAreas;
    }

    public void addOrderStatisticsForAreas(OrderStatisticsForArea orderStatisticsForArea) {
        this.orderStatisticsForAreas.add(orderStatisticsForArea);
    }

    public List<OrderStatistics> getOrderStatistics() {
        return orderStatistics;
    }

    public void addOrderStatistics(OrderStatistics orderStatisticsForDevice) {
        this.orderStatistics.add(orderStatisticsForDevice);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderStatisticsForUser = ")
                .append(user)
                .append("\n")
                .append(super.toString()).append("\n");
        getOrderStatistics().stream().forEach(k -> builder.append(k).append("\n"));
        getOrderStatisticsForAreas().stream().forEach(k -> builder.append(k).append("\n"));
        return builder.toString();
    }
}
