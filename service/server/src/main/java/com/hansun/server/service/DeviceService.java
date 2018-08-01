package com.hansun.server.service;

import com.hansun.server.dto.Device;
import com.hansun.server.db.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class DeviceService {

    @Autowired
    private DataStore dataStore;

    /**
     * 从datastore获取的device填充了location相关信息
     *
     * @param device
     * @return
     */
    public Device createDevice(Device device) {
        Device device1 = dataStore.createDevice(device);
        return device1;
    }

    public Device updateDevice(Device device, byte status) {
        return dataStore.updateDevice(device, status);
    }

    public Device updateDevice(Device device) {
        return dataStore.updateDevice(device);
    }

    public Device updateDeviceManagerStatus(Long id, byte status) {
        return dataStore.updateManagerStatus(id,status);
    }

    public List<Device> updateDeviceListManagerStatus(List<Long> ids, byte status) {
        return dataStore.updateManagerStatus(ids,status);
    }


    public void deleteDevice(Long deviceID) {
        dataStore.deleteDevice(deviceID);
    }

    public void deleteDeviceByOwner(int owner) {
        dataStore.deleteDeviceByOwner(owner);
    }

    public void deleteDeviceByLocationID(int locationID) {
        dataStore.deleteDeviceByLocationID(locationID);
    }

    public List<Device> getDevicesByOwner(int useID) {
        return dataStore.queryDeviceByOwner(useID);
    }

    public List<Device> getAllDevices() {
        return dataStore.queryAllDevices();
    }

    public List<Device> getDevicesByLocationID(int locationID) {
        return dataStore.queryDeviceByLocation(locationID);
    }

    public Device getDevice(Long deviceID) {
        return dataStore.queryDeviceByDeviceID(deviceID);
    }

    public List<Device> getDevicesByDeviceBox(String simID) {
        return dataStore.queryDeviceByDeviceBox(simID);
    }

    public boolean containDeviceBox(String deviceBox){
        return dataStore.containDeviceBox(deviceBox);
    }
}
