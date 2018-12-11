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
public class RefundOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "deviceID", nullable = false)
    private long deviceID;

    @Column(name = "refundTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime refundTime;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "refundFee", nullable = false)
    private float refundFee;

    @Column(name = "payAccount")
    private String payAccount;

    @Column(name = "outTradeNo", nullable = false)
    private String outTradeNo;

    @Column(name = "outFefundNo", nullable = false)
    private String outFefundNo;

    @Column(name = "refundDesc", nullable = false)
    private String refundDesc;

    @Column(name = "refundStatus", nullable = false)
    private short refundStatus;

    @Column(name = "userID")
    private short userID;

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

    public LocalDateTime getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(float refundFee) {
        this.refundFee = refundFee;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOutFefundNo() {
        return outFefundNo;
    }

    public void setOutFefundNo(String outFefundNo) {
        this.outFefundNo = outFefundNo;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }

    public short getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(short refundStatus) {
        this.refundStatus = refundStatus;
    }

    public short getUserID() {
        return userID;
    }

    public void setUserID(short userID) {
        this.userID = userID;
    }
}
