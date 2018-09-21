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
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "provinceID", nullable = false)
    private short provinceID;

    @Transient
    private String province;

    @Column(name = "cityID", nullable = false)
    private short cityID;

    @Transient
    private String city;

    @Column(name = "areaID", nullable = false)
    private short areaID;

    @Transient
    private String areaName;

    @Transient
    private String address;

    @Column(name = "userID", nullable = false)
    private short userID;

    @Column(name = "enterTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime enterTime;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(short provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public short getCityID() {
        return cityID;
    }

    public void setCityID(short cityID) {
        this.cityID = cityID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public short getAreaID() {
        return areaID;
    }

    public void setAreaID(short areaID) {
        this.areaID = areaID;
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

    public LocalDateTime getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(LocalDateTime enterTime) {
        this.enterTime = enterTime;
    }

    @Override
    public String toString() {
        return "location{" +
                "id=" + id +
                ", provinceID=" + provinceID +
                ", province=" + province + "\n" +
                ", cityID=" + cityID +
                ", city=" + city +
                ", areaID=" + areaID +
                ", areaName=" + areaName +
                ", address=" + address +
                ", enterTime=" + enterTime + "\n" +
                ", userID=" + userID +
                "}";
    }

    @Override
    public int hashCode() {
        return (this.provinceID * 31
                + this.cityID) * 31 + this.areaID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Location && this.getProvinceID() == (((Location) obj).getProvinceID())
                    && this.getCityID() == (((Location) obj).getCityID())
                    && this.getAreaID() == (((Location) obj).getAreaID());
        }
    }
}
