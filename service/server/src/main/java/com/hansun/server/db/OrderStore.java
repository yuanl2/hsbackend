package com.hansun.server.db;

import com.hansun.dto.Order;
import com.hansun.server.common.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by yuanl2 on 2017/4/27.
 */
@Repository
public class OrderStore {

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    private OrderTable orderTable;

    private Map<Long, Order> orderCache = new HashMap<>();

    @PostConstruct
    private void init() {
        orderTable = new OrderTable(connectionPoolManager);
    }

    @PreDestroy
    public void destroy() {
        try {
            orderCache.clear();
            connectionPoolManager.destroy();
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    public Order queryOrder(Long deviceID) {
        return orderCache.get(deviceID); // 根据deviceID获取订单
    }

    public Order queryOrder(String name) {
        Optional<Order> result = orderTable.select(name);
        if (result.isPresent()) {
            Order d = result.get();
            return d;
        }
        return null;
    }

    public Order updateOrder(Order device) {
        orderTable.update(device, device.getOrderName());
        return device;
    }

    public void deleteOrder(Long deviceID) {
        orderCache.remove(deviceID);
    }

    public List<Order> queryByDevice(List<Long> deviceID, Instant startTime, Instant endTIme) {
        Optional<List<Order>> result = orderTable.selectByDevice(deviceID, startTime, endTIme);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }


    public List<Order> queryNotFinish() {
        Optional<List<Order>> result = orderTable.selectNotFinish();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }


    public Order insertOrder(Order order) {
        orderCache.put(order.getDeviceID(), order);
        orderTable.insert(order);
        return order;
    }
}
