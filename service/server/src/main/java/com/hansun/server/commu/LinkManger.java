package com.hansun.server.commu;

import com.hansun.server.common.*;
import com.hansun.server.dto.Device;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.commu.common.IMsg;
import com.hansun.server.commu.common.MsgTime;
import com.hansun.server.metrics.HSServiceMetrics;
import com.hansun.server.metrics.HSServiceMetricsService;
import com.hansun.server.metrics.Metrics;
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
import java.time.Instant;
import java.util.ArrayList;
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
            logger.debug("LinkManger add deviceSimCard = {} address on {}", id, handler.getSocketChannel().getRemoteAddress());
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
                        k.setLogoutTime(Utils.convertToLocalDateTime(time));
                        deviceService.updateDevice(k, DeviceStatus.DISCONNECTED);
                    });
                }
                IHandler handler = map.remove(id);
                logger.debug("LinkManger remove deviceSim {} address on {} ", id, handler.getSocketChannel().getRemoteAddress());
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
                device.setLoginTime(Utils.getNowTime());
                deviceService.updateDevice(device, map.get((int)device.getPort()));
            }
        }
    }

    public void updateDeviceLoginTime(String deviceName, short loginReason, short signal, Map<Integer, Byte> map, String version) {
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setLoginTime(Utils.getNowTime());
                device.setLoginReason(loginReason);
                device.setSignal(signal);
                device.setVersion(version);
                deviceService.updateDevice(device, map.get((int)device.getPort()));
            }
        }
    }

    public void updateDeviceLogoutTime(String deviceName) {
        List<Device> deviceList = deviceService.getDevicesByDeviceBox(deviceName);
        if (deviceList != null && deviceList.size() > 0) {
            for (Device device : deviceList) {
                device.setLogoutTime(Utils.getNowTime());
                //如果服务器重启，设备需要设置状态为断链
                deviceService.updateDevice(device, DeviceStatus.DISCONNECTED);
            }
        }
    }

    private void updateOrderStatus(Map map, Map portMap, List<Device> deviceList, String dup, IMsg msg) {
        if (deviceList != null) {
            for (Device device : deviceList) {
                OrderInfo order = orderService.getOrder(device.getDeviceID());
                MsgTime msgTime = (MsgTime) portMap.get((int)device.getPort());
                byte status = (Byte) map.get((int)device.getPort());
                byte setStatus = device.getStatus();

                logger.info("status {} msgtime {} runtime {} order {} ", status, msgTime.getTime(), msgTime.getRuntime(), order);

                if (order != null && (msgTime.getTime() == 0 || status == DeviceStatus.IDLE)) {
                    if (Utils.isOrderFinished(order)) {
                        if (order.getOrderStatus() == OrderStatus.SERVICE) {
                            logger.info("{} update order status from service to {}", order.getId(), OrderStatus.FINISH);

                            /**
                             * 测试订单不需要记录
                             */
                            if(order.getOrderType() == OrderType.OPERATIONS.getType()) {
                                sendMetrics(device, order);
                            }

                            orderService.deleteOrder(device.getDeviceID());
                            setStatus = DeviceStatus.IDLE;
                        } else if (order.getOrderStatus() == OrderStatus.FINISH) {
                            logger.error("order = {} has finished! Delete error", order);
                            orderService.deleteOrder(device.getDeviceID());
                        }
                        /**
                         * 如果订单支付后，但是收到的状态空闲，说明下发的订单未执行
                         * 订单状态不需要更新，删除缓存中的订单数据
                         */
                        else if(order.getOrderStatus() == OrderStatus.PAYDONE){
                            logger.info("{} not running {} status is PAYDONE ", device.getDeviceID(), order.getId());
                            order.setEndTime(Utils.getNowTime());
                            order.setOrderStatus(OrderStatus.NOTSTART);
                            orderService.updateOrder(order);
                            orderService.removeOrder(device.getDeviceID());
                            setStatus = DeviceStatus.IDLE;
                            /**
                             * 对于支付成功未下发的订单，属于异常，后续做处理
                             */

                        }
                        else if(order.getOrderStatus() == OrderStatus.USER_PAY_FAIL){
                            logger.info("{} not running {} status is USER_PAY_FAIL ", device.getDeviceID(), order.getId());
                            order.setEndTime(Utils.getNowTime());
                            orderService.updateOrder(order);
                            orderService.removeOrder(device.getDeviceID());
                            setStatus = DeviceStatus.IDLE;
                            /**
                             * 对于交易失败，但是下发了，也是异常数据
                             */
                        }
                        else {
                            logger.error("{} update order status from start to {}", order.getId(), OrderStatus.FINISH);
                            orderService.deleteOrder(device.getDeviceID());
                            setStatus = DeviceStatus.IDLE;
                        }
                    }else{
                        /**
                         * 如果订单在支付了3分钟之后设备还没下发任务，则设置为已支付未启动状态
                         * 该订单状态为异常
                         */
                        if(order.getOrderStatus() == OrderStatus.PAYDONE && Utils.isOrderStarted(order,180)){
                            logger.info("{} Order is not Finished not running {} status is PAYDONE ", device.getDeviceID(), order.getId());
                            order.setEndTime(Utils.getNowTime());
                            order.setOrderStatus(OrderStatus.NOTSTART);
                            orderService.updateOrder(order);
                            orderService.removeOrder(device.getDeviceID());
                            setStatus = DeviceStatus.IDLE;
                            //TODO

                        }
                        logger.info("order {} setstatus {} status {}", order, setStatus, status);
                    }
                } else if (order != null && (status == DeviceStatus.SERVICE || msgTime.getTime() != 0)) {
                    if (order.getOrderStatus() == OrderStatus.NOTSTART || order.getOrderStatus() == OrderStatus.PAYDONE) {
                        logger.debug("update order before = {}", order);
                        order.setOrderStatus(OrderStatus.SERVICE);
                        if(order.getStartTime() == null) {
                            order.setStartTime(Utils.getNowTime());
                        }
                        orderService.updateOrder(order);

                        if (device.getStatus() != DeviceStatus.SERVICE) {
                            if (msg.getMsgType().equals(DEVICE_START_FINISH_MSG)) {
                                device.setSeq(Short.valueOf(msg.getSeq()));
                                logger.debug("{} set task seq = {}", device.getDeviceID(), device.getSeq());
                            }

                            //不能等心跳消息来了再更新设备的状态，应该根据业务的回应及时更新
                            setStatus = DeviceStatus.SERVICE;
                        }
                    } else if (order.getOrderStatus() == OrderStatus.FINISH) {
                        logger.error("order = {} status is error. Has finished!", order);
                    } else if( order.getOrderStatus() == OrderStatus.USER_PAY_FAIL){
                        logger.error("order = {} status is error. Pay failed", order);
                    }
                } else {
                    logger.debug("{} have no order now", device.getDeviceID());
                    if (status == DeviceStatus.IDLE) {
                        setStatus = status;
                    }
                }

                if (status == DeviceStatus.DISCONNECTED) {
                    device.setLogoutTime(Utils.getNowTime());
                    setStatus = status;
                } else if (Integer.valueOf(dup) > 1) {
                    setStatus = DeviceStatus.BADNETWORK;
                }
                deviceService.updateDevice(device, setStatus);
            }
        }

    }

    private void sendMetrics(Device device, OrderInfo order) {
        //如果设备的管理状态是测试，则不发送metrics统计信息
        if (device.getManagerStatus() != DeviceManagerStatus.TEST.getStatus()) {
            HSServiceMetrics.Builder builder = HSServiceMetrics.builder();
            builder.measurement(Metrics.ORDER_FINISH).device(String.valueOf(device.getDeviceID())).area(device.getAreaName()).user(device.getUser())
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
                logger.debug("initial {} set seq = {}", device.getDeviceID(), device.getSeq());
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