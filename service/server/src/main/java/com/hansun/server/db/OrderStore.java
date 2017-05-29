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

    @PostConstruct
    private void init() {
        orderTable = new OrderTable(connectionPoolManager);
    }

    @PreDestroy
    public void destroy() {
        try {
            connectionPoolManager.destroy();
        } catch (SQLException e) {
            throw new ServerException(e);
        }
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

    public void deleteOrder(String name) {
        orderTable.delete(name);
    }

    public List<Order> queryByDevice(List<Long> deviceID, Instant startTime, Instant endTIme) {
        Optional<List<Order>> result = orderTable.selectByDevice(deviceID, startTime, endTIme);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public Order insertOrder(Order device) {
        orderTable.insert(device);
        return device;
    }
}
