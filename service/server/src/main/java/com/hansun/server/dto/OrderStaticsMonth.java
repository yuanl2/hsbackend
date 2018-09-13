package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author yuanl2
 */
@Entity
@Table(name = "OrderStaticsMonth",
        indexes = {@Index(name = "device_time_index",  columnList="deviceID, time", unique = true),
                @Index(name = "user_index", columnList="userID")})
public class OrderStaticsMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "deviceID", nullable = false, unique = true)
    private long deviceID;

    @Column(name = "time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private LocalDateTime time;

    @Column(name = "orderTotal  ", nullable = false)
    private int orderTotal ;

    @Column(name = "incomeTotal ", nullable = false)
    private double incomeTotal;

    @Column(name = "locationID")
    private short locationID;

    @Transient
    private String areaName;

    @Transient
    private String address;

    @Column(name = "userID",nullable = false)
    private short userID;

    @Transient
    private String userName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(int orderTotal) {
        this.orderTotal = orderTotal;
    }

    public double getIncomeTotal() {
        return incomeTotal;
    }

    public void setIncomeTotal(double incomeTotal) {
        this.incomeTotal = incomeTotal;
    }

    public short getLocationID() {
        return locationID;
    }

    public void setLocationID(short locationID) {
        this.locationID = locationID;
    }

    public String getAreaName() {
        return areaName;
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

    public short getUserID() {
        return userID;
    }

    public void setUserID(short userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
