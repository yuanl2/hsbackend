package com.hansun.server.service;

import com.hansun.dto.Device;
import com.hansun.server.HSServiceProperties;
import com.hansun.server.common.Status;
import com.hansun.server.db.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    private void init() {
        timer = new Timer();
        Set<Integer> deviceIDs = dataStore.geAllDevices();
        if (!deviceIDs.isEmpty()) {
            deviceIDs.forEach(k -> deviceIDMapSlot.put(k, currentIndex));
            slotMapDeviceIDs.put(currentIndex, deviceIDs);

            timer.schedule(new DeviceStatusTask(), Long.valueOf(hsServiceProperties.getHeartBeatInternal()),
                    Long.valueOf(hsServiceProperties.getSweepBeatInternal()));
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                timer.cancel();

                clean();
            }
        });
    }

    private void clean() {

        deviceIDMapSlot.clear();
        slotMapDeviceIDs.clear();
    }

    private Timer timer;

    private Map<Integer, Integer> deviceIDMapSlot = new ConcurrentHashMap<>();

    private Map<Integer, Set<Integer>> slotMapDeviceIDs = new ConcurrentHashMap<>();

    private volatile int currentIndex = 0;

    private DeviceConnect connectListener = new DeviceConnect();

    public DeviceConnect getConnectListener() {
        return connectListener;
    }

    class DeviceStatusTask extends TimerTask {

        public void run() {
            //5秒扫一格，
            currentIndex = (currentIndex++) % 60;
            Set<Integer> sets = slotMapDeviceIDs.remove(currentIndex);

            //update sets device status disconnetced
            sets.forEach(k -> {
                Device d = dataStore.queryDeviceByDeviceID(k);
                d.setStatus(Status.DISCONNECTED);
                connectListener.disconnect(d);
            });
        }
    }

    class DeviceConnect implements DeviceListener<Device> {

        private AtomicInteger count = new AtomicInteger(0);

        private Map<Integer, Device> map = new HashMap<Integer, Device>();

        @Override
        public void connnect(Device d) {
            map.put(d.getId(), d);
            count.incrementAndGet();
            int id = d.getId();
            deviceIDMapSlot.replace(id, currentIndex);

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


