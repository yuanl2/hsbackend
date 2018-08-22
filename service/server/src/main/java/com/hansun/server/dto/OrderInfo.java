package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Entity
public class OrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "orderID", nullable = false)
    private long orderID;

    @Column(name = "deviceID", nullable = false)
    private long deviceID;

    @Transient
    private String deviceName;

    @Column(name = "startTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @Column(name = "consumeType", nullable = false)
    private short consumeType;

    /**
     * unit seconds
     */
    @Column(name = "duration", nullable = false)
    private short duration;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "payAccount")
    private String payAccount;

    @Column(name = "accountType", nullable = false)
    private short accountType;

    @Column(name = "orderName", nullable = false)
    private String orderName;


    @Column(name = "createTime", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 1: start
     * 2: successful end
     * 3: device not response
     * 4: user not pay
     * 5: unknown
     */
    @Column(name = "orderStatus", nullable = false)
    private short orderStatus;

    @Column(name = "orderType", nullable = false)
    private short orderType;

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public short getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(short consumeType) {
        this.consumeType = consumeType;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public short getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(short orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public short getOrderType() {
        return orderType;
    }

    public void setOrderType(short orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "order{" +
                "id=" + id +
                ", orderID=" + orderID +
                ", deviceID=" + deviceID +
                ", deviceName=" + deviceName +
                ", consumeType=" + consumeType +
                ", startTime=" + (startTime == null ? null : startTime.toString()) +
                ", endTime=" + (endTime == null ? null : endTime.toString()) +
                ", duration=" + duration +
                ", price=" + price +
                ", createTime=" + (createTime == null ? null : createTime.toString()) +
                ", orderStatus=" + orderStatus +
                ", orderType=" + orderType +
                "}";
    }

    @Override
    public int hashCode() {
        return (int) (((this.id * 31 + this.deviceID) * 31
                + this.startTime.hashCode()) * 31 + this.payAccount.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof OrderInfo && this.getId() == ((OrderInfo) obj).getId()
                    && this.getDeviceID() == (((OrderInfo) obj).getDeviceID())
                    && this.getDeviceName() == (((OrderInfo) obj).getDeviceName())
                    && this.getStartTime().equals(((OrderInfo) obj).getStartTime())
                    && this.getPayAccount().equals(((OrderInfo) obj).getPayAccount());
        }
    }
}
