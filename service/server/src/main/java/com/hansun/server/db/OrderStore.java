package com.hansun.server.db;

import com.hansun.server.db.dao.OrderInfoDao;
import com.hansun.server.db.dao.OrderStaticsDayDao;
import com.hansun.server.db.dao.OrderStaticsMonthDao;
import com.hansun.server.db.dao.OrderStaticsTaskDao;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.dto.OrderStaticsDay;
import com.hansun.server.dto.OrderStaticsMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanl2 on 2017/4/27.
 */
@Repository
public class OrderStore {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderInfoDao orderDao;

    @Autowired
    private OrderStaticsTaskDao orderStaticsTaskDao;

    @Autowired
    private OrderStaticsDayDao orderStaticsDayDao;

    @Autowired
    private OrderStaticsMonthDao orderStaticsMonthDao;

    private Map<Long, OrderInfo> orderCache = new HashMap<>();

    @PostConstruct
    private void init() {
        initOrderCache();
    }

    @PreDestroy
    public void destroy() {
        orderCache.clear();

    }

    public List<OrderStaticsDay> getStaticsForOrderStaticsDayForUser(short userID, LocalDateTime startTime, LocalDateTime endTime){
        return orderStaticsDayDao.queryByTimeRangeForUser(userID, startTime, endTime);
    }

    public List<OrderStaticsDay> getStaticsForOrderStaticsDay(LocalDateTime startTime, LocalDateTime endTime){
        return orderStaticsDayDao.queryByTimeRange(startTime, endTime);
    }

    public List<OrderStaticsMonth> getStaticsForOrderStaticsMonthForUser(short userID, LocalDateTime month, LocalDateTime endTime){
        return orderStaticsMonthDao.queryByTimeRangeForUser(userID, month, endTime);
    }

    public List<OrderStaticsMonth> getStaticsForOrderStaticsMonth(LocalDateTime month, LocalDateTime endTime){
        return orderStaticsMonthDao.queryByTimeRange(month, endTime);
    }

    public OrderInfo queryOrderByDeviceID(Long deviceID) {
        return orderCache.get(deviceID); // 根据deviceID获取订单
    }

    public OrderInfo queryOrderByOrderID(Long orderID) {
        return orderDao.findByOrderID(orderID);
    }

    public OrderInfo updateOrder(OrderInfo order) {
        orderCache.put(order.getDeviceID(), order);
        OrderInfo o = orderDao.save(order);
        logger.info("update order = " + o);
        return o;
    }

    public OrderInfo updateOrderStatus(OrderInfo order) {
        orderCache.put(order.getDeviceID(), order);
        orderDao.updateOrderStatus(order.getOrderStatus(), order.getEndTime(), order.getOrderID());
        OrderInfo o = orderDao.findByOrderID(order.getOrderID());
        logger.info("update order = " + o);
        return o;
    }

    public void deleteOrder(Long deviceID) {
        orderCache.remove(deviceID);
    }

    public List<OrderInfo> queryByDevice(List<Long> deviceID, LocalDateTime startTime, LocalDateTime endTIme, short orderType) {
        return orderDao.queryByTime(startTime, endTIme, deviceID, orderType);
    }
    public List<OrderInfo> queryByTimeRangeOrderForUserNotFinish(short userID, LocalDateTime startTime, LocalDateTime endTIme, short orderType) {
        return orderDao.queryByTimeRangeForUserNotFinish(userID, startTime, endTIme, OrderStatus.FINISH, orderType);
    }

    public List<OrderInfo> queryByTimeRangeOrderNotFinish(LocalDateTime startTime, LocalDateTime endTIme, short orderType) {
        return orderDao.queryByTimeRangeNotFinish(startTime, endTIme, OrderStatus.FINISH, orderType);
    }

    public List<OrderInfo> queryByTimeRangeOrderFinish(short userID, LocalDateTime startTime, LocalDateTime endTIme, short orderType) {
        return orderDao.queryByTimeRangeForUser(userID, startTime, endTIme, OrderStatus.FINISH, orderType);
    }

    public List<OrderInfo> queryByTimeRangeOrderFinishForAll(LocalDateTime startTime, LocalDateTime endTIme, short orderType) {
        return orderDao.queryByTimeRange(startTime, endTIme, OrderStatus.FINISH, orderType);
    }

    public List<OrderInfo> queryNotFinish(short status) {
        return orderDao.queryOrderStatusNot(status);
    }

    public OrderInfo insertOrder(OrderInfo order) {
        OrderInfo o = orderDao.save(order);
        orderCache.put(o.getDeviceID(), o);

        return o;
    }

    private void initOrderCache() {
        long begin = System.currentTimeMillis();
        List<OrderInfo> notFinishedOrders = queryNotFinish(OrderStatus.FINISH);
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
