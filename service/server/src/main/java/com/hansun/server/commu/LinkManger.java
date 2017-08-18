package com.hansun.server.commu;

import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.commu.common.MsgTime;
import com.hansun.server.service.DeviceListener;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanl2 on 2017/5/10.
 */
@Service
public class LinkManger {
    private final static Logger logger = LoggerFactory.getLogger(LinkManger.class);

    private List<DeviceListener> listeners = new ArrayList<>();

    private Map<String, IHandler> map = new ConcurrentHashMap<>();

    @Autowired
    private OrderService orderService;

    @Autowired
    private HSServiceProperties hsServiceProperties;

    private ExecutorService executorService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SyncAsynMsgController syncAsynMsgController;

    @PostConstruct
    public void init() {
        logger.info("LinkManger process msg thread pool number = " + hsServiceProperties.getProcessMsgThreadNum());
        executorService = Executors.newFixedThreadPool(Integer.valueOf(hsServiceProperties.getProcessMsgThreadNum()));
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down LinkManger and close handler for device size = " + map.size());

        long begin = System.currentTimeMillis();
        map.forEach((k, v) -> {
            if (v.isHasConnected()) {
                try {
                    logger.info("server down will close link for " + v.getDeviceName());
                    v.handleClose();
                } catch (IOException e) {

                }
                updateDeviceLogoutTime(k);
            }
            else{
                logger.debug("status is disconnected for " + v.getDeviceName());
            }
        });
        executorService.shutdown();
        long end = System.currentTimeMillis();
        logger.info("Shutting down LinkManger consume time = " + ( end - begin ) + " ms");
    }

    public SyncAsynMsgController getSyncAsynMsgController() {
        return syncAsynMsgController;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void process(DeviceTask task) {
        executorService.submit(task);
    }

    public void add(String id, IHandler handler) {
        map.put(id, handler);
        logger.info("LinkManger add deviceSimCard = " + id);
    }

    public boolean isValidDevice(String deviceBox) {
        return deviceService.containDeviceBox(deviceBox);
    }

    public void remove(String id, final Instant time) {
        try {
            if (map.containsKey(id)) {
                List<Device> list = deviceService.getDevicesByDeviceBox(id);
                if (list != null && list.size() > 0) {
                    //对于设备重连的情况，需要先设置设备的logout时间和状态，等连上后再更新
                    list.forEach(k -> {
                        k.setStatus(DeviceStatus.DISCONNECTED);
                        k.setLogoutTime(time);
                        deviceService.updateDevice(k);
                    });
                }
                map.remove(id);
                logger.info("LinkManger remove deviceSim = " + id);
            } else {
                logger.error("LinkManger not contains deviceSimCard = " + id);
            }
        } catch (Exception e) {
            logger.error("LinkManger not contains deviceSimCard = " + id);
        }
    }

    public IHandler get(String id) {
        return map.get(id);
    }

    public void addDeviceListener(DeviceListener listener) {
        listeners.add(listener);
    }

    public void processHeart(String deviceName, Map map, Map portMap, String dup) {
        listeners.forEach(l -> l.connnect(deviceName, map, dup));
        //需要判断当前设备所有端口是否还有订单，和设备运行时间是否相符合
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        updateOrderStatus(portMap, deviceList);
    }

    public void updateDeviceLoginTime(String deviceName){
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setStatus(DeviceStatus.IDLE);
                device.setLoginTime(Instant.now());
                deviceService.updateDevice(device);
            }
        }
    }

    public void updateDeviceLogoutTime(String deviceName){
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setLogoutTime(Instant.now());
                deviceService.updateDevice(device);
            }
        }
    }

    private void updateOrderStatus(Map portMap, List<Device> deviceList) {
        if (deviceList != null) {
            for (Device device : deviceList) {
                Order order = orderService.getOrder(device.getId());
                MsgTime msgTime = (MsgTime) portMap.get(device.getPort());

                if (order != null && msgTime.getTime() == 0) {
                    //如果订单还在缓存中，但是结束时在当前时间之前，则需要从缓存中删除该订单
                    if (Instant.now().isAfter(order.getCreateTime().plus(Duration.ofMinutes(order.getDuration())))
                            || Instant.now().isAfter(order.getStartTime().plus(Duration.ofMinutes(order.getDuration())))) {
                        //设备没有收到后续结束报文，所以收到心跳消息，判断当前设备是否还在运行，如果指示时间为0，而订单是运行中，则更新订单为finish
                        if (order.getOrderStatus() == OrderStatus.SERVICE) {
                            logger.info(order.getId() + " update order status from service to " + OrderStatus.FINISH);
//                            order.setOrderStatus(OrderStatus.FINISH);
//                            order.setEndTime(Instant.now());
//                            orderService.updateOrder(order);

                            device.setStatus(DeviceStatus.IDLE);
                            orderService.deleteOrder(device.getId());
                            deviceService.updateDevice(device);

//                            logger.info("order delete = " + order);
                        } else if(order.getOrderStatus() == OrderStatus.FINISH){
                            logger.error("order = " + order + " has finished! Delete error");
                            orderService.deleteOrder(device.getId());
                        } else {
                            logger.error(order.getId() + " update order status from start to " + OrderStatus.FINISH);
//                            order.setOrderStatus(OrderStatus.FINISH);
//                            order.setEndTime(Instant.now());
//                            orderService.updateOrder(order);

                            device.setStatus(DeviceStatus.IDLE);
                            orderService.deleteOrder(device.getId());
                            deviceService.updateDevice(device);

//                            logger.info("order delete = " + order);
                        }
                    }
                } else if (order != null && msgTime.getTime() != 0) {
                    if (order.getOrderStatus() == OrderStatus.START) {
                        logger.info("update order before = " + order);
                        order.setOrderStatus(OrderStatus.SERVICE);
                        order.setStartTime(Instant.now());
                        orderService.updateOrder(order);

                        if (device.getStatus() != DeviceStatus.SERVICE) {
                            //不能等心跳消息来了再更新设备的状态，应该根据业务的回应及时更新
                            device.setStatus(DeviceStatus.SERVICE);
                            deviceService.updateDevice(device);
                        }
                    } else if (order.getOrderStatus() == OrderStatus.FINISH) {
                        logger.error("order = " + order + " status is error. Has finished!");
                    }
                } else {
                    logger.debug(device.getId() + " have no order now");
                }
            }
        }
    }

    public String getResponseDelay() {
        return hsServiceProperties.getProcessMsgResponseDelay();
    }
}