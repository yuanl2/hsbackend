package com.hansun.server.service;

import com.hansun.dto.Consume;
import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.db.DataStore;
import com.hansun.server.util.TenpayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.*;
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
                        if(device.getSimCard().equalsIgnoreCase(k) && device.getType() == 100){
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
                        int type = random.nextInt(3);
                        Device d = dataStore.queryDeviceByDeviceID(deviceID);
                        List<Consume> consumeList = dataStore.queryAllConsumeByDeviceType(String.valueOf(d.getType()));
                        Consume consume = consumeList.get(type);

                        logger.debug("queryDeviceByDeviceID = " + d.getId() + " status " + d.getStatus());

                        if (d.getStatus() !=  DeviceStatus.IDLE) {
                            Thread.sleep((random.nextInt(5) + 10) * 1000);
                        } else {
                            logger.info("queryDeviceByDeviceID = " + d.getId() + " status " + d.getStatus());
                            Order order = new Order();
                            //---------------生成订单号 开始------------------------
                            //当前时间 yyyyMMddHHmmss
                            String currTime = TenpayUtil.getCurrTime();
                            //四位随机数
                            String strRandom = TenpayUtil.buildRandom(5) + "";
                            //10位序列号,可以自行调整。
                            String strReq = currTime + strRandom;
                            //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
                            String out_trade_no = strReq;
                            order.setId(Long.valueOf(out_trade_no));
                            order.setOrderName("ordername-" + orderService.getSequenceNumber());
                            order.setStartTime(Instant.now());
                            order.setCreateTime(Instant.now());
                            order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
                            order.setOrderStatus(OrderStatus.PAYDONE);
                            order.setDeviceID(Long.valueOf(deviceID));
                            order.setDeviceName(boxName);
                            order.setConsumeType(Short.valueOf(consume.getId()));
                            Order result = orderService.createOrder(order);
                            orderService.createStartMsgToDevice(result);
                            logger.info("device_id = " + deviceID + " start order " + result);
                            Thread.sleep((random.nextInt(5) + 5) * 30000);
                        }
                    }
                } catch (Exception e) {
                    logger.error("device_id = " + device_id, e);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
