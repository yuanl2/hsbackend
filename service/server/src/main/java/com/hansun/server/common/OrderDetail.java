package com.hansun.server.common;


import com.hansun.server.dto.OrderInfo;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by yuanl2 on 2017/4/27.
 */
public class OrderDetail {

    private String orderID;
    private String province;
    private String city;
    private String areaName;
    private String address;
    private String user;
    private String cTime;
    private String sTime;
    private String month;
    private int day;
    private String eTime;
    private long deviceID;
    private String deviceName;
    private short consumeType;
    private short duration;
    private float price;
    private String payAccount;
    private short accountType;
    private String orderName;
    private short orderStatus;
    private String orderStatusDesc;
    private String sMonth;
    private short locationID;
    private String sDay;
    private LocalDateTime startTime;
    private int hour;

    public OrderDetail(OrderInfo order) {

        this.duration = order.getDuration();
        this.price = order.getPrice();
        this.orderName = order.getOrderName();
        this.orderStatus = order.getOrderStatus();
        this.payAccount = order.getPayAccount();
        this.deviceID = order.getDeviceID();
        this.consumeType = order.getConsumeType();
        this.accountType = order.getAccountType();
        this.orderID = String.valueOf(order.getOrderID());
        this.startTime = order.getStartTime();
        try {
            this.orderStatusDesc = OrderStatus.getOrderStatusDesc(orderStatus);

            Calendar calendar = Calendar.getInstance();
            if (order.getCreateTime() != null) {
                Instant createTime = Utils.convertToInstant(order.getCreateTime());
                Date date = Date.from(createTime);
                calendar.setTime(date);
                month = (calendar.get(Calendar.MONTH) + 1) + "月份";
                day = calendar.get(Calendar.DAY_OF_MONTH);
                cTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            }
            if (order.getStartTime() != null) {
                Instant createTime = Utils.convertToInstant(order.getStartTime());
                Date date = Date.from(createTime);
                calendar.setTime(date);
                sTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                sMonth = new SimpleDateFormat("yyyy-MM").format(date);
                sDay = new SimpleDateFormat("yyyy-MM-dd").format(date);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
            }

            if (order.getEndTime() != null) {
                Instant createTime = Utils.convertToInstant(order.getEndTime());
                Date date = Date.from(createTime);
                calendar.setTime(date);
                eTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            }
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

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public short getConsumeType() {
        return consumeType;
    }

    public short getDuration() {
        return duration;
    }

    public float getPrice() {
        return price;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public short getAccountType() {
        return accountType;
    }

    public void setAccountType(short accountType) {
        this.accountType = accountType;
    }

    public String getOrderName() {
        return orderName;
    }


    public short getOrderStatus() {
        return orderStatus;
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

    public String getsMonth() {
        return sMonth;
    }

    public short getLocationID() {
        return locationID;
    }

    public void setLocationID(short locationID) {
        this.locationID = locationID;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getsDay() {
        return sDay;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getOrderStatusDesc() {
        return orderStatusDesc;
    }

    public int getHour() {
        return hour;
    }
}
