package com.hansun.server.service;

import com.hansun.dto.Device;
import com.hansun.dto.Location;
import com.hansun.dto.Order;
import com.hansun.server.db.DataStore;
import com.hansun.server.db.OrderStore;
import com.hansun.server.db.PayAcountStore;
import com.hansun.server.metrics.HSServiceMetrics;
import com.hansun.server.metrics.HSServiceMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class OrderService {

    @Autowired
    private HSServiceMetricsService metricsService;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private OrderStore orderStore;

    @Autowired
    private PayAcountStore payAcountStore;

    public void sendMetrics(Order order) {
        metricsService.sendMetrics(HSServiceMetrics
                .builder()
                .measurement("")
                .build());
    }

    public Order createOrder(Order order) {

        return null;
    }

    public void deleteOrder(String name) {

        //Todo
    }

    public List<Order> queryOrderByDevice(String id, Instant startTime, Instant endTime) {
        List<String> deviceIDs = new ArrayList<>();
        deviceIDs.add(id);
        return orderStore.queryByDevice(deviceIDs, startTime, endTime);
    }

    public List<Order> queryOrderByUser(String id, Instant startTime, Instant endTime) {
        List<Device> devices = dataStore.queryDeviceByOwner(Integer.valueOf(id));
        List<String> deviceIDs = new ArrayList<>();
        devices.forEach(k -> {
            deviceIDs.add(k.getId());
        });
        return orderStore.queryByDevice(deviceIDs, startTime, endTime);
    }

    public List<Order> queryOrderByArea(String id, Instant startTime, Instant endTime) {
        List<Location> locationList = dataStore.queryLocationByAreaID(Integer.valueOf(id));
        List<String> deviceIDs = new ArrayList<>();
        locationList.forEach(k -> {
            List<Device> lists = dataStore.queryDeviceByLocation(k.getId());
            if (lists != null) {
                lists.forEach(v -> {
                    deviceIDs.add(v.getId());
                });
            }
        });
        return orderStore.queryByDevice(deviceIDs, startTime, endTime);
    }
}
