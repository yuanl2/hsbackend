package com.hansun.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hansun.server.common.InstantSerialization;

import java.time.Instant;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class Order {
    private long id;
    private long deviceID;
    private String deviceName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant startTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant endTime;
    private int consumeType;
    private int duration;
    private float price;
    private String payAccount;
    private int accountType;
    private String orderName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant createTime;
    /**
     * 1: start
     * 2: successful end
     * 3: device not response
     * 4: user not pay
     * 5: unknown
     */
    private int orderStatus;

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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
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

    @Override
    public String toString() {
        return "order{" +
                "id=" + id +
                ", deviceID=" + deviceID +
                ", deviceName=" + deviceName +
                ", consumeType=" + consumeType + "\n" +
                ", startTime=" + (startTime == null ? null : startTime.toString()) +
                ", endTime=" + (endTime == null ? null : endTime.toString()) +
                ", duration=" + duration + "\n" +
                ", price=" + price +
                ", payAccount=" + payAccount +
                ", accountType=" + accountType +
                ", createTime=" + (createTime == null ? null : createTime.toString()) +
                ", orderStatus=" + orderStatus +
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
            return obj instanceof Order && this.getId() == ((Order) obj).getId()
                    && this.getDeviceID() == (((Order) obj).getDeviceID())
                    && this.getDeviceName() == (((Order) obj).getDeviceName())
                    && this.getStartTime().equals(((Order) obj).getStartTime())
                    && this.getPayAccount().equals(((Order) obj).getPayAccount());
        }
    }
}
