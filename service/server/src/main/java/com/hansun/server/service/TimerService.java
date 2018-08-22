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

            int loop = 0;
            while (!Thread.currentThread().interrupted()) {


                loop++;


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
                    logger.info("deviceLists size {}", deviceLists.size());

                    int count = deviceLists.size();
                    long sleepTime = 50;
                    if(5000/count > sleepTime){
                        sleepTime = 5000/count;
                    }

                    long begin = System.currentTimeMillis();
                    Consume consume = null;
                    boolean task = false;
                    for (Long deviceID:
                            deviceLists) {

                        Device d = dataStore.queryDeviceByDeviceID(deviceID);
                        List<Consume> consumeList = dataStore.queryAllConsumeByDeviceType(String.valueOf(d.getType()),ConsumeType.TEST.getValue());
                        int type = random.nextInt(consumeList.size());
                        consume = consumeList.get(type);

                        logger.debug("queryDeviceByDeviceID  = {} , status = {}, managerStatus = {}",d.getDeviceID(),d.getStatus(),d.getManagerStatus());

                        if(d.getManagerStatus() == DeviceManagerStatus.TEST.getStatus()){
                            if (d.getStatus() !=  DeviceStatus.IDLE) {
                                Thread.sleep(sleepTime);
                            } else {
                                task = true;
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
                                order.setOrderType(OrderType.TEST.getType());
                                OrderInfo result = orderService.createOrder(order);
                                orderService.createStartMsgToDevice(result);
                                logger.info("device_id = " + deviceID + " start order " + result);
                            }
                        }
                    }
                    long end = System.currentTimeMillis();
                    //轮询所有设备后，需要sleep的时间


                    if (task) {
                        /**
                         * 每连续下发两次任务之后，再多休息15分钟
                         */
                        if (loop % 2 == 0) {
                            long duration = consume.getDuration() * 1000 + 900000 - (end - begin);
                            if (duration > 0) {
                                logger.info("sleep for {}", duration);
                                Thread.sleep(duration);
                            }
                        } else {
                            long duration = consume.getDuration() * 1000 - (end - begin);
                            if (duration > 0) {
                                logger.info("sleep for {}", duration);
                                Thread.sleep(duration);
                            }
                        }
                        task = false;
                    }
                    else {
                        Thread.sleep(10000);
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
