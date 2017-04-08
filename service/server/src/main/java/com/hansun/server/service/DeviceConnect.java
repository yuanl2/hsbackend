package com.hansun.server.service;

import com.hansun.dto.Device;
import com.hansun.server.common.Status;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuanl2 on 2017/3/28.
 */
public class DeviceConnect implements DeviceListener<Device> {

    private AtomicInteger count = new AtomicInteger(0);

    private Map<Integer, Device> map = new HashMap<Integer, Device>();


    @Autowired
    private DeviceService deviceService;

    @Autowired
    private HeartBeatService heartBeatService;

    @Override
    public void connnect(Device device) {
        map.put(device.getId(), device);
        count.incrementAndGet();
        //update device status as connected
        device.setStatus(Status.CONNECT);
        deviceService.updateDevice(device);
    }

    @Override
    public void disconnect(Device device) {

    }

    public int getCount(){
        return count.get();
    }
}
