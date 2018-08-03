package com.hansun.server.db;

import com.hansun.server.db.dao.OrderDao;
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
import java.time.LocalDateTime;
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
    private OrderDao orderDao;

    private Map<Long, Order> orderCache = new HashMap<>();

    @PostConstruct
    private void init() {
        initOrderCache();
    }

    @PreDestroy
    public void destroy() {
        orderCache.clear();

    }

    public Order queryOrderByDeviceID(Long deviceID) {
        return orderCache.get(deviceID); // 根据deviceID获取订单
    }

    public Order queryOrderByOrderID(Long orderID) {
        return orderDao.findByOrderID(orderID);
    }

    public Order updateOrder(Order order) {
        orderCache.put(order.getDeviceID(), order);
        Order o = orderDao.save(order);
        logger.info("update order = " + o);
        return o;
    }

    public Order updateOrderStatus(Order order) {
        orderCache.put(order.getDeviceID(), order);
        Order o = orderDao.updateOrderStatus(order.getOrderStatus(), order.getEndTime(), order.getOrderID());
        logger.info("update order = " + o);
        return o;
    }

    public void deleteOrder(Long deviceID) {
        orderCache.remove(deviceID);
    }

    public List<Order> queryByDevice(List<Long> deviceID, LocalDateTime startTime, LocalDateTime endTIme) {
        return orderDao.queryByTime(startTime, endTIme, deviceID);
    }

    public List<Order> queryNotFinish(short status) {
        return orderDao.queryOrderStatusNot(status);
    }

    public Order insertOrder(Order order) {
        Order o = orderDao.save(order);
        orderCache.put(o.getDeviceID(), o);

        return o;
    }

    private void initOrderCache() {
        long begin = System.currentTimeMillis();
        List<Order> notFinishedOrders = queryNotFinish(OrderStatus.FINISH);
        if (notFinishedOrders != null && notFinishedOrders.size() > 0) {
            notFinishedOrders.forEach(k -> {
                if (k.getOrderStatus() == OrderStatus.NOTSTART || k.getOrderStatus() == OrderStatus.PAYDONE || k.getOrderStatus() == OrderStatus.SERVICE) {
                    logger.debug("init orderCache add order = {} ", k);
                    orderCache.putIfAbsent(k.getDeviceID(), k);
                }
            });
        }
        long end = System.currentTimeMillis();
        logger.info("init orderCache consume time = {} ms", (end - begin));
    }
}
