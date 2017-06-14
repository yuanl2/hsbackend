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
    private final static Logger logger = LoggerFactory.getLogger(HeartBeatService.class);

    @Autowired
    private HSServiceProperties hsServiceProperties;

    private ExecutorService executor = null;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DataStore dataStore;

    private List<Consume> consumeList;

    private int min;
    private int max;
    private volatile boolean flag;
    private Object object;

    @PostConstruct
    private void init() {
        object = new Object();
        consumeList = dataStore.queryAllConsume();
        executor = Executors.newFixedThreadPool(10);
        min = hsServiceProperties.getOrderIntervalMin();
        max = hsServiceProperties.getOrderIntervalMax();
        flag = hsServiceProperties.getOrderIntervalFlag();
        Set<String> deviceBoxes = dataStore.getAllDeviceBoxes();

        if (deviceBoxes != null && deviceBoxes.size() > 0) {
            deviceBoxes.forEach(k -> executor.submit(new Schedule_Task(k, dataStore.queryDeviceByDeviceBox(k))));
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
        private String deviceBox;
        private List<Device> deviceList;

        public Schedule_Task(String deviceBox, List<Device> deviceList) {
            this.deviceBox = deviceBox;
            this.deviceList = deviceList;
        }

        public void run() {
            logger.info("deviceBox = " + deviceBox + " start run");
            while (!Thread.currentThread().interrupted()) {
                try {
                    synchronized (object) {
                        if (!isFlag()) {
                            logger.info("deviceBox = " + deviceBox + " start wait for false");
                            object.wait();
                            logger.info("deviceBox = " + deviceBox + " continue run for true");
                        }
                    }
                    Random random = new Random();
                    int maxConsumeDuration = 0;
                    for (Device device : deviceList) {
                        Long device_id = device.getId();
                        if (device.getStatus() != DeviceStatus.IDLE) {
                            Thread.sleep(3000);
                            continue;
                        } else {
                            Thread.sleep(5000);
                            int type = random.nextInt(4);
                            Consume consume = consumeList.get(type);

                            int time = Integer.valueOf(consume.getDuration());
                            if (time > maxConsumeDuration) {
                                maxConsumeDuration = time;
                            }
                            Order order = new Order();
                            order.setOrderName("ordername-" + orderService.getSequenceNumber());
                            order.setStartTime(Instant.now());
                            order.setCreateTime(Instant.now());
                            order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
                            order.setOrderStatus(OrderStatus.CREATED);
                            order.setDeviceID(device_id);
                            order.setConsumeType(Integer.valueOf(consume.getId()));
                            orderService.createOrder(order);
                            logger.info("device_id = " + device_id + " start order " + order);
                        }
                    }//end for

                    //wait for task finish
                    Thread.sleep(maxConsumeDuration * 60 * 1000);

                    int sleep = random.nextInt(max - min) + min;

                    //wait for task finish
                    Thread.sleep(sleep * 60 * 1000);
                } catch (Exception e) {
                    logger.error("deviceBox = " + deviceBox, e);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }//end try
            }//end while
        }// end run
    }
}
