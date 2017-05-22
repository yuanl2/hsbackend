package com.hansun.server.service;

import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.commu.LinkManger;
import com.hansun.server.commu.SocketServerSelector;
import com.hansun.server.db.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuanl2 on 2017/3/31.
 */
@Service
public class HeartBeatService {

    private final static Logger logger = LoggerFactory.getLogger(HeartBeatService.class);

    @Autowired
    private HSServiceProperties hsServiceProperties;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private LinkManger linkManger;

    private int count;

    @PostConstruct
    private void init() {
        // 循环圈的格子个数 index 0 ~ count-1
        count = Integer.valueOf(hsServiceProperties.getHeartBeatInternal()) / Integer.valueOf(hsServiceProperties.getSweepBeatInternal());

        for (int i = 0; i < count; i++) {
            slotMapDeviceIDs.put(i, new HashSet<String>());
        }
        timer = new Timer();

        // deviceboxid_"id"
        Set<String> deviceIDs = dataStore.geAllDevices();
        Set<String> deviceBoxIDs = new HashSet<>();

        deviceIDs.forEach(id -> {
            String tmp = id.split("_")[0];
            deviceBoxIDs.add(tmp);
        });

        if (!deviceBoxIDs.isEmpty()) {
            deviceBoxIDs.forEach(k -> {
                deviceIDMapSlot.put(k, currentIndex);
                slotMapDeviceIDs.get(currentIndex).add(k);
            });
        }
        timer.schedule(new DeviceStatusTask(), Long.valueOf(hsServiceProperties.getHeartBeatInternal()),
                Long.valueOf(hsServiceProperties.getSweepBeatInternal()));
    }

    @PreDestroy
    private void destroy() {
        timer.cancel();
        deviceIDMapSlot.clear();
        slotMapDeviceIDs.clear();
    }

    private Timer timer;

    private Map<String, Integer> deviceIDMapSlot = new ConcurrentHashMap<>();

    private Map<Integer, Set<String>> slotMapDeviceIDs = new ConcurrentHashMap<>();

    private volatile int currentIndex = 0;

    private DeviceConnect connectListener = new DeviceConnect();

    public DeviceConnect getConnectListener() {
        return connectListener;
    }

    class DeviceStatusTask extends TimerTask {
        public void run() {
            try {
                //5秒扫一格，
                currentIndex = (++currentIndex) % count;
                Set<String> sets = slotMapDeviceIDs.get(currentIndex);
                if (sets != null) {
                    //update sets device status disconnetced
                    sets.forEach(k -> {
                        connectListener.disconnect(k);
                    });
                }
                sets.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DeviceConnect implements DeviceListener<String> {

        @Override
        public void connnect(String id) {
            try {
                int index = currentIndex;
                int next = (index + 1) % count;
                deviceIDMapSlot.replace(id, next);
                //把设备对应的ID移动到下一格
                slotMapDeviceIDs.get(index).remove(id);
                slotMapDeviceIDs.get(next).add(id);
                //id只是设备盒子的id，具体对应4个具体的设备
                dataStore.updateDeviceStatus(DeviceStatus.CONNECT, id);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect(String id) {
            try {
                //收不到心跳，主动断开链路
                linkManger.get(id).handleClose();
            } catch (IOException e) {
                logger.error("disconnect error " + id ,e);
            }
            dataStore.updateDeviceStatus(DeviceStatus.DISCONNECTED, id);
        }

    }
}


