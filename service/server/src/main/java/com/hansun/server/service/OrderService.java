package com.hansun.server.service;

import com.hansun.server.common.*;
import com.hansun.server.commu.IHandler;
import com.hansun.server.commu.LinkManger;
import com.hansun.server.commu.SyncAsynMsgController;
import com.hansun.server.db.DataStore;
import com.hansun.server.db.OrderStore;
import com.hansun.server.dto.*;
import com.hansun.server.dto.summary.InfoCardData;
import com.hansun.server.dto.summary.OrderSummaryData;
import com.hansun.server.dto.summary.PieData;
import com.hansun.server.dto.summary.SummaryInfo;
import com.hansun.server.metrics.HSServiceMetrics;
import com.hansun.server.metrics.HSServiceMetricsService;
import com.hansun.server.metrics.InfluxDBClientHelper;
import com.hansun.server.util.MsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hansun.server.common.Utils.*;
import static java.util.stream.Collectors.*;

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
    private SyncAsynMsgController syncAsynMsgController;

    @Autowired
    private LinkManger linkManger;

    @Autowired
    private InfluxDBClientHelper influxDBClientHelper;

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }

    public void sendMetrics(OrderInfo order) {
        metricsService.sendMetrics(HSServiceMetrics
                .builder()
                .measurement("")
                .build());
    }

    public void createStartMsgToDevice(OrderInfo order) {
        try {
//            order.setId(Long.valueOf(getSequenceNumber()));
            Device device = dataStore.queryDeviceByDeviceID(order.getDeviceID());
            if (device == null) {
                logger.error("can not create order for device not exist  " + order.getDeviceName());
                throw new ServerException("can not create order for device not exist  " + order.getDeviceName());
            }
            int index = device.getPort();

            String deviceType = MsgUtil.getMsgBodyLength(device.getType(), 3);
            //4g device
            if (device.getSimCard().length() == MsgConstant4g.DEVICE_NAME_FIELD_SIZE) {

                int portNum = 1;
                com.hansun.server.commu.msg4g.ServerStartDeviceMsg msg = new com.hansun.server.commu.msg4g.ServerStartDeviceMsg(MsgConstant4g.DEVICE_START_MSG);
                msg.setDeviceType(deviceType);

                order.setStartTime(Utils.getNowTime());
                order.setPrice(dataStore.queryConsume(order.getConsumeType()).getPrice());
                order.setDuration(dataStore.queryConsume(order.getConsumeType()).getDuration());

                if (deviceType.equals("000")) {
                    portNum = 4;
                } else if (deviceType.equals("100")) {
                    portNum = 1;
                }
                Map<Integer, Byte> map = new HashMap<>();
                for (int i = 1; i <= portNum; i++) {
                    if (index == i) {
                        map.put(i, (byte) 1);
                    } else {
                        map.put(i, (byte) 0);
                    }
                }
                msg.setMap(map);
                Map<Integer, String> times = new HashMap<>();
                for (int i = 1; i <= portNum; i++) {
                    if (index == i) {
                        int durationMinute = order.getDuration() / 60;
                        int durationSecond = order.getDuration() % 60;
                        StringBuilder stringBuilder = new StringBuilder();
                        if (durationMinute < 10) {
                            stringBuilder.append("0").append(durationMinute);
                        } else {
                            stringBuilder.append(durationMinute);
                        }
                        stringBuilder.append(",");
                        if (durationSecond < 10) {
                            stringBuilder.append("0").append(durationSecond);
                        } else {
                            stringBuilder.append(durationSecond);
                        }
                        stringBuilder.append(",");
                        times.put(i, stringBuilder.toString());
                    } else {
                        times.put(i, "00,00,");
                    }

                }
                msg.setStartMap(times);

                String simcard = device.getSimCard();

                IHandler handler = linkManger.get(simcard);
                if (handler == null) {
                    logger.error("can not create order for handler for device not exist  " + device.getName());
                    throw new ServerException("can not create order for handler for device not exist  " + device.getName());
                }

                msg.setSeq(String.valueOf(handler.getSeq()));
                syncAsynMsgController.createSyncWaitResult(msg, handler, index);
                handler.sendMsg(msg, device.getPort());
            }
//            else{
//                ServerStartDeviceMsg msg = new ServerStartDeviceMsg(DEVICE_START_MSG);
//                msg.setDeviceType("000");
//
//                order.setPrice(dataStore.queryConsume(order.getConsumeType()).getPrice());
//                order.setDuration(dataStore.queryConsume(order.getConsumeType()).getDuration());
//
//                Map<Integer, String> map = new HashMap<>();
//                for (int i = 1; i <= 4; i++) {
//                    if (index == i) {
//                        map.put(i, "1");
//                    } else {
//                        map.put(i, "0");
//                    }
//                }
//                msg.setStatus(map);
//                Map<Integer, String> times = new HashMap<>();
//                for (int i = 1; i <= 4; i++) {
//                    if (index == i) {
//                        int duration = order.getDuration();
//                        if (duration < 10) {
//                            times.put(i, "0" + order.getDuration());
//                        } else {
//                            times.put(i, order.getDuration() + "");
//                        }
//                    } else {
//                        times.put(i, "00");
//                    }
//                }
//                msg.setMap(times);
//
//                String simcard = device.getSimCard();
//
//                IHandler handler = linkManger.get(simcard);
//                if (handler == null) {
//                    logger.error("can not create order for handler for device not exist  " + device.getName());
//                    throw new ServerException("can not create order for handler for device not exist  " + device.getName());
//                }
//
//                msg.setSeq(String.valueOf(handler.getSeq()));
//                syncAsynMsgController.createSyncWaitResult(msg, handler, index);
//                handler.sendMsg(msg, device.getPort());
//            }


        } catch (Exception e) {
            logger.error("createStartMsgToDevice error", e);
            throw new ServerException("createStartMsgToDevice error", e);
        }
    }

    public OrderInfo createOrder(OrderInfo order) {
        Device device = dataStore.queryDeviceByDeviceID(order.getDeviceID());
//        device.setStatus(DeviceStatus.STARTTASK);
//        dataStore.updateDevice(device);
        order.setUserID(device.getUserID());
        OrderInfo result = orderStore.insertOrder(order);
        return result;
    }

//    public void processStartOrder(String deviceBoxName, int port) {
//        Device d = dataStore.queryDeviceByDeviceBoxAndPort(deviceBoxName, port);
//        OrderInfo order = orderStore.queryOrderByDeviceID(d.getDeviceID());
//        if (order != null && order.getOrderStatus() != OrderStatus.SERVICE) {
//            logger.info("update order before = " + order);
//            order.setOrderStatus(OrderStatus.SERVICE);
//            order.setStartTime(Utils.getNowTime());
//            orderStore.updateOrder(order);
//
//            //不能等心跳消息来了再更新设备的状态，应该根据业务的回应及时更新
//            dataStore.updateDevice(d, DeviceStatus.SERVICE);
//        } else {
//            logger.error(d.getDeviceID() + " have no order now");
//        }
//    }
//
//    public void processFinishOrder(String deviceBoxName, Map<Integer, Byte> map) {
//        map.forEach((k, v) -> {
//            Device d = dataStore.queryDeviceByDeviceBoxAndPort(deviceBoxName, k);
//            OrderInfo order = orderStore.queryOrderByDeviceID(d.getDeviceID());
//
//            if (order != null && v == DeviceStatus.IDLE) {
//                if (Utils.isOrderFinshed(order)) {
//                    logger.info("update order before = " + order);
//                    order.setOrderStatus(OrderStatus.FINISH);
//                    order.setEndTime(Utils.getNowTime());
//                    orderStore.updateOrder(order);
//
//                    dataStore.updateDevice(d, DeviceStatus.IDLE);
//
//                    //remove order from cache not table
//                    orderStore.deleteOrder(d.getDeviceID());
//                    logger.info("order delete = " + order);
//                }
//            } else {
//                logger.error(d.getDeviceID() + " have no order now");
//            }
//
//        });
//
//        dataStore.updateDeviceStatus(deviceBoxName, map, "0");
//    }


//    public void OrderNotFinish(String name, int orderStatus) {
//        Order order = orderCache.get(name);
//        if (order != null) {
//            order = orderStore.queryOrder(name);
//        }
//        if (order != null) {
//            order.setOrderStatus(orderStatus);
//            order.setEndTime(Instant.now());
//            orderStore.updateOrder(order);
//        }
//        logger.error(name + " have no order now");
//    }

    public void updateOrder(OrderInfo order) {
        orderStore.updateOrder(order);
    }

    public OrderInfo getOrder(Long deviceID) {
        return orderStore.queryOrderByDeviceID(deviceID);
    }

    public OrderInfo getOrderByOrderID(long orderID) {
        return orderStore.queryOrderByOrderID(orderID);
    }

    /**
     * 更新订单状态为完成，并且删除缓存中的订单
     *
     * @param deviceID
     */
    public void deleteOrder(Long deviceID) {
        OrderInfo order = orderStore.queryOrderByDeviceID(deviceID);
        if (order != null && order.getOrderStatus()!=OrderStatus.FINISH) {
            order.setEndTime(Utils.getNowTime());
            order.setOrderStatus(OrderStatus.FINISH);
            orderStore.updateOrder(order);

            dataStore.addPayAccount(order.getPayAccount());

            logger.debug("Before delete order {} ", order);
            orderStore.deleteOrder(deviceID);
            logger.debug("After delete order {}", orderStore.queryOrderByDeviceID(deviceID));
        }
    }

    /**
     * 不更新订单状态为完成，并且删除缓存中的订单
     *
     * @param deviceID
     */
    public void removeOrder(Long deviceID) {
        orderStore.deleteOrder(deviceID);
    }


    /**
     * get the not finish order list for userID and beginTime
     *
     * @param userID
     * @param beginTime
     * @return
     */
    public List<OrderDetail> queryOrderByTimeOrderNotFinish(short userID, LocalDateTime beginTime) {
        if (convertToInstant(beginTime).isAfter(Instant.now())) {
            return null;
        }
        List<OrderInfo> orderList = orderStore.queryByTimeRangeOrderNotFinish(userID, beginTime, Utils.getNowTime(), OrderType.OPERATIONS.getType());
        return getOrderDetails(orderList);
    }

    /**
     * 补充订单的信息
     *
     * @param orderList
     * @return
     */
    private List<OrderDetail> getOrderDetails(List<OrderInfo> orderList) {
        List<OrderDetail> orderDetailList = new ArrayList<>();
        if (checkListNotNull(orderList)) {
            orderDetailList = orderList.stream().map(
                    orderInfo -> {
                        OrderDetail orderDetail = new OrderDetail(orderInfo);
                        try {
                            Device device = dataStore.queryDeviceByDeviceID(orderInfo.getDeviceID());
                            if (device != null) {
                                Location location = dataStore.queryLocationByLocationID(device.getLocationID());
                                orderDetail.setAreaName(location.getAreaName());
                                orderDetail.setLocationID(location.getId());
                                orderDetail.setAddress(location.getAddress());
                                orderDetail.setDeviceName(device.getName());
                                orderDetail.setUser(dataStore.queryUser(device.getUserID()).getUsername());
                                orderDetail.setCity(location.getCity());
                                orderDetail.setProvince(location.getProvince());
                            } else {
                                logger.error("order {} has no device", orderDetail.getOrderID());
                            }
                        } catch (Exception e) {
                            logger.error(" process orderDetail error {}", e);
                        }
                        return orderDetail;
                    }
            ).collect(toList());
        }
        return orderDetailList;

    }

    /**
     * get all order between startTime and endTime
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByTimeForUser(short userID, LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderInfo> orderList = orderStore.queryByTimeRangeOrderFinish(userID, startTime, endTime, OrderType.OPERATIONS.getType());
        return getOrderDetails(orderList);
    }

    /**
     * get all order between startTime and endTime
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByTime(LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderInfo> orderList = orderStore.queryByTimeRangeOrderFinishForAll(startTime, endTime, OrderType.OPERATIONS.getType());
        return getOrderDetails(orderList);
    }

    /**
     * include month and exclude endTIme
     *
     * @param month
     * @param endTime
     * @return
     */
    public List<OrderStaticsMonth> getStaticsForOrderStaticsMonthForUser(short userID, LocalDateTime month, LocalDateTime endTime) {
        return orderStore.getStaticsForOrderStaticsMonthForUser(userID, month, endTime);
    }

    /**
     * @param month
     * @param endTime
     * @return
     */
    public List<OrderStaticsMonth> getStaticsForOrderStaticsMonth(LocalDateTime month, LocalDateTime endTime) {
        return orderStore.getStaticsForOrderStaticsMonth(month, endTime);
    }

    /**
     * @param userID
     * @param month
     * @param endTime
     * @return
     */
    public List<OrderStaticsDay> getStaticsForOrderStaticsDayForUser(short userID, LocalDateTime month, LocalDateTime endTime) {
        return orderStore.getStaticsForOrderStaticsDayForUser(userID, month, endTime);
    }

    /**
     * @param month
     * @param endTime
     * @return
     */
    public List<OrderStaticsDay> getStaticsForOrderStaticsDay(LocalDateTime month, LocalDateTime endTime) {
        return orderStore.getStaticsForOrderStaticsDay(month, endTime);
    }

    /**
     * sum order group by deviceID and day
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderStaticsDay> getStaticsFromOrderInfoForOrderStaticsDay(LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderDetail> orderList = queryOrderByTime(startTime, endTime);
        List<OrderStaticsDay> orderStaticsDayList = new ArrayList<>();

        if (checkListNotNull(orderList)) {
            orderList.stream().collect(groupingBy(OrderDetail::getDeviceID))
                    .forEach((deviceID, orderDetailList) -> {
                        Device device = dataStore.queryDeviceByDeviceID(deviceID);
                        Location location = dataStore.queryLocationByLocationID(device.getLocationID());
                        orderDetailList.stream().collect(groupingBy(OrderDetail::getsDay)).forEach(
                                (time, o) -> {
                                    String month = o.get(0).getMonth();
                                    double income = o.stream().collect(summingDouble(OrderDetail::getPrice));
                                    short count = (short) o.size();
                                    OrderStaticsDay orderStaticsDay = new OrderStaticsDay();
                                    orderStaticsDay.setAddress(location.getAddress());
                                    orderStaticsDay.setAreaName(location.getAreaName());
                                    orderStaticsDay.setDeviceID(deviceID);
                                    orderStaticsDay.setLocationID(location.getId());
                                    orderStaticsDay.setUserID(device.getUserID());
                                    orderStaticsDay.setUserName(device.getUser());
                                    orderStaticsDay.setIncomeTotal(income);
                                    orderStaticsDay.setOrderTotal(count);
                                    orderStaticsDay.setsMonth(month);
                                    orderStaticsDay.setTime(o.get(0).getStartTime());
                                    orderStaticsDayList.add(orderStaticsDay);
                                });
                    });
        }
        return orderStaticsDayList;
    }


    /**
     * um order group by deviceID and day
     *
     * @param userID
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderStaticsDay> getStaticsFromOrderInfoForUser(short userID, LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderDetail> orderList = queryOrderByTimeForUser(userID, startTime, endTime);
        logger.info("getStaticsFromOrderInfoForUser {}", orderList.size());
        List<OrderStaticsDay> orderStaticsDayList = new ArrayList<>();

        if (checkListNotNull(orderList)) {
            orderList.stream().collect(groupingBy(OrderDetail::getDeviceID))
                    .forEach((deviceID, orderDetailList) -> {
                        Device device = dataStore.queryDeviceByDeviceID(deviceID);
                        Location location = dataStore.queryLocationByLocationID(device.getLocationID());
                        orderDetailList.stream().collect(groupingBy(OrderDetail::getsDay)).forEach(
                                (time, o) -> {
                                    String month = o.get(0).getMonth();
                                    double income = o.stream().collect(summingDouble(OrderDetail::getPrice));
                                    short count = (short) o.size();
                                    OrderStaticsDay orderStaticsDay = new OrderStaticsDay();
                                    orderStaticsDay.setAddress(location.getAddress());
                                    orderStaticsDay.setAreaName(location.getAreaName());
                                    orderStaticsDay.setDeviceID(deviceID);
                                    orderStaticsDay.setLocationID(location.getId());
                                    orderStaticsDay.setUserID(device.getUserID());
                                    orderStaticsDay.setUserName(device.getUser());
                                    orderStaticsDay.setIncomeTotal(income);
                                    orderStaticsDay.setOrderTotal(count);
                                    orderStaticsDay.setsMonth(month);
                                    orderStaticsDay.setTime(o.get(0).getStartTime());
                                    orderStaticsDayList.add(orderStaticsDay);
                                });
                    });
        }
        return orderStaticsDayList;

    }

    /**
     * get order list with user name
     *
     * @param user
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByUser(short user, LocalDateTime startTime, LocalDateTime endTime) {
        return queryOrderByTimeForUser(user, startTime, endTime).stream().collect(toList());
    }

    /**
     * get order list with location id
     *
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByArea(short id, short userID, LocalDateTime startTime, LocalDateTime endTime) {
        return queryOrderByTimeForUser(userID, startTime, endTime).stream().filter(orderDetail ->
                orderDetail.getLocationID() == id
        ).collect(toList());
    }

    /**
     * get order list with device id
     *
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByDevice(long id, LocalDateTime startTime, LocalDateTime endTime) {
        Device device = dataStore.queryDeviceByDeviceID(id);
        if (device != null) {
            return queryOrderByTimeForUser(device.getUserID(), startTime, endTime).stream().filter(orderDetail ->
                    orderDetail.getDeviceID() == id
            ).collect(toList());
        }
        return null;
    }

    private short getUserId(String user) {
        short id = -1;
        if (!Utils.isNumeric(user)) {
            for (User u : dataStore.queryAllUser()
                    ) {
                if (u.getUsername().equals(user)) {
                    id = u.getId();
                }
            }
        } else {
            id = Short.valueOf(user);
        }
        return id;
    }

    private User getUser(String user) {
        if (!Utils.isNumeric(user)) {
            for (User u : dataStore.queryAllUser()
                    ) {
                if (u.getUsername().equals(user)) {
                    return u;
                }
            }
        } else {
            return dataStore.queryUser(Short.valueOf(user));
        }
        return null;
    }

//    public OrderStatisticsForUser queryOrderStatisticsByUser(String user, LocalDateTime endTime) {
//        int id = getUserId(user);
//        User u = getUser(id + "");
//        return queryOrderStatisticsByUser(u, u.getCreateTime(), endTime);
//    }
//
//
//    public OrderStatisticsForUser queryOrderStatisticsByUser(String user) {
//        int id = getUserId(user);
//        User u = getUser(id + "");
//        return queryOrderStatisticsByUser(u, u.getCreateTime(), Utils.getNowTime());
//    }


    /**
     * 时间汇总维度，day，month，year
     *
     * @param userName
     * @param startTime
     * @param endTime
     * @param sumType
     * @return
     */
    public List<OrderStatistics> queryOrderStatisticsByUser(String userName, LocalDateTime startTime, LocalDateTime endTime, int sumType) {
        if (convertToInstant(startTime).isAfter(convertToInstant(endTime))) {
            return null;
        }

        short userID = getUserId(userName);
        User user = getUser(userID + "");

        //get current month data
        LocalDateTime currentMonth = Utils.getCurrentMonth();
        LocalDateTime today = Utils.getZeroClock(Utils.getNowTime());

        //如果时间范围包含了今天，则需要从原始记录表OrderInfo中读取原始记录汇总
        boolean isContainToday = convertToInstant(endTime).isAfter(Instant.now());

        logger.info("isContainToday = {}", isContainToday);

        //这个flags只是用来在进行日报表查询时候，判断原始记录是否已经累加到当日记录中
        final List<Boolean> flags = new ArrayList<>();
        List<OrderStatistics> orderStatisticsList = new ArrayList<>();
        //当日数据形成按区域的Map
        final Map<Short, List<OrderStaticsDay>> todayData = getStaticsFromOrderInfoForUser(userID, today, Utils.getNowTime()).stream().collect(groupingBy(OrderStaticsDay::getLocationID));

        logger.info("todayData = {}", todayData.size());

        todayData.forEach((k, v) -> {
            logger.info("k = {}", k);
            v.forEach(list -> {
                logger.info("{}", list);
            });
        });

        if (sumType == OrderStaticsType.DAY.getValue()) {
            List<OrderStaticsDay> dayDataList = getStaticsForOrderStaticsDayForUser(userID, startTime, endTime);
            if (checkListNotNull(dayDataList)) {
                dayDataList.stream().collect(groupingBy(OrderStaticsDay::getLocationID)).forEach((locationID, orderStaticsDayList) -> {
                    Location location = dataStore.queryLocationByLocationID(locationID);
                    int devices = dataStore.queryDeviceByLocation(locationID).size();
                    orderStaticsDayList.stream().collect(groupingBy(OrderStaticsDay::getTime)).forEach((time, lists) -> {
                        if (checkListNotNull(lists)) {
                            double income = lists.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal));
                            int count = lists.stream().collect(summingInt(OrderStaticsDay::getOrderTotal));
                            int runningDevices = lists.size();

                            OrderStatistics orderStatistics = new OrderStatistics();
                            orderStatistics.setOrderTotal(count);
                            orderStatistics.setIncomeTotal(income);
                            orderStatistics.setTime(new SimpleDateFormat("yyyy-MM-dd").format(dateToLocalDate(time)));
                            orderStatistics.setSumTimeType(OrderStaticsType.DAY.getDesc());
                            orderStatistics.setDeviceTotal(devices);
                            orderStatistics.setRunningDeviceTotal(runningDevices);
                            orderStatistics.setUser(userName);
                            orderStatistics.setAreaName(location.getAreaName());
                            orderStatistics.setEnterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateToLocalDate(location.getEnterTime())));
                            orderStatistics.setAverageIncome(income / devices);
                            orderStatisticsList.add(orderStatistics);
                        }
                    });
                });
            }

            //查询的时候，OrderStaticsDay不会有当天的数据，当天的数据汇总成Day，只会在第二天触发
            if (isContainToday) {
                todayData.forEach((locationID, orderStaticsDayList) -> {
                    Location location = dataStore.queryLocationByLocationID(locationID);
                    int devices = dataStore.queryDeviceByLocation(locationID).size();

                    orderStaticsDayList.stream().collect(groupingBy(OrderStaticsDay::getTime)).forEach((time, lists) -> {
                        if (checkListNotNull(lists)) {
                            double income = lists.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal));
                            int count = lists.stream().collect(summingInt(OrderStaticsDay::getOrderTotal));
                            int runningDevices = lists.size();

                            OrderStatistics orderStatistics = new OrderStatistics();
                            orderStatistics.setOrderTotal(count);
                            orderStatistics.setIncomeTotal(income);
                            orderStatistics.setTime(new SimpleDateFormat("yyyy-MM-dd").format(dateToLocalDate(time)));
                            orderStatistics.setSumTimeType(OrderStaticsType.DAY.getDesc());
                            orderStatistics.setDeviceTotal(devices);
                            orderStatistics.setRunningDeviceTotal(runningDevices);
                            orderStatistics.setUser(userName);
                            orderStatistics.setAreaName(location.getAreaName());
                            orderStatistics.setEnterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateToLocalDate(location.getEnterTime())));
                            orderStatistics.setAverageIncome(income / devices);
                            orderStatisticsList.add(orderStatistics);
                        }
                    });
                });
            }
        } else if (sumType == OrderStaticsType.MONTH.getValue()) {
            List<OrderStaticsMonth> monthDataList = getStaticsForOrderStaticsMonthForUser(userID, startTime, endTime);

            if (checkListNotNull(monthDataList)) {
                monthDataList.stream().collect(groupingBy(OrderStaticsMonth::getLocationID)).forEach((locationID, orderStaticsMonthData) -> {
                    Location location = dataStore.queryLocationByLocationID(locationID);
                    int devices = dataStore.queryDeviceByLocation(locationID).size();
                    orderStaticsMonthData.stream().collect(groupingBy(OrderStaticsMonth::getTime)).forEach((time, lists) -> {
                        if (checkListNotNull(lists)) {
                            double income = lists.stream().collect(summingDouble(OrderStaticsMonth::getIncomeTotal));
                            int count = lists.stream().collect(summingInt(OrderStaticsMonth::getOrderTotal));
                            int runningDevices = lists.size();

                            OrderStatistics orderStatistics = new OrderStatistics();
                            orderStatistics.setOrderTotal(count);
                            orderStatistics.setIncomeTotal(income);
                            orderStatistics.setTime(new SimpleDateFormat("yyyy-MM").format(dateToLocalDate(time)));
                            orderStatistics.setSumTimeType(OrderStaticsType.DAY.getDesc());
                            orderStatistics.setDeviceTotal(devices);
                            orderStatistics.setRunningDeviceTotal(runningDevices);
                            orderStatistics.setUser(userName);
                            orderStatistics.setAreaName(location.getAreaName());
                            orderStatistics.setEnterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateToLocalDate(location.getEnterTime())));
                            if (isContainToday && currentMonth.isEqual(time)) {
                                List<OrderStaticsDay> list = todayData.get(locationID);
                                if (checkListNotNull(list)) {
                                    double todayIncome = list.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal));
                                    int todayCount = list.stream().collect(summingInt(OrderStaticsDay::getOrderTotal));
                                    int todayRunningDevices = list.size();

                                    if (orderStatistics.getRunningDeviceTotal() < todayRunningDevices) {
                                        orderStatistics.setRunningDeviceTotal(todayRunningDevices);
                                    }
                                    orderStatistics.setIncomeTotal(orderStatistics.getIncomeTotal() + todayIncome);
                                    orderStatistics.setOrderTotal(orderStatistics.getOrderTotal() + todayCount);
                                }
                                //add one boolean value with true
                                flags.add(true);
                            }
                            orderStatistics.setAverageIncome(income / devices);
                            orderStatisticsList.add(orderStatistics);
                        }
                    });
                });
            }

            if (flags.size() == 0 && todayData != null && todayData.size() > 0) {
                todayData.forEach((locationID, orderStaticsDayList) -> {
                    Location location = dataStore.queryLocationByLocationID(locationID);
                    int devices = dataStore.queryDeviceByLocation(locationID).size();
                    if (checkListNotNull(orderStaticsDayList)) {
                        orderStaticsDayList.stream().collect(groupingBy(OrderStaticsDay::getTime)).forEach((time, lists) -> {
                            if (checkListNotNull(lists)) {
                                double income = lists.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal));
                                int count = lists.stream().collect(summingInt(OrderStaticsDay::getOrderTotal));
                                int runningDevices = lists.size();

                                OrderStatistics orderStatistics = new OrderStatistics();
                                orderStatistics.setOrderTotal(count);
                                orderStatistics.setIncomeTotal(income);
                                orderStatistics.setTime(new SimpleDateFormat("yyyy-MM").format(dateToLocalDate(time)));
                                orderStatistics.setSumTimeType(OrderStaticsType.DAY.getDesc());
                                orderStatistics.setDeviceTotal(devices);
                                orderStatistics.setRunningDeviceTotal(runningDevices);
                                orderStatistics.setUser(userName);
                                orderStatistics.setAreaName(location.getAreaName());
                                orderStatistics.setEnterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateToLocalDate(location.getEnterTime())));
                                orderStatistics.setAverageIncome(income / devices);
                                orderStatisticsList.add(orderStatistics);
                            }
                        });
                    }
                });
            }
        }
        return orderStatisticsList;
    }

    /**
     * get all devices under the user
     *
     * @param user
     * @return
     */
    private Map<Short, List<Long>> getLocationDeviceMaps(User user) {
        List<Location> locationList = dataStore.queryLocationByUserID(Integer.valueOf(user.getId()));
        /**
         * Key 是LocationID
         */
        Map<Short, List<Long>> deviceIDMaps = new HashMap<>();
        if (locationList != null && locationList.size() > 0) {
            locationList.forEach(k -> {
                List<Device> lists = dataStore.queryDeviceByLocation(k.getId());
                if (lists != null) {
                    deviceIDMaps.put(k.getId(), lists.stream().map(Device::getDeviceID).collect(toList()));
                }
            });
        }
        logger.debug("queryOrderStatisticsByUser deviceIDMaps size {}", deviceIDMaps.size());
        return deviceIDMaps;
    }

    public static String getSequenceNumber() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss"); // 12
        String str = sdf.format(d);
        String haomiao = String.valueOf(System.nanoTime());
        str = str + haomiao.substring(haomiao.length() - 6, haomiao.length());
        return str;
    }

    public static long getOrderName() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss"); // 12
        String str = sdf.format(d);
        String haomiao = String.valueOf(System.nanoTime());
        str = str + haomiao.substring(haomiao.length() - 6, haomiao.length());
        return Long.valueOf("00" + str);
    }


    /**
     * @param userID
     * @return
     */
    public OrderSummaryData getOrderSummaryData(short userID) {

        OrderSummaryData orderSummaryData = new OrderSummaryData();

        int[] todayData = new int[24];
        int[] todayDataIncome = new int[24];
        int[] yesterdayData = new int[24];
        int[] yesterdayDataIncome = new int[24];
        LocalDateTime today = Utils.getZeroClock(Utils.getNowTime());
        LocalDateTime yesterday = Utils.getDayBefore(today, 1);
        List<OrderDetail> todayDataLists = queryOrderByTimeForUser(userID, today, Utils.getNowTime());
        List<OrderDetail> yesterdayDataLists = queryOrderByTimeForUser(userID, yesterday, today);

        if (checkListNotNull(yesterdayDataLists)) {
            yesterdayDataLists.stream().collect(groupingBy(OrderDetail::getHour)).forEach(
                    (hour, lists) -> {
                        int income = lists.stream().collect(summingDouble(OrderDetail::getPrice)).intValue();
                        int count = lists.size();
                        todayDataIncome[hour] = income;
                        todayData[hour] = count;
                    }
            );
        }
        if (checkListNotNull(todayDataLists)) {
            todayDataLists.stream().collect(groupingBy(OrderDetail::getHour)).forEach(
                    (hour, lists) -> {
                        int income = lists.stream().collect(summingDouble(OrderDetail::getPrice)).intValue();
                        int count = lists.size();
                        yesterdayDataIncome[hour] = income;
                        yesterdayData[hour] = count;
                    }
            );
        }

        orderSummaryData.setTodayOrderData(todayData);
        orderSummaryData.setYesterdayOrderData(yesterdayData);
        return orderSummaryData;
    }


    /**
     * get all summary info for user
     *
     * @param userID
     * @return
     */
    public SummaryInfo getSummaryInfo(short userID) {
        SummaryInfo summaryInfo = new SummaryInfo();

        //get current month data
        LocalDateTime currentMonth = Utils.getCurrentMonth();
        LocalDateTime today = Utils.getZeroClock(Utils.getNowTime());

        List<OrderStaticsMonth> currentOldData = getStaticsForOrderStaticsMonthForUser(userID, currentMonth, Utils.getNextMonth(currentMonth));

        //get today data from orderInfo table
        List<OrderStaticsDay> todayDataLists = getStaticsFromOrderInfoForUser(userID, today, Utils.getNowTime());
        Map<Long, List<OrderStaticsDay>> todayData = todayDataLists.stream().collect(groupingBy(OrderStaticsDay::getDeviceID));

        processCurrrentMonthData(summaryInfo, currentOldData, todayDataLists, todayData);
        processAllData(summaryInfo, userID, currentMonth);
        processSummaryInfo(summaryInfo, userID, todayDataLists);

        return summaryInfo;
    }

    private void processAllData(SummaryInfo summaryInfo, short userID, LocalDateTime currentMonth) {
        //get all month data
        List<OrderStaticsMonth> allData = getStaticsForOrderStaticsMonthForUser(userID, Utils.getOldTime(), currentMonth);
        if (checkListNotNull(allData)) {
            allData.stream().collect(groupingBy(OrderStaticsMonth::getLocationID)).forEach((locationID, orderStaticsMonth) ->
            {
                Location location = dataStore.queryLocationByLocationID(locationID);
                double income = orderStaticsMonth.stream().collect(summingDouble(OrderStaticsMonth::getIncomeTotal));
                int count = orderStaticsMonth.stream().collect(summingInt(OrderStaticsMonth::getOrderTotal));
                List<PieData> currentMonthPieDataList = summaryInfo.getCurrentMonthPieData().stream().filter(k -> k.getName().equalsIgnoreCase(location.getAreaName())).collect(toList());
                List<PieData> currentMonthOrderPieDataList = summaryInfo.getCurrentMonthOrderPieData().stream().filter(k -> k.getName().equalsIgnoreCase(location.getAreaName())).collect(toList());

                if (checkListNotNull(currentMonthPieDataList)) {
                    PieData pieData = new PieData((int) income + currentMonthPieDataList.get(0).getValue(), location.getAreaName());
                    summaryInfo.addAllPieData(pieData);
                } else {
                    PieData pieData = new PieData((int) income, location.getAreaName());
                    summaryInfo.addAllPieData(pieData);
                }

                if (checkListNotNull(currentMonthOrderPieDataList)) {
                    PieData pieData = new PieData(count + currentMonthOrderPieDataList.get(0).getValue(), location.getAreaName());
                    summaryInfo.addAllOrderPieData(pieData);
                } else {
                    PieData pieData = new PieData(count, location.getAreaName());
                    summaryInfo.addAllOrderPieData(pieData);
                }
            });
        }
    }

    private void processCurrrentMonthData(SummaryInfo summaryInfo, List<OrderStaticsMonth> currentOldData, List<OrderStaticsDay> todayDataLists, Map<Long, List<OrderStaticsDay>> todayData) {
        // get income and order data group by area for today
        if (checkListNotNull(todayDataLists)) {
            todayDataLists.stream().collect(groupingBy(OrderStaticsDay::getLocationID)).forEach((locationID, currentDayOrderStatics) -> {
                Location location = dataStore.queryLocationByLocationID(locationID);
                double income = currentDayOrderStatics.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal));
                int count = currentDayOrderStatics.stream().collect(summingInt(OrderStaticsDay::getOrderTotal));
                PieData pieData = new PieData((int) income, location.getAreaName());
                summaryInfo.addCurrentDayPieData(pieData);

                pieData = new PieData(count, location.getAreaName());
                summaryInfo.addCurrentDayOrderPieData(pieData);
            });
        }

        //当月的统计数据需要加上当日的
        if (checkListNotNull(currentOldData)) {
            currentOldData.stream().forEach(data -> {
                        if (todayData != null && todayData.size() > 0) {
                            List<OrderStaticsDay> todayOrderStatics = todayData.get(data.getDeviceID());
                            if (todayOrderStatics != null && todayOrderStatics.size() > 0) {
                                OrderStaticsDay orderStaticsDay = todayOrderStatics.get(0);
                                data.setIncomeTotal(data.getIncomeTotal() + orderStaticsDay.getIncomeTotal());
                                data.setOrderTotal(data.getOrderTotal() + orderStaticsDay.getOrderTotal());
                            }
                        }
                    }
            );

            //当月数据汇总之后，设置统计值
            currentOldData.stream().collect(groupingBy(OrderStaticsMonth::getLocationID)).forEach((locationID, orderStaticsMonth) ->
            {
                Location location = dataStore.queryLocationByLocationID(locationID);
                double income = orderStaticsMonth.stream().collect(summingDouble(OrderStaticsMonth::getIncomeTotal));
                int count = orderStaticsMonth.stream().collect(summingInt(OrderStaticsMonth::getOrderTotal));
                PieData pieData = new PieData((int) income, location.getAreaName());
                summaryInfo.addCurrentMonthPieData(pieData);

                pieData = new PieData(count, location.getAreaName());
                summaryInfo.addCurrentMonthOrderPieData(pieData);
            });
        }
    }

    private void processSummaryInfo(SummaryInfo summaryInfo, short userID, List<OrderStaticsDay> todayDataLists) {
        //get info card data
        long deviceCount = 0;
        long faultDevices = 0;
        List<Device> deviceList = dataStore.queryDeviceByUser(userID);
        if (checkListNotNull(deviceList)) {
            deviceCount = deviceList.stream().count();
            faultDevices = deviceList.stream().filter(device ->
                    device.getStatus() > DeviceStatus.SERVICE || device.getStatus() == DeviceStatus.DISCONNECTED
            ).collect(Collectors.toList()).stream().count();
        }

        long areaCount = dataStore.queryAllLocation().stream().filter(k -> k.getUserID() == userID).count();

        InfoCardData deviceInfo = new InfoCardData();
        deviceInfo.setTitle("总设备");
        deviceInfo.setCount(deviceCount);
        deviceInfo.setIcon("ios-laptop");
        deviceInfo.setColor("#2d8cf0");

        InfoCardData areaInfo = new InfoCardData();
        areaInfo.setTitle("总网点");
        areaInfo.setCount(areaCount);
        areaInfo.setIcon("ios-planet");
        areaInfo.setColor("#19be6b");

        InfoCardData incomeInfo = new InfoCardData();
        incomeInfo.setTitle("总收益");
        incomeInfo.setCount(summaryInfo.getAllPieData().stream().collect(summingInt(PieData::getValue)).intValue());
        incomeInfo.setIcon("ios-cash");
        incomeInfo.setColor("#ed3f14");

        InfoCardData orderInfo = new InfoCardData();
        orderInfo.setTitle("总订单");
        orderInfo.setCount(summaryInfo.getAllOrderPieData().stream().collect(summingInt(PieData::getValue)).intValue());
        orderInfo.setIcon("ios-people");
        orderInfo.setColor("#E46CBB");

        InfoCardData abnormalOrderInfo = new InfoCardData();
        abnormalOrderInfo.setTitle("异常设备");
        abnormalOrderInfo.setCount(faultDevices);
        abnormalOrderInfo.setIcon("ios-warning-outline");
        abnormalOrderInfo.setColor("#9A66E4");

        int todayIncome = 0;
        int todayOrder = 0;
        if (checkListNotNull(todayDataLists)) {
            todayIncome = todayDataLists.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal)).intValue();
            todayOrder = todayDataLists.stream().collect(summingDouble(OrderStaticsDay::getOrderTotal)).intValue();
        }
        InfoCardData todayIncomeData = new InfoCardData();
        todayIncomeData.setTitle("今日收益");
        todayIncomeData.setCount(todayIncome);
        todayIncomeData.setIcon("ios-cash");
        todayIncomeData.setColor("#ff9900");

        InfoCardData todayOrderData = new InfoCardData();
        todayOrderData.setTitle("今日订单");
        todayOrderData.setCount(todayOrder);
        todayOrderData.setIcon("ios-people");
        todayOrderData.setColor("#3A66E4");

        summaryInfo.addInfoCardData(deviceInfo);
        summaryInfo.addInfoCardData(areaInfo);
        summaryInfo.addInfoCardData(todayIncomeData);
        summaryInfo.addInfoCardData(todayOrderData);
        summaryInfo.addInfoCardData(incomeInfo);
        summaryInfo.addInfoCardData(orderInfo);
        summaryInfo.addInfoCardData(abnormalOrderInfo);
    }
}