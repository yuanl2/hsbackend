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
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "deviceID",nullable = false, unique = true)
    private long deviceID;

    @Column(name = "deviceType",nullable = false)
    private short type;

    @Column(nullable = false)
    private byte port;

    @Column(name = "deviceName")
    private String name;

    @Column(name = "locationID")
    private short locationID;

    @Transient
    private String province;
    @Transient
    private String city;
    @Transient
    private String areaName;
    @Transient
    private String address;

    @Column(name = "additionInfo")
    private String additionInfo;

    @Column(name = "ownerID")
    private short ownerID;
    @Transient
    private String owner;

    @Column
    private byte status;

    @Column(name = "signalValue",nullable = false)
    private short signal = -1;

    @Column(name = "loginReason",nullable = false)
    private short loginReason = -1;

    @Column
    //add last seq number
    private short seq;

    @Column(name = "loginTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;

    @Column(name = "logoutTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime logoutTime;
    /**
     * 设备上报的sim卡名字，初始化连接带有sim卡信息
     */
    @Column(name = "simCard")
    private String simCard;

    @Column(name = "beginTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @Column(name = "QRCode", nullable = false)
    private String QRCode;

    @Column(name = "managerStatus")
    private byte managerStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getLocationID() {
        return locationID;
    }

    public void setLocationID(short locationID) {
        this.locationID = locationID;
    }

    public String getProvince() {
        return province;
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

    public String getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(String additionInfo) {
        this.additionInfo = additionInfo;
    }

    public short getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(short ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getSimCard() {
        return simCard;
    }

    public void setSimCard(String simCard) {
        this.simCard = simCard;
    }

    public byte getPort() {
        return port;
    }

    public void setPort(byte port) {
        this.port = port;
    }

    public short getSignal() {
        return signal;
    }

    public void setSignal(short signal) {
        this.signal = signal;
    }

    public short getLoginReason() {
        return loginReason;
    }

    public void setLoginReason(short loginReason) {
        this.loginReason = loginReason;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public byte getManagerStatus() {
        return managerStatus;
    }

    public void setManagerStatus(byte managerStatus) {
        this.managerStatus = managerStatus;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    @Override
    public String toString() {
        return "device{" +
                "id=" + id +
                "deviceID=" + deviceID +
                ", type=" + type +
                ", name=" + name +
                ", locationID=" + locationID +
                ", province=" + province +
                ", city=" + city +
                ", areaName=" + areaName +
                ", address=" + address +
                ", additionInfo=" + additionInfo +
                ", owner=" + owner +
                ", ownerID=" + ownerID +
                ", status=" + status +
                ", signal=" + signal +
                ", loginReason=" + loginReason +
                ", simCard=" + simCard +
                ", port=" + port +
                ", beginTime=" + beginTime +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                ", seq=" + seq +
                ", QRCode=" + QRCode +
                ", managerStatus=" + managerStatus +
                "}";
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 31 + (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Device && this.getId() == (((Device) obj).getId())
                    && this.getName().equals(((Device) obj).getName());
        }
    }
}
