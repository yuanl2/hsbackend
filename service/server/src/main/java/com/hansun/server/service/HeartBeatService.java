package com.hansun.server.service;

import com.hansun.dto.Device;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.Status;
import com.hansun.server.db.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuanl2 on 2017/3/31.
 */
@Service
public class HeartBeatService {

    @Autowired
    private HSServiceProperties hsServiceProperties;

    @Autowired
    private DataStore dataStore;

    private int count;

    @PostConstruct
    private void init() {
        timer = new Timer();
        Set<String> deviceIDs = dataStore.geAllDevices();
        if (!deviceIDs.isEmpty()) {
            deviceIDs.forEach(k -> deviceIDMapSlot.put(k, currentIndex));
            slotMapDeviceIDs.put(currentIndex, deviceIDs);

            timer.schedule(new DeviceStatusTask(), Long.valueOf(hsServiceProperties.getHeartBeatInternal()),
                    Long.valueOf(hsServiceProperties.getSweepBeatInternal()));
        }
        count = Integer.valueOf(hsServiceProperties.getHeartBeatInternal()) / Integer.valueOf(hsServiceProperties.getSweepBeatInternal());
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
            //5秒扫一格，
            currentIndex = (++currentIndex) % count;
            Set<String> sets = slotMapDeviceIDs.remove(currentIndex);

            if (sets != null) {
                //update sets device status disconnetced
                sets.forEach(k -> {
                    Device d = dataStore.queryDeviceByDeviceID(k);
                    d.setStatus(Status.DISCONNECTED);
                    connectListener.disconnect(d);
                });
            }
        }
    }

    class DeviceConnect implements DeviceListener<Device> {

        private AtomicInteger count = new AtomicInteger(0);

        private Map<String, Device> map = new HashMap<String, Device>();

        @Override
        public void connnect(Device d) {
            map.put(d.getId(), d);
            count.incrementAndGet();
            String id = d.getId();
            deviceIDMapSlot.replace(id, currentIndex);
            //把设备对应的ID移动到下一格
            slotMapDeviceIDs.get(currentIndex).remove(d.getId());
            slotMapDeviceIDs.get(currentIndex+1).add(d.getId());

            d.setStatus(Status.CONNECT);
            dataStore.updateDeviceStatus(d);
        }

        @Override
        public void disconnect(Device device) {
            dataStore.updateDeviceStatus(device);
        }

        public int getCount() {
            return count.get();
        }
    }

}


