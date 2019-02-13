package com.hansun;

import java.util.ArrayList;
import java.util.List;

public class CreateDeviceData {


    public static void main(String[] args) {


        List<DeviceData> list = new ArrayList<>();
        long deviceID = 201812038100l;
        String simcard = "898604301118C1638";
        for (int i = 100;i<200;i++){
            DeviceData deviceData = new DeviceData();
            deviceData.setManagerStatus(12);
            deviceData.setPort(1);
            deviceData.setQrcode("test");
            deviceData.setBeginTime("2018-12-25 08:15:38");
            deviceData.setConsumeType(2);
            deviceData.setLocationID(8);
            deviceData.setUserID(4);
            deviceData.setDeviceID(deviceID++);
            deviceData.setSimCard(simcard+i);
            deviceData.setType(100);
            deviceData.setName("Second-"+(i-99));
            list.add(deviceData);
        }


        new StorageService().saveResult(list,"test");


    }
}

class DeviceData {
    private int managerStatus;
    private int port;
    private String qrcode;
    private int locationID;
    private String beginTime;
    private int userID;
    private int type;
    private long deviceID;
    private int consumeType;
    private String simCard;
    private String name;

    public int getManagerStatus() {
        return managerStatus;
    }

    public void setManagerStatus(int managerStatus) {
        this.managerStatus = managerStatus;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    public String getSimCard() {
        return simCard;
    }

    public void setSimCard(String simCard) {
        this.simCard = simCard;
    }
}
