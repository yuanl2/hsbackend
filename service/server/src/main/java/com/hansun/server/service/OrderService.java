package com.hansun.server.service;

import com.hansun.dto.Device;
import com.hansun.dto.Location;
import com.hansun.dto.Order;
import com.hansun.dto.User;
import com.hansun.server.common.*;
import com.hansun.server.commu.*;
import com.hansun.server.commu.msg.ServerStartDeviceMsg;

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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.hansun.server.common.MsgConstant.DEVICE_START_MSG;

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

    public void sendMetrics(Order order) {
        metricsService.sendMetrics(HSServiceMetrics
                .builder()
                .measurement("")
                .build());
    }

    public void createStartMsgToDevice(Order order) {
        try {
//            order.setId(Long.valueOf(getSequenceNumber()));
            Device device = dataStore.queryDeviceByDeviceID(order.getDeviceID());
            if (device == null) {
                logger.error("can not create order for device not exist  " + order.getDeviceName());
                throw new ServerException("can not create order for device not exist  " + order.getDeviceName());
            }
            int index = device.getPort();

            String deviceType = MsgUtil.getMsgBodyLength(device.getType(),3);
            //4g device
            if(device.getSimCard().length() == MsgConstant4g.DEVICE_NAME_FIELD_SIZE){

                int portNum =1;
                com.hansun.server.commu.msg4g.ServerStartDeviceMsg msg = new com.hansun.server.commu.msg4g.ServerStartDeviceMsg(MsgConstant4g.DEVICE_START_MSG);
                msg.setDeviceType(deviceType);

                order.setStartTime(Instant.now());
                order.setPrice(dataStore.queryConsume(order.getConsumeType()).getPrice());
                order.setDuration(dataStore.queryConsume(order.getConsumeType()).getDuration());

                if(deviceType.equals("000")){
                    portNum = 4;
                }
                else if(deviceType.equals("100")){
                    portNum = 1;
                }
                Map<Integer, Integer> map = new HashMap<>();
                for (int i = 1; i <= portNum; i++) {
                    if (index == i) {
                        map.put(i, 1);
                    } else {
                        map.put(i, 0);
                    }
                }
                msg.setMap(map);
                Map<Integer, String> times = new HashMap<>();
                for (int i = 1; i <= portNum; i++) {
                    if (index == i) {
                        int duration = order.getDuration();
                        if (duration < 10) {
                            times.put(i, "0" + order.getDuration());
                        } else {
                            times.put(i, order.getDuration() + "");
                        }
                    } else {
                        times.put(i, "00");
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

    public Order createOrder(Order order) {
//        Device device = dataStore.queryDeviceByDeviceID(order.getDeviceID());
//        device.setStatus(DeviceStatus.STARTTASK);
//        dataStore.updateDevice(device);
        Order result =  orderStore.insertOrder(order);
        return result;
    }

    public void processStartOrder(String deviceBoxName, int port) {
        Device d = dataStore.queryDeviceByDeviceBoxAndPort(deviceBoxName, port);
        Order order = orderStore.queryOrder(d.getId());
        if (order != null && order.getOrderStatus() != OrderStatus.SERVICE) {
            logger.info("update order before = " + order);
            order.setOrderStatus(OrderStatus.SERVICE);
            order.setStartTime(Instant.now());
            orderStore.updateOrder(order);

            //不能等心跳消息来了再更新设备的状态，应该根据业务的回应及时更新
            dataStore.updateDevice(d,DeviceStatus.SERVICE);
        } else {
            logger.error(d.getId() + " have no order now");
        }
    }

    public void processFinishOrder(String deviceBoxName, Map<Integer, Integer> map) {
        map.forEach((k, v) -> {
            Device d = dataStore.queryDeviceByDeviceBoxAndPort(deviceBoxName, k);
            Order order = orderStore.queryOrder(d.getId());

            if (order != null && v == DeviceStatus.IDLE) {
                if (Instant.now().isAfter(order.getCreateTime().plus(Duration.ofMinutes(order.getDuration())))
                        || Instant.now().isAfter(order.getStartTime().plus(Duration.ofMinutes(order.getDuration())))) {
                    logger.info("update order before = " + order);
                    order.setOrderStatus(OrderStatus.FINISH);
                    order.setEndTime(Instant.now());
                    orderStore.updateOrder(order);

                    dataStore.updateDevice(d,DeviceStatus.IDLE);

                    //remove order from cache not table
                    orderStore.deleteOrder(d.getId());
                    logger.info("order delete = " + order);
                }
            } else {
                logger.error(d.getId() + " have no order now");
            }

        });

        dataStore.updateDeviceStatus(deviceBoxName, map, "0");
    }

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

    public void updateOrder(Order order) {
        orderStore.updateOrder(order);
    }

    public Order getOrder(Long deviceID) {
        return orderStore.queryOrder(deviceID);
    }

    public Order getOrderByOrderID(String orderID) {
        return orderStore.queryOrder(orderID);
    }

    /**
     * 更新订单状态为完成，并且删除缓存中的订单
     * @param deviceID
     */
    public void deleteOrder(Long deviceID) {
        Order order = orderStore.queryOrder(deviceID);
        if (order != null) {
            order.setEndTime(Instant.now());
            order.setOrderStatus(OrderStatus.FINISH);
            orderStore.updateOrder(order);
            orderStore.deleteOrder(deviceID);
            logger.info("order delete = " + order);
        }
    }

    /**
     * 不更新订单状态为完成，并且删除缓存中的订单
     * @param deviceID
     */
    public void removeOrder(Long deviceID) {
        orderStore.deleteOrder(deviceID);
    }

    public List<OrderDetail> queryOrderByDevice(Long id, Instant startTime, Instant endTime) {
        List<Long> deviceIDs = new ArrayList<>();
        deviceIDs.add(id);
        List<Order> orderList = orderStore.queryByDevice(deviceIDs, startTime, endTime);
        List<OrderDetail> orderDetailList = new ArrayList<>();

        orderList.forEach(
                k -> {
                    try {
                        OrderDetail orderDetail = new OrderDetail(k);
                        Device device = dataStore.queryDeviceByDeviceID(k.getDeviceID());
                        Location location = dataStore.queryLocationByLocationID(device.getLocationID());
                        orderDetail.setAreaName(location.getAreaName());
                        orderDetail.setAddress(location.getAddress());
                        orderDetail.setDeviceName(device.getName());
                        orderDetail.setUser(dataStore.queryUser(device.getOwnerID()).getName());
                        orderDetail.setCity(location.getCity());
                        orderDetail.setProvince(location.getProvince());
                        orderDetailList.add(orderDetail);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
        );
        return orderDetailList;
    }

    public List<OrderDetail> queryOrderByUser(String user, Instant startTime, Instant endTime) {
        List<Device> devices;
        int id = getUserId(user);
        devices = dataStore.queryDeviceByOwner(id);
        List<Long> deviceIDs = new ArrayList<>();
        if (devices != null && devices.size() > 0) {
            devices.forEach(k -> {
                deviceIDs.add(k.getId());
            });
        }
        List<OrderDetail> orderDetailList = new ArrayList<>();
        deviceIDs.forEach(
                k -> orderDetailList.addAll(queryOrderByDevice(k, startTime, endTime))
        );
        return orderDetailList;
    }

    private int getUserId(String user) {
        int id = -1;
        if (!Utils.isNumeric(user)) {
            for (User u : dataStore.queryAllUser()
                    ) {
                if (u.getName().equals(user)) {
                    id = u.getId();
                }
            }

        } else {
            id = Integer.valueOf(user);
        }
        return id;
    }

    private User getUser(String user) {
        if (!Utils.isNumeric(user)) {
            for (User u : dataStore.queryAllUser()
                    ) {
                if (u.getName().equals(user)) {
                    return u;
                }
            }
        } else {
            return dataStore.queryUser(Integer.valueOf(user));
        }
        return null;
    }

    public List<OrderDetail> queryOrderByArea(String id, Instant startTime, Instant endTime) {
        List<Location> locationList = dataStore.queryLocationByAreaID(Integer.valueOf(id));
        List<Long> deviceIDs = new ArrayList<>();
        locationList.forEach(k -> {
            List<Device> lists = dataStore.queryDeviceByLocation(k.getId());
            if (lists != null) {
                lists.forEach(v -> {
                    deviceIDs.add(v.getId());
                });
            }
        });
        List<OrderDetail> orderDetailList = new ArrayList<>();
        deviceIDs.forEach(
                k -> orderDetailList.addAll(queryOrderByDevice(k, startTime, endTime))
        );
        return orderDetailList;
    }

    public OrderStatisticsForUser queryOrderStatisticsByUser(String user, Instant endTime) {
        int id = getUserId(user);
        User u = getUser(id + "");
        return queryOrderStatisticsByUser(u, u.getCreateTime(), endTime);
    }

    public OrderStatisticsForUser queryOrderStatisticsByUser(User user, Instant startTime, Instant endTime) {
        List<Location> locationList = dataStore.queryLocationByUserID(Integer.valueOf(user.getId()));
        List<Long> deviceIDs = new ArrayList<>();
        if (locationList != null && locationList.size() > 0) {
            locationList.forEach(k -> {
                List<Device> lists = dataStore.queryDeviceByLocation(k.getId());
                if (lists != null) {
                    lists.forEach(v -> {
                        deviceIDs.add(v.getId());
                    });
                }
            });
        }

        OrderStatisticsForUser orderStatisticsForUser = new OrderStatisticsForUser();
        orderStatisticsForUser.setUser(user.getName());
        orderStatisticsForUser.setUserId(user.getId());

        try {
            Instant now = Instant.now();
            Date date = Date.from(now);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            final String month = (calendar.get(Calendar.MONTH) + 1) + "月份";
            final int day = calendar.get(Calendar.DAY_OF_MONTH);


            List<OrderStatisticsForDevice> orderStatisticsForDeviceList = new ArrayList<>();
            List<OrderStatisticsForArea> orderStatisticsForAreaList = new ArrayList<>();
            Map<Integer, OrderStatisticsForArea> map = new HashMap<>();
            deviceIDs.forEach(
                    k -> {
                        List<OrderDetail> lists = queryOrderByDevice(k, startTime, endTime);

                        Location location = dataStore.queryLocationByLocationID(dataStore.queryDeviceByDeviceID(k).getLocationID());
                        OrderStatisticsForArea statisticsForArea = map.get(location.getAreaID());
                        if (statisticsForArea == null) {
                            statisticsForArea = new OrderStatisticsForArea();
                            statisticsForArea.setAreaId(location.getAreaID());
                            statisticsForArea.setAreaName(location.getAreaName());
                        }

                        OrderStatisticsForDevice statisticsForDevice = new OrderStatisticsForDevice();
                        statisticsForDevice.setDeviceId(k);
                        statisticsForDevice.setDeviceName(dataStore.queryDeviceByDeviceID(k).getName());

                        //statistics for device
                        lists.forEach(orderDetail -> {
                            statisticsForDevice.addIncomeTotal(orderDetail.getPrice());


                            if (month.equals(orderDetail.getMonth())) {
                                statisticsForDevice.addIncomeTotalOnMonth(orderDetail.getPrice());
                                statisticsForDevice.addOrderTotalOnMonth(1);
                            }
                            if (day == orderDetail.getDay()) {
                                statisticsForDevice.addIncomeTotalOnDay(orderDetail.getPrice());
                                statisticsForDevice.addOrderTotalOnDay(1);
                            }
                        });
                        statisticsForDevice.addOrderTotal(lists.size());
                        statisticsForDevice.addDeviceTotal(1);

                        //statistics for area
                        statisticsForArea.addIncomeTotal(statisticsForDevice.getIncomeTotal());
                        statisticsForArea.addIncomeTotalOnDay(statisticsForDevice.getIncomeTotalOnDay());
                        statisticsForArea.addIncomeTotalOnMonth(statisticsForDevice.getIncomeTotalOnMonth());
                        statisticsForArea.addOrderTotal(statisticsForDevice.getOrderTotal());
                        statisticsForArea.addOrderTotalOnDay(statisticsForDevice.getOrderTotalOnDay());
                        statisticsForArea.addOrderTotalOnMonth(statisticsForDevice.getOrderTotalOnMonth());
                        statisticsForArea.addDeviceTotal(statisticsForDevice.getDeviceTotal());

                        statisticsForArea.addOrderStatisticsForDevices(statisticsForDevice);

                        map.put(location.getAreaID(),statisticsForArea);
                    }
            );
            //statistics for user
            map.forEach((k,v) -> {
                orderStatisticsForUser.addOrderStatisticsForAreas(v);
                orderStatisticsForUser.addDeviceTotal(v.getDeviceTotal());
                orderStatisticsForUser.addOrderTotal(v.getOrderTotal());
                orderStatisticsForUser.addOrderTotalOnDay(v.getOrderTotalOnDay());
                orderStatisticsForUser.addOrderTotalOnMonth(v.getOrderTotalOnMonth());

                orderStatisticsForUser.addIncomeTotal(v.getIncomeTotal());
                orderStatisticsForUser.addIncomeTotalOnDay(v.getIncomeTotalOnDay());
                orderStatisticsForUser.addIncomeTotalOnMonth(v.getIncomeTotalOnMonth());
            });

        } catch (Exception e) {
            logger.error("queryOrderStatisticsByUser error! " + e.getMessage(),e);
        }
        return orderStatisticsForUser;
    }

    public static String getSequenceNumber() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss"); // 12
        String str = sdf.format(d);
        String haomiao = String.valueOf(System.nanoTime());
        str = str + haomiao.substring(haomiao.length() - 6, haomiao.length());
        return str;
    }

    public static String getOrderName() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss"); // 12
        String str = sdf.format(d);
        String haomiao = String.valueOf(System.nanoTime());
        str = str + haomiao.substring(haomiao.length() - 6, haomiao.length());
        return "Free" + str;
    }

}