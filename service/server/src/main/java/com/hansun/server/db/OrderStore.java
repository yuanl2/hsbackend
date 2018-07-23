package com.hansun.server.db;

import com.hansun.server.dto.Order;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.common.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    private OrderTable orderTable;

    private Map<Long, Order> orderCache = new HashMap<>();

    @PostConstruct
    private void init() {
        orderTable = new OrderTable(connectionPoolManager);
        initOrderCache();
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

    public Order queryOrder(String orderID) {
        Optional<Order> result = orderTable.select(Long.valueOf(orderID));
        if (result.isPresent()) {
            Order d = result.get();
            return d;
        }
        return null;
    }

    public Order updateOrder(Order order) {
        orderCache.put(order.getDeviceID(),order);
        orderTable.update(order, order.getId());
        logger.info("update order = " + order);
        return order;
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

    public List<Order> queryNotFinish(int status) {
        Optional<List<Order>> result = orderTable.selectNotFinish(status);
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

    private void initOrderCache() {
        long begin = System.currentTimeMillis();
        List<Order> notFinishedOrders = queryNotFinish(OrderStatus.FINISH);
        if (notFinishedOrders != null && notFinishedOrders.size() > 0) {
            notFinishedOrders.forEach(k -> {
                if(k.getOrderStatus() == OrderStatus.NOTSTART || k.getOrderStatus() == OrderStatus.PAYDONE || k.getOrderStatus() == OrderStatus.SERVICE) {
                    logger.debug("init orderCache add order = {} ", k);
                    orderCache.putIfAbsent(k.getDeviceID(), k);
                }
            });
        }
        long end = System.currentTimeMillis();
        logger.info("init orderCache consume time = {} ms",(end - begin));
    }
}
