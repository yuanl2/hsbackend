package com.hansun.server.common;


import com.hansun.server.dto.Order;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by yuanl2 on 2017/4/27.
 */
public class OrderDetail extends Order {

    private String province;
    private String city;
    private String areaName;
    private String address;
    private String user;
    private String cTime;
    private String sTime;
    private String month;
    private int day;
    private String createDate;
    private String eTime;
    private long id;
    private long deviceID;
    private String deviceName;
    private short consumeType;
    private short duration;
    private float price;
    private String payAccount;
    private short accountType;
    private String orderName;
    private short orderStatus;

    public OrderDetail(Order order) {

        this.duration = order.getDuration();
        this.price = order.getPrice();
        this.orderName = order.getOrderName();
        this.orderStatus = order.getOrderStatus();
        this.payAccount = order.getPayAccount();
        this.deviceID = order.getDeviceID();
        this.consumeType = order.getConsumeType();
        this.accountType = order.getAccountType();

        try {
            Instant createTime = order.getCreateTime();
            Date date = Date.from(createTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            month = (calendar.get(Calendar.MONTH) + 1) + "月份";
            day = calendar.get(Calendar.DAY_OF_MONTH);
            createDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            cTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            createTime = order.getStartTime();
            date = Date.from(createTime);
            calendar.setTime(date);
            sTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            createTime = order.getEndTime();
            date = Date.from(createTime);
            calendar.setTime(date);
            eTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {

        }

    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getDeviceID() {
        return deviceID;
    }

    @Override
    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public short getConsumeType() {
        return consumeType;
    }

    @Override
    public void setConsumeType(short consumeType) {
        this.consumeType = consumeType;
    }

    @Override
    public short getDuration() {
        return duration;
    }

    @Override
    public void setDuration(short duration) {
        this.duration = duration;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String getPayAccount() {
        return payAccount;
    }

    @Override
    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    @Override
    public short getAccountType() {
        return accountType;
    }

    @Override
    public void setAccountType(short accountType) {
        this.accountType = accountType;
    }

    @Override
    public String getOrderName() {
        return orderName;
    }

    @Override
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    @Override
    public short getOrderStatus() {
        return orderStatus;
    }

    @Override
    public void setOrderStatus(short orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getProvince() {
        return province;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getcTime() {
        return cTime;
    }

    public String getsTime() {
        return sTime;
    }

    public String getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String geteTime() {
        return eTime;
    }

    public String getCreateDate() {
        return createDate;
    }
}
