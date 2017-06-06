package com.hansun.server.commu;

import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.service.DeviceListener;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    }


    public void remove(String id) {
        map.remove(id);
    }

    public IHandler get(String id) {
        return map.get(id);
    }

    public void addDeviceListener(DeviceListener listener) {
        listeners.add(listener);
    }

    public void processHeart(String deviceName) {
        listeners.forEach(l -> l.connnect(deviceName));
    }

    public String getResponseDelay() {
        return hsServiceProperties.getProcessMsgResponseDelay();
    }
}
