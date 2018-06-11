package com.hansun.server.service;

import com.hansun.dto.Consume;
import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.db.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanl2 on 2017/6/13.
 */
@Service
public class TimerService {

    private final static Logger logger = LoggerFactory.getLogger(TimerService.class);

    @Autowired
    private HSServiceProperties hsServiceProperties;

    private ExecutorService executor = null;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DataStore dataStore;


    private int min;
    private int max;

    private volatile boolean flag;

    private Object object;

    @PostConstruct
    private void init() {
        object = new Object();
        executor = Executors.newFixedThreadPool(30);
        min = hsServiceProperties.getOrderIntervalMin();
        max = hsServiceProperties.getOrderIntervalMax();
        flag = hsServiceProperties.getOrderIntervalFlag();
        Set<String> deviceBoxs = dataStore.getAllDeviceBoxes();

        if(deviceBoxs != null && deviceBoxs.size()> 0){
            Set<Long> deviceList = dataStore.getAllDevices();
            if (deviceList != null && deviceList.size() > 0) {

                deviceBoxs.forEach(k->{

                    final List<Long> lists = new ArrayList<>();
                    deviceList.forEach(d->{
                        Device device = dataStore.queryDeviceByDeviceID(d);
                        if(device.getSimCard().equalsIgnoreCase(k)){
                            lists.add(d);
                        }

                    });

                    executor.submit(new Schedule_Task(lists,k));

                });

            }
        }

    }

    @PreDestroy
    private void destroy() {
        executor.shutdown();
    }


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        synchronized (object) {
            this.flag = flag;
            object.notifyAll();
        }
    }

    private class Schedule_Task implements Runnable {

        private List<Long> device_id;

        private String boxName;
        public Schedule_Task(List<Long> device_id, String boxName) {
            this.device_id = device_id;
            this.boxName = boxName;
        }

        public void run() {
            boolean threadFlag;
            Random random = new Random();
            while (!Thread.currentThread().interrupted()) {
                try {
                    synchronized (object) {
                        if (!isFlag()) {
                            logger.info("boxName = " + boxName + " start wait for false");
                            object.wait();
                            logger.info("boxName = " + boxName + " continue run for true");
                        }
                    }

                    for (Long deviceID:
                            device_id) {
                        int type = random.nextInt(4);
                        Device d = dataStore.queryDeviceByDeviceID(deviceID);
                        List<Consume> consumeList = dataStore.queryAllConsumeByDeviceType(String.valueOf(d.getType()));
                        Consume consume = consumeList.get(type);

                        logger.debug("queryDeviceByDeviceID = " + d.getId() + " status " + d.getStatus());

                        if (d.getStatus() == DeviceStatus.SERVICE || d.getStatus() == DeviceStatus.DISCONNECTED || d.getStatus() == DeviceStatus.BADNETWORK ||
                                d.getStatus() == DeviceStatus.INVALID) {
                            Thread.sleep((random.nextInt(5) + 10) * 1000);
                        } else {
                            logger.info("queryDeviceByDeviceID = " + d.getId() + " status " + d.getStatus());
                            Order order = new Order();
                            order.setOrderName("ordername-" + orderService.getSequenceNumber());
                            order.setStartTime(Instant.now());
                            order.setCreateTime(Instant.now());
                            order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
                            order.setOrderStatus(OrderStatus.CREATED);
                            order.setDeviceID(Long.valueOf(deviceID));
                            order.setDeviceName(boxName);
                            order.setConsumeType(Short.valueOf(consume.getId()));
                            orderService.createOrder(order);
                            logger.info("device_id = " + deviceID + " start order " + order);
                            Thread.sleep((random.nextInt(5) + 10) * 1000);
                        }
                    }
                } catch (Exception e) {
                    logger.error("device_id = " + device_id, e);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
