package com.hansun.server.service;

import com.hansun.dto.Device;
import com.hansun.dto.Location;
import com.hansun.dto.Order;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.commu.LinkManger;
import com.hansun.server.commu.SocketServerSelector;
import com.hansun.server.db.DataStore;
import com.hansun.server.db.OrderStore;
import com.hansun.server.db.PayAcountStore;
import com.hansun.server.metrics.HSServiceMetrics;
import com.hansun.server.metrics.HSServiceMetricsService;
import org.apache.tools.ant.taskdefs.condition.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HSServiceMetricsService metricsService;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private OrderStore orderStore;

    @Autowired
    private PayAcountStore payAcountStore;

    private Map<String, Order> orderCache = new HashMap<>();


    @PostConstruct
    public void init() {
        //todo 初始化订单缓存
    }

    @PreDestroy
    public void destroy() {
        orderCache.clear();
    }

    public void sendMetrics(Order order) {
        metricsService.sendMetrics(HSServiceMetrics
                .builder()
                .measurement("")
                .build());
    }

    public void startOrder(String name) {
        Order order = orderCache.get(name);
        if (order != null) {
            order = orderStore.queryOrder(name);
        }
        if (order != null) {
            order.setOrderStatus(OrderStatus.START);
            order.setStartTime(Instant.now());
            orderStore.updateOrder(order);
        }
        logger.error(name + " have no order now");
    }

    public void finishOrder(String name) {
        Order order = orderCache.get(name);
        if (order != null) {
            order = orderStore.queryOrder(name);
        }
        if (order != null) {
            order.setOrderStatus(OrderStatus.FINISH);
            order.setEndTime(Instant.now());
            orderStore.updateOrder(order);
        }
        logger.error(name + " have no order now");
    }

    public void OrderNotFinish(String name, int orderStatus) {
        Order order = orderCache.get(name);
        if (order != null) {
            order = orderStore.queryOrder(name);
        }
        if (order != null) {
            order.setOrderStatus(orderStatus);
            order.setEndTime(Instant.now());
            orderStore.updateOrder(order);
        }
        logger.error(name + " have no order now");
    }

    public Order createOrder(Order order) {
        orderCache.put(order.getDeviceName(), order);
        return orderStore.insertOrder(order);
    }

    public List<Order> getOrder(String deviceName){
       orderCache.get(deviceName);
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
