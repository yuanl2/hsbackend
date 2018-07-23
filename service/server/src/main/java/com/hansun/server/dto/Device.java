package com.hansun.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hansun.server.common.InstantSerialization;

import java.time.Instant;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class Device {
    private long id;
    private short type;
    private byte port;
    /**
     * 内部的设备名字
     */
    private String name;
    private short locationID;
    private String province;
    private String city;
    private String areaName;
    private String address;
    private String additionInfo;
    private short ownerID;
    private String owner;
    private byte status;
    private short signal = -1;
    private short loginReason = -1;

    //add last seq number
    private short seq;

    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant loginTime;

    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant logoutTime;
    /**
     * 设备上报的sim卡名字，初始化连接带有sim卡信息
     */
    private String simCard;

    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant beginTime;

    private String QRCode;

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

    public Instant getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Instant beginTime) {
        this.beginTime = beginTime;
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

    public Instant getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
    }

    public Instant getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Instant logoutTime) {
        this.logoutTime = logoutTime;
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

    @Override
    public String toString() {
        return "device{" +
                "id=" + id +
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
