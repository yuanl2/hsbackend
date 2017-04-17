package com.hansun.dto;

import java.time.Instant;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class Order {
    private int id;
    private int deviceID;
    private Instant startTime;
    private Instant endTime;
    private int type;
    private int duration;
    private float price;
    private String payAccount;
    private int accountType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    @Override

    public String toString() {
        return "order{" +
                "id=" + id +
                ", deviceID=" + deviceID +
                ", consumeType=" + type + "\n" +
                ", startTime=" + startTime.toString() +
                ", endTime=" + endTime.toString() +
                ", duration=" + duration + "\n" +
                ", price=" + price +
                ", payAccount=" + payAccount +
                ", accountType=" + accountType +
                "}";
    }

    @Override
    public int hashCode() {
        return ((this.id * 31 + this.deviceID) * 31
                + this.startTime.hashCode()) * 31 + this.payAccount.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Order && this.getId() == ((Order) obj).getId()
                    && this.getDeviceID() == (((Order) obj).getDeviceID())
                    && this.getStartTime().equals(((Order) obj).getStartTime())
                    && this.getPayAccount().equals(((Order) obj).getPayAccount());
        }
    }
}
