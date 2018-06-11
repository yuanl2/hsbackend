package com.hansun.server.service;

import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.commu.IHandler;

import com.hansun.server.commu.LinkManger;
import com.hansun.server.db.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private Timer timer;

    private Map<String, Integer> deviceIDMapSlot = new ConcurrentHashMap<>();

    private Map<Integer, Set<String>> slotMapDeviceIDs = new ConcurrentHashMap<>();

    private volatile int currentIndex = 0;

    private DeviceConnect connectListener = new DeviceConnect();

    public DeviceConnect getConnectListener() {
        return connectListener;
    }

    @PostConstruct
    private void init() {
        // 循环圈的格子个数 index 0 ~ count-1
        count = Integer.valueOf(hsServiceProperties.getHeartBeatInternal()) / Integer.valueOf(hsServiceProperties.getSweepBeatInternal());
        logger.info("HeartBeatService count value = " + count);

        for (int i = 0; i < count; i++) {
            slotMapDeviceIDs.put(i, new HashSet<String>());
        }
        timer = new Timer();

        // deviceboxid_"id"
        Set<String> deviceBoxIDs = dataStore.getAllDeviceBoxes();

        if (!deviceBoxIDs.isEmpty()) {
            deviceBoxIDs.forEach(k -> {
                logger.debug("HeartBeatService deviceIDMapSlot put  " + k + "  into "+ currentIndex);
                deviceIDMapSlot.put(k, currentIndex);
                slotMapDeviceIDs.get(currentIndex).add(k);
            });
        }
        timer.schedule(new DeviceStatusTask(), Long.valueOf(Long.valueOf(hsServiceProperties.getSweepBeatInternal())),
                Long.valueOf(hsServiceProperties.getSweepBeatInternal()));
    }

    @PreDestroy
    private void destroy() {
        timer.cancel();
        deviceIDMapSlot.clear();
        slotMapDeviceIDs.clear();
    }

    class DeviceStatusTask extends TimerTask {
        public void run() {
            Set<String> sets;
            try {
                //5秒扫一格，
                currentIndex = (++currentIndex) % count;
                logger.debug("HeartBeatService timer scan currentIndex value = " + currentIndex);
                sets = slotMapDeviceIDs.get(currentIndex);

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
        public void connnect(String simid, Map map, String dup) {
            try {
                Integer index = deviceIDMapSlot.get(simid);
                if (index == null) {
                    logger.debug("DeviceConnect new device connnect" + simid + " currentIndex value = " + currentIndex);
                    slotMapDeviceIDs.get((currentIndex - 1 + count) % count).add(simid);
                    deviceIDMapSlot.put(simid,(currentIndex - 1 + count) % count);                }
                else
                {
                    logger.debug("DeviceConnect connnect" + simid + " currentIndex value = " + index);
                    int next = (currentIndex - 1 + count) % count;
                    //把设备对应的ID移动到下一格
                    slotMapDeviceIDs.get(index).remove(simid);
                    logger.debug("DeviceConnect connnect " + simid + " move to next " + next);
                    slotMapDeviceIDs.get(next).add(simid);
                    deviceIDMapSlot.put(simid,next);
//                    //id只是设备盒子的id，具体对应4个具体的设备
//                    dataStore.updateDeviceStatus(simid, map, dup);
                }
            } catch (Exception e) {
                logger.error("DeviceConnect: connect error " + simid, e);
            }
        }

        @Override
        public void disconnect(String simid) {
            try {
                logger.info("DeviceConnect disconnect " + simid);
                deviceIDMapSlot.remove(simid);
                //收不到心跳，主动断开链路
                IHandler handler = linkManger.get(simid);
                if (handler != null) {
                    handler.handleClose();
                }
            } catch (Exception e) {
                logger.error("disconnect error " + simid, e);
            }
            Map<Integer,Byte> map = new HashMap<>();
            map.put(1,DeviceStatus.DISCONNECTED);
            map.put(2,DeviceStatus.DISCONNECTED);
            map.put(3,DeviceStatus.DISCONNECTED);
            map.put(4,DeviceStatus.DISCONNECTED);
            dataStore.updateDeviceStatus(simid,map,"0");
        }
    }
}