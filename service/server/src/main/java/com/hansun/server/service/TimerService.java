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

    private final static Logger logger = LoggerFactory.getLogger(TimerService.class);

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
        Set<Long> deviceList = dataStore.getAllDevices();
        if (deviceList != null && deviceList.size() > 0) {
            deviceList.forEach(k -> executor.submit(new Schedule_Task(k)));
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

        private long device_id;

        public Schedule_Task(long device_id) {
            this.device_id = device_id;
        }

        public void run() {
            boolean threadFlag;
            logger.info("device_id = " + device_id + " start run");
            Random random = new Random();
            while (!Thread.currentThread().interrupted()) {
                try {
                    synchronized (object) {
                        if (!isFlag()) {
                            logger.info("device_id = " + device_id + " start wait for false");
                            object.wait();
                            logger.info("device_id = " + device_id + " continue run for true");
                        }
                    }
                    int type = random.nextInt(4);
                    Consume consume = consumeList.get(type);
                    Device d = dataStore.queryDeviceByDeviceID(device_id);

//                    logger.info("queryDeviceByDeviceID = " + d.getId() + " status " + d.getStatus());

                    if (d.getStatus() == DeviceStatus.SERVICE || d.getStatus() == DeviceStatus.DISCONNECTED) {
                        Thread.sleep((random.nextInt(5) + 10) * 1000);
                    } else {
                        logger.info("queryDeviceByDeviceID = " + d.getId() + " status " + d.getStatus());
                        Order order = new Order();
                        order.setOrderName("ordername-" + orderService.getSequenceNumber());
                        order.setStartTime(Instant.now());
                        order.setCreateTime(Instant.now());
                        order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
                        order.setOrderStatus(OrderStatus.CREATED);
                        order.setDeviceID(Long.valueOf(device_id));
                        order.setConsumeType(Integer.valueOf(consume.getId()));
                        orderService.createOrder(order);
                        logger.info("device_id = " + device_id + " start order " + order);

//                        //wait for task finish
//                        Thread.sleep(consume.getDuration() * 60 * 1000);
//
//                        int sleep = random.nextInt(max - min) + min;
//
//                        //wait for task finish
//                        Thread.sleep(sleep * 60 * 1000);
                        Thread.sleep((random.nextInt(6) * 5 + 70) * 1000);
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
