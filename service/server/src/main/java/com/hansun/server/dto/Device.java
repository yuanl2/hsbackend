package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hansun.server.common.DeviceManagerStatus;
import com.hansun.server.common.DeviceStatus;

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

    @Column(name = "deviceID", nullable = false, unique = true)
    private long deviceID;

    @Column(name = "deviceType", nullable = false)
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

    @Column(name = "userID")
    private short userID;

    @Transient
    private String user;

    @Column(name = "status")
    private byte status = DeviceStatus.DISCONNECTED;

    @Column(name = "signalValue", nullable = false)
    private short signal = -1;

    @Column(name = "loginReason", nullable = false)
    private short loginReason = -1;

    @Column(name = "seq")
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

    @Column(name = "qrcode", nullable = false)
    private String qrcode;

    @Transient
    private String statusDesc;

    @Column(name = "managerStatus")
    private byte managerStatus;

    @Column(name = "consumeType")
    private byte consumeType;

    @Column(name = "version")
    private String version;

    @Column(name = "storeID", nullable = false)
    private short storeID;

    @Transient
    private String store;

    @Transient
    private String enterTime;

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


    public short getUserID() {
        return userID;
    }

    public void setUserID(short userID) {
        this.userID = userID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
        setStatusDesc(DeviceStatus.getStatusDesc(status));
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

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
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

    public byte getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(byte consumeType) {
        this.consumeType = consumeType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public short getStoreID() {
        return storeID;
    }

    public void setStoreID(short storeID) {
        this.storeID = storeID;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    @Override
    public String toString() {
        return "device{" +
                "id=" + id +
                "deviceID=" + deviceID +
                ", type=" + type +
                ", name=" + name +
                ", locationID=" + locationID +
                ", userID=" + userID +
                ", status=" + status +
                ", simCard=" + simCard +
                ", port=" + port +
                ", managerStatus=" + managerStatus +
                ", consumeType=" + consumeType +
                ", version=" + version +
                ", storeID=" + storeID +
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
