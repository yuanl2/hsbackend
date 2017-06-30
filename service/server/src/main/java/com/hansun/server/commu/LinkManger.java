package com.hansun.server.commu;

import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.commu.msg.MsgTime;
import com.hansun.server.service.DeviceListener;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
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

    //因为多线程处理
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

    public SyncAsynMsgController getSyncAsynMsgController() {
        return syncAsynMsgController;
    }


    @PostConstruct
    public void init() {
        logger.info("LinkManger process msg thread pool number = " + hsServiceProperties.getProcessMsgThreadNum());
        executorService = Executors.newFixedThreadPool(Integer.valueOf(hsServiceProperties.getProcessMsgThreadNum()));
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down LinkManger");
        executorService.shutdown();
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


    public void remove(String id) {
        try {
            if (map.containsKey(id)) {
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
                            logger.info(order.getId() + " update order status to " + OrderStatus.FINISH);
                            orderService.deleteOrder(device.getId());
                        } else {
                            logger.info(order.getId() + " remove order " + order);
                            orderService.removeOrder(device.getId());
                        }
                    }
                }
            }
        }
    }

    public String getResponseDelay() {
        return hsServiceProperties.getProcessMsgResponseDelay();
    }
}
