package com.hansun.server.service;

import com.hansun.server.dto.Device;
import com.hansun.server.dto.Location;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.dto.User;
import com.hansun.server.common.*;
import com.hansun.server.commu.*;

import com.hansun.server.db.DataStore;
import com.hansun.server.db.OrderStore;
import com.hansun.server.db.PayAcountStore;
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
    private PayAcountStore payAcountStore;

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
//        Device device = dataStore.queryDeviceByDeviceID(order.getDeviceID());
//        device.setStatus(DeviceStatus.STARTTASK);
//        dataStore.updateDevice(device);
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
        if (order != null) {
            order.setEndTime(Utils.getNowTime());
            order.setOrderStatus(OrderStatus.FINISH);
            orderStore.updateOrder(order);
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


    public List<OrderDetail> queryOrderByTimeOrderNotFinish(String user, LocalDateTime beginTime) {
        if (Utils.convertToInstant(beginTime).isAfter(Instant.now())) {
            return null;
        }

        short userID = getUserId(user);
        List<OrderInfo> orderList = orderStore.queryByTimeRangeOrderNotFinish(userID, beginTime, Utils.getNowTime());
        List<OrderDetail> orderDetailList;

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
                        logger.error(e.getMessage(), e);
                    }
                    return orderDetail;
                }
        ).collect(toList());
        return orderDetailList;

    }

    /**
     * get the raw order data with time range
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByTimeByUser(short userID, LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderInfo> orderList = orderStore.queryByTimeRangeOrderFinish(userID, startTime, endTime);
        List<OrderDetail> orderDetailList;

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
                        logger.error(e.getMessage(), e);
                    }
                    return orderDetail;
                }
        ).collect(toList());
        return orderDetailList;
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
        return queryOrderByTimeByUser(user, startTime, endTime).stream().collect(toList());
    }

    /**
     * get order list with location id
     *
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    public List<OrderDetail> queryOrderByArea(short id, String user, LocalDateTime startTime, LocalDateTime endTime) {
        short userID = getUserId(user);
        return queryOrderByTimeByUser(userID, startTime, endTime).stream().filter(orderDetail ->
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
            return queryOrderByTimeByUser(device.getUserID(), startTime, endTime).stream().filter(orderDetail ->
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
    public OrderStatisticsForUser queryOrderStatisticsByUser(String userName, LocalDateTime startTime, LocalDateTime endTime, int sumType) {

        if (Utils.convertToInstant(startTime).isAfter(Utils.convertToInstant(endTime))) {
            return null;
        }

        short userID = getUserId(userName);
        User user = getUser(userID + "");
        Map<Short, List<Long>> deviceIDMaps = getLocationDeviceMaps(user);

        List<OrderDetail> lists = queryOrderByTimeByUser(userID, startTime, endTime);

        OrderStatisticsForUser orderStatisticsForUser = new OrderStatisticsForUser();
        orderStatisticsForUser.setUser(user.getUsername());
        orderStatisticsForUser.setUserId(user.getId());

        deviceIDMaps.forEach((locationID, deviceIDLists) -> {
            OrderStatisticsForArea orderStatisticsForArea = new OrderStatisticsForArea();
            Location location = dataStore.queryLocationByLocationID(locationID);
            orderStatisticsForArea.setAreaName(location.getAreaName());
            orderStatisticsForArea.setAreaId(location.getAreaID());
            orderStatisticsForArea.setAddress(location.getAddress());
            orderStatisticsForArea.setCity(location.getCity());
            orderStatisticsForArea.setProvince(location.getProvince());

            lists.stream().filter(orderDetail -> orderDetail.getLocationID() == locationID).collect(groupingBy(OrderDetail::getDeviceID))
                    .forEach((deviceID, orderDetailList) -> {
                        OrderStatisticsForDevice orderStatisticsForDevice = new OrderStatisticsForDevice();
                        orderStatisticsForDevice.setDeviceId(deviceID);
                        if (sumType == OrderStaticsType.DAY.getValue()) {
                            orderDetailList.stream().sorted(Comparator.comparing(OrderDetail::getStartTime)).collect(groupingBy(OrderDetail::getsDay)).forEach(
                                    (time, o) -> {
                                        double income = o.stream().collect(summingDouble(OrderDetail::getPrice));
                                        int count = o.size();
                                        OrderStatistics orderStatistics = new OrderStatistics();
                                        orderStatistics.setOrderTotal(count);
                                        orderStatistics.setIncomeTotal(income);
                                        orderStatistics.setTime(time);
                                        orderStatistics.setSumTimeType(OrderStaticsType.DAY.getDesc());
                                        orderStatistics.setDeviceTotal(1);
                                        orderStatistics.setRunningDeviceTotal(1);
                                        orderStatistics.setUser(userName);
                                        orderStatistics.setAreaName(location.getAreaName());
                                        orderStatisticsForDevice.addOrderStatistics(orderStatistics);
                                    }
                            );
                        } else if (sumType == OrderStaticsType.MONTH.getValue()) {
                            orderDetailList.stream().sorted(Comparator.comparing(OrderDetail::getStartTime)).collect(groupingBy(OrderDetail::getsMonth)).forEach(
                                    (time, o) -> {
                                        double income = o.stream().collect(summingDouble(OrderDetail::getPrice));
                                        int count = o.size();
                                        OrderStatistics orderStatistics = new OrderStatistics();
                                        orderStatistics.setOrderTotal(count);
                                        orderStatistics.setIncomeTotal(income);
                                        orderStatistics.setTime(time);
                                        orderStatistics.setSumTimeType(OrderStaticsType.MONTH.getDesc());
                                        orderStatistics.setDeviceTotal(1);
                                        orderStatistics.setRunningDeviceTotal(1);
                                        orderStatistics.setUser(userName);
                                        orderStatistics.setAreaName(location.getAreaName());
                                        orderStatisticsForDevice.addOrderStatistics(orderStatistics);
                                    }
                            );
                        } else {
                            logger.error("Order statics sum type error {}", sumType);
                        }
                        double incomeTotal = orderStatisticsForDevice.getOrderStatistics().stream().collect(summingDouble(OrderStatistics::getIncomeTotal));
                        int orderTotal = orderStatisticsForDevice.getOrderStatistics().stream().collect(summingInt(OrderStatistics::getOrderTotal));
                        orderStatisticsForDevice.setDeviceTotal(1);
                        orderStatisticsForDevice.setRunningDeviceTotal(1);
                        orderStatisticsForDevice.setIncomeTotal(incomeTotal);
                        orderStatisticsForDevice.setOrderTotal(orderTotal);
                        orderStatisticsForDevice.setSumTimeType("ALL");
                        orderStatisticsForDevice.setUser(userName);
                        orderStatisticsForDevice.setAreaName(location.getAreaName());
                        /**
                         * 每个List都是具体某天的数据，汇总计算
                         */
                        orderStatisticsForArea.addOrderStatisticsForDevices(orderStatisticsForDevice);


                    });
            /**
             * 每个List都是具体某个设备的数据，汇总计算
             */
            double incomeTotal = orderStatisticsForArea.getOrderStatisticsForDevices().stream().collect(summingDouble(OrderStatisticsForDevice::getIncomeTotal));
            int orderTotal = orderStatisticsForArea.getOrderStatisticsForDevices().stream().collect(summingInt(OrderStatisticsForDevice::getOrderTotal));
            int runningDeviceTotal = orderStatisticsForArea.getOrderStatisticsForDevices().stream().collect(summingInt(OrderStatisticsForDevice::getRunningDeviceTotal));
            orderStatisticsForArea.setRunningDeviceTotal(runningDeviceTotal);
            orderStatisticsForArea.setDeviceTotal(deviceIDLists.size());
            orderStatisticsForArea.setIncomeTotal(incomeTotal);
            orderStatisticsForArea.setOrderTotal(orderTotal);
            orderStatisticsForArea.setSumTimeType("ALL");
            orderStatisticsForArea.setUser(userName);
            orderStatisticsForArea.setAreaName(location.getAreaName());

            /**
             * 获取该Area下所有Device的统计信息
             */
            List<OrderStatistics> orderStatisticsList = new ArrayList<>();
            orderStatisticsForArea.getOrderStatisticsForDevices().stream().forEach(
                    k -> orderStatisticsList.addAll(k.getOrderStatistics())
            );

            orderStatisticsList.stream().collect(groupingBy(OrderStatistics::getTime)).forEach((time, statistics) -> {
                double income = statistics.stream().collect(summingDouble(OrderStatistics::getIncomeTotal));
                int count = statistics.stream().collect(summingInt(OrderStatistics::getOrderTotal));
                int deviceSize = statistics.stream().collect(summingInt(OrderStatistics::getDeviceTotal));
                OrderStatistics orderStatistics = new OrderStatistics();
                orderStatistics.setOrderTotal(count);
                orderStatistics.setIncomeTotal(income);
                orderStatistics.setTime(time);
                orderStatistics.setSumTimeType(statistics.get(0).getSumTimeType());
                orderStatistics.setDeviceTotal(orderStatisticsForArea.getDeviceTotal());
                orderStatistics.setRunningDeviceTotal(deviceSize);
                orderStatistics.setUser(userName);
                orderStatistics.setAreaName(location.getAreaName());
                orderStatisticsForArea.addOrderStatistics(orderStatistics);
            });

            orderStatisticsForUser.addOrderStatisticsForAreas(orderStatisticsForArea);
        });

        double incomeTotal = orderStatisticsForUser.getOrderStatisticsForAreas().stream().collect(summingDouble(OrderStatisticsForArea::getIncomeTotal));
        int orderTotal = orderStatisticsForUser.getOrderStatisticsForAreas().stream().collect(summingInt(OrderStatisticsForArea::getOrderTotal));
        int deviceTotal = orderStatisticsForUser.getOrderStatisticsForAreas().stream().collect(summingInt(OrderStatisticsForArea::getDeviceTotal));
        int runningDeviceTotal = orderStatisticsForUser.getOrderStatisticsForAreas().stream().collect(summingInt(OrderStatisticsForArea::getRunningDeviceTotal));
        orderStatisticsForUser.setDeviceTotal(deviceTotal);
        orderStatisticsForUser.setRunningDeviceTotal(runningDeviceTotal);
        orderStatisticsForUser.setIncomeTotal(incomeTotal);
        orderStatisticsForUser.setOrderTotal(orderTotal);
        orderStatisticsForUser.setSumTimeType("ALL");
        orderStatisticsForUser.setUser(userName);
        /**
         *
          */
        List<OrderStatistics> orderStatisticsList = new ArrayList<>();
        orderStatisticsForUser.getOrderStatisticsForAreas().stream().forEach(
                k -> orderStatisticsList.addAll(k.getOrderStatistics())
        );

        orderStatisticsList.stream().collect(groupingBy(OrderStatistics::getTime)).forEach((time, statistics) -> {
            double income = statistics.stream().collect(summingDouble(OrderStatistics::getIncomeTotal));
            int count = statistics.stream().collect(summingInt(OrderStatistics::getOrderTotal));
            int deviceSize = statistics.stream().collect(summingInt(OrderStatistics::getRunningDeviceTotal));
            OrderStatistics orderStatistics = new OrderStatistics();
            orderStatistics.setOrderTotal(count);
            orderStatistics.setIncomeTotal(income);
            orderStatistics.setTime(time);
            orderStatistics.setSumTimeType(statistics.get(0).getSumTimeType());
            orderStatistics.setDeviceTotal(orderStatisticsForUser.getDeviceTotal());
            orderStatistics.setRunningDeviceTotal(deviceSize);
            orderStatistics.setUser(userName);
            orderStatisticsForUser.addOrderStatistics(orderStatistics);
        });

        return orderStatisticsForUser;
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

}