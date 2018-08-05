package com.hansun.server.service;

import com.hansun.server.dto.Consume;
import com.hansun.server.dto.Device;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.common.*;
import com.hansun.server.db.DataStore;
import com.hansun.server.util.TenpayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
        executor = Executors.newFixedThreadPool(1);
        min = hsServiceProperties.getOrderIntervalMin();
        max = hsServiceProperties.getOrderIntervalMax();
        flag = hsServiceProperties.getOrderIntervalFlag();
        executor.submit(new Schedule_Task(dataStore));
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

        private DataStore dataStore;
        public Schedule_Task(DataStore dataStore) {
            this.dataStore = dataStore;
        }

        public void run() {
            boolean threadFlag;
            Random random = new Random();
            while (!Thread.currentThread().interrupted()) {
                try {
                    synchronized (object) {
                        if (!isFlag()) {
                            logger.info("start wait for false");
                            object.wait();
                            logger.info("continue run for true");
                        }
                    }


                    Set<String> deviceBoxs = dataStore.getAllDeviceBoxes();
                    final List<Long> deviceLists = new ArrayList<>();
                    if(deviceBoxs != null && deviceBoxs.size()> 0){

                        Set<Long> deviceList = dataStore.getAllDevices();
                        if (deviceList != null && deviceList.size() > 0) {
                            deviceBoxs.forEach(k->{
                                deviceList.forEach(d->{
                                    Device device = dataStore.queryDeviceByDeviceID(d);
                                    if(device.getSimCard().equalsIgnoreCase(k) && device.getType() == DeviceType.DEVICE_4G.getType()){
                                        deviceLists.add(d);
                                    }
                                });
                            });
                        }
                    }

                    long begin = System.currentTimeMillis();
                    for (Long deviceID:
                            deviceLists) {
                        int type = random.nextInt(3);
                        Device d = dataStore.queryDeviceByDeviceID(deviceID);
                        List<Consume> consumeList = dataStore.queryAllConsumeByDeviceType(String.valueOf(d.getType()),ConsumeType.TEST.getValue());
                        Consume consume = consumeList.get(type);

                        logger.debug("queryDeviceByDeviceID  = {} , status = {}, managerStatus = {}",d.getDeviceID(),d.getStatus(),d.getManagerStatus());

                        if(d.getManagerStatus() == DeviceManagerStatus.TEST.getStatus()){
                            if (d.getStatus() !=  DeviceStatus.IDLE) {
                                Thread.sleep(5000);
                            } else {
                                logger.info("queryDeviceByDeviceID = " + d.getDeviceID() + " status " + d.getStatus());
                                OrderInfo order = new OrderInfo();
                                //---------------生成订单号 开始------------------------
                                //当前时间 yyyyMMddHHmmss
                                String currTime = TenpayUtil.getCurrTime();
                                //四位随机数
                                String strRandom = TenpayUtil.buildRandom(5) + "";
                                //10位序列号,可以自行调整。
                                String strReq = currTime + strRandom;
                                //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
                                String out_trade_no = strReq;
                                order.setOrderID(Long.valueOf(out_trade_no));
                                order.setOrderName("ordername-" + orderService.getSequenceNumber());
                                order.setStartTime(Utils.getNowTime());
                                order.setCreateTime(Utils.getNowTime());
                                order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
                                order.setOrderStatus(OrderStatus.PAYDONE);
                                order.setDeviceID(Long.valueOf(deviceID));
                                order.setDeviceName(d.getName());
                                order.setConsumeType(Short.valueOf(consume.getId()));
                                OrderInfo result = orderService.createOrder(order);
                                orderService.createStartMsgToDevice(result);
                                logger.info("device_id = " + deviceID + " start order " + result);
                            }
                        }
//                        else{
//                            Thread.sleep((random.nextInt(5) + 5) * 60000);
//                        }

                    }
                    long end = System.currentTimeMillis();
                    //轮询所有设备后，需要sleep的时间

                    long duration = 600000 - (end - begin);

                    if (duration > 0) {
                        Thread.sleep(duration);
                    }

                } catch (Exception e) {
                    logger.error("run task error",e);
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
