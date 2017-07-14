package com.hansun.server.common;

import com.hansun.dto.Order;

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
    private String day;
    private String eTime;
    private long id;
    private long deviceID;
    private String deviceName;
    private int consumeType;
    private int duration;
    private float price;
    private String payAccount;
    private int accountType;
    private String orderName;
    private int orderStatus;

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
            day = new SimpleDateFormat("yyyy-MM-dd").format(date);
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
    public int getConsumeType() {
        return consumeType;
    }

    @Override
    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
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
    public int getAccountType() {
        return accountType;
    }

    @Override
    public void setAccountType(int accountType) {
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
    public int getOrderStatus() {
        return orderStatus;
    }

    @Override
    public void setOrderStatus(int orderStatus) {
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

    public String getDay() {
        return day;
    }

    public String geteTime() {
        return eTime;
    }
}
