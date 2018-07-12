package com.hansun.server.commu;

import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.common.DeviceManagerStatus;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.commu.common.IMsg;
import com.hansun.server.commu.common.IMsg4g;
import com.hansun.server.commu.common.MsgTime;
import com.hansun.server.metrics.HSServiceMetrics;
import com.hansun.server.metrics.HSServiceMetricsService;
import com.hansun.server.metrics.Metrics;
import com.hansun.server.service.DeviceListener;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hansun.server.common.MsgConstant4g.DEVICE_START_FINISH_MSG;

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

    @Autowired
    private HSServiceMetricsService hsServiceMetricsService;

    @PostConstruct
    public void init() {
        logger.info("LinkManger process msg thread pool number = {}", hsServiceProperties.getProcessMsgThreadNum());
        executorService = Executors.newFixedThreadPool(Integer.valueOf(hsServiceProperties.getProcessMsgThreadNum()));
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down LinkManger and close handler for device size = {}", map.size());

        long begin = System.currentTimeMillis();
        map.forEach((k, v) -> {
            if (v.isHasConnected()) {
                try {
                    logger.info("server down will close link for {}", v.getDeviceName());
                    v.handleClose();
                } catch (IOException e) {
                    logger.error("{} close error", v.getDeviceName(), e);
                }
                updateDeviceLogoutTime(k);
            } else {
                logger.debug("status is disconnected for {}", v.getDeviceName());
            }
        });
        executorService.shutdown();
        long end = System.currentTimeMillis();
        logger.info("Shutting down LinkManger consume time = {} ms", (end - begin));
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
        try {
            logger.info("LinkManger add deviceSimCard = {} address on {}", id, handler.getSocketChannel().getRemoteAddress());
        } catch (IOException e) {
            logger.error("{} get socketaddress error", id);
        }
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
                        k.setLogoutTime(time);
                        deviceService.updateDevice(k, DeviceStatus.DISCONNECTED);
                    });
                }
                IHandler handler = map.remove(id);
                logger.info("LinkManger remove deviceSim {} address on {} ", id, handler.getSocketChannel().getRemoteAddress());
            } else {
                logger.error("LinkManger not contains deviceSimCard = {}", id);
            }
        } catch (Exception e) {
            logger.error("LinkManger not contains deviceSimCard = {}", id);
        }
    }

    public IHandler get(String id) {
        return map.get(id);
    }

    public void addDeviceListener(DeviceListener listener) {
        listeners.add(listener);
    }

    public void processHeart(String deviceName, Map map, Map portMap, String dup, IMsg msg) {
        //connect 只更新设备的ID在状态圈的移动
        listeners.forEach(l -> l.connnect(deviceName, map, dup));
        //需要判断当前设备所有端口是否还有订单，和设备运行时间是否相符合
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        updateOrderStatus(map, portMap, deviceList, dup, msg);
    }

    public void updateDeviceLoginTime(String deviceName, Map<Integer, Byte> map) {
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setLoginTime(Instant.now());
                deviceService.updateDevice(device, map.get((int)device.getPort()));
            }
        }
    }

    public void updateDeviceLoginTime(String deviceName, short loginReason, short signal, Map<Integer, Byte> map) {
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setLoginTime(Instant.now());
                device.setLoginReason(loginReason);
                device.setSignal(signal);
                deviceService.updateDevice(device, map.get((int)device.getPort()));
            }
        }
    }

    public void updateDeviceLogoutTime(String deviceName) {
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setLogoutTime(Instant.now());
                //如果服务器重启，设备需要设置状态为断链
                deviceService.updateDevice(device, DeviceStatus.DISCONNECTED);
            }
        }
    }

    private void updateOrderStatus(Map map, Map portMap, List<Device> deviceList, String dup, IMsg msg) {
        if (deviceList != null) {
            for (Device device : deviceList) {
                Order order = orderService.getOrder(device.getId());
                MsgTime msgTime = (MsgTime) portMap.get((int)device.getPort());
                byte status = (Byte) map.get((int)device.getPort());
                byte setStatus = device.getStatus();

                logger.info("status {} msgtime {} runtime {} order {} ", status, msgTime.getTime(), msgTime.getRuntime(), order);

                if (order != null && (msgTime.getTime() == 0 || status == DeviceStatus.IDLE)) {
                    //如果订单还在缓存中，但是结束时在当前时间之前，则需要从缓存中删除该订单
                    if (Instant.now().isAfter(order.getCreateTime().plus(Duration.ofMinutes(order.getDuration())))
                            || Instant.now().isAfter(order.getStartTime().plus(Duration.ofMinutes(order.getDuration())))) {
                        //设备没有收到后续结束报文，所以收到心跳消息，判断当前设备是否还在运行，如果指示时间为0，而订单是运行中，则更新订单为finish
                        if (order.getOrderStatus() == OrderStatus.SERVICE) {
                            logger.info("{} update order status from service to {}", order.getId(), OrderStatus.FINISH);

                            sendMetrics(device, order);

                            orderService.deleteOrder(device.getId());
                            setStatus = DeviceStatus.IDLE;
                        } else if (order.getOrderStatus() == OrderStatus.FINISH) {
                            logger.error("order = {} has finished! Delete error", order);
                            orderService.deleteOrder(device.getId());
                        }
                        /**
                         * 如果订单支付后，但是收到的状态空闲，说明下发的订单未执行
                         * 订单状态不需要更新，删除缓存中的订单数据
                         */
                        else if(order.getOrderStatus() == OrderStatus.PAYDONE){
                            logger.info("{} not running {} status is PAYDONE ", device.getId(), order.getId());
                            orderService.removeOrder(device.getId());
                        }
                        else {
                            logger.error("{} update order status from start to {}", order.getId(), OrderStatus.FINISH);
                            orderService.deleteOrder(device.getId());
                            setStatus = DeviceStatus.IDLE;
                        }
                    }else{
                        logger.info("order {} setstatus {} status {}", order, setStatus, status);
                    }
                } else if (order != null && (status == DeviceStatus.SERVICE || msgTime.getTime() != 0)) {
                    if (order.getOrderStatus() == OrderStatus.NOTSTART || order.getOrderStatus() == OrderStatus.PAYDONE) {
                        logger.info("update order before = {}", order);
                        order.setOrderStatus(OrderStatus.SERVICE);
                        if(order.getStartTime() == null) {
                            order.setStartTime(Instant.now());
                        }
                        orderService.updateOrder(order);

                        if (device.getStatus() != DeviceStatus.SERVICE) {

                            if (msg.getMsgType().equals(DEVICE_START_FINISH_MSG)) {
                                device.setSeq(Short.valueOf(msg.getSeq()));
                                logger.info("{} set task seq = {}", device.getId(), device.getSeq());
                            }

                            //不能等心跳消息来了再更新设备的状态，应该根据业务的回应及时更新
                            setStatus = DeviceStatus.SERVICE;
                        }
                    } else if (order.getOrderStatus() == OrderStatus.FINISH) {
                        logger.error("order = {} status is error. Has finished!", order);
                    }
                } else {
                    logger.debug("{} have no order now", device.getId());
                    if (status == DeviceStatus.IDLE) {
                        setStatus = status;
                    }
                }

                if (status == DeviceStatus.DISCONNECTED) {
                    device.setLogoutTime(Instant.now());
                    setStatus = status;
                } else if (Integer.valueOf(dup) > 1) {
                    setStatus = DeviceStatus.BADNETWORK;
                }
                deviceService.updateDevice(device, setStatus);
            }
        }

    }

    private void sendMetrics(Device device, Order order) {
        //如果设备的管理状态是测试，则不发送metrics统计信息
        if (device.getManagerStatus() != DeviceManagerStatus.TEST.getStatus()) {
            HSServiceMetrics.Builder builder = HSServiceMetrics.builder();
            builder.measurement(Metrics.ORDER_FINISH).device(String.valueOf(device.getId())).area(device.getAreaName()).user(device.getOwner())
                    .count(1).duration(order.getDuration()).price(order.getPrice());
            hsServiceMetricsService.sendMetrics(builder.build());
        }
    }

    public String getResponseDelay() {
        return hsServiceProperties.getProcessMsgResponseDelay();
    }

    public void initialHandler(String deviceName, IHandler handler) {
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null) {
            deviceList.forEach(device -> {
                handler.setSeq(device.getSeq());
                logger.info("initial {} set seq = {}", device.getId(), device.getSeq());
            });
        }
    }
//
//    public void setTaskSeq(IHandler handler, Map map, Map portMap, int seq) {
//        List<Device> deviceList = deviceService.getDevicesByDeviceBox(handler.getDeviceName());
//        if (deviceList != null) {
//            deviceList.forEach(device -> {
//                //if the device has task running
//                if ((Integer) map.get(device.getPort()) == DeviceStatus.SERVICE
//                        && ((MsgTime) portMap.get(device.getPort())).getTime() != 0) {
//                    device.setSeq(seq);
//                    deviceService.updateDevice(device, device.getStatus());
//                    logger.info("{} set task seq = {}", device.getId(), device.getSeq());
//                }
//                else{
//                    logger.info("{} task failed", device.getId());
//                }
//            });
//        }
//    }
}