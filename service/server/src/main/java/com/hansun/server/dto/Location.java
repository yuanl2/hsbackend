package com.hansun.server.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Entity
public class Location {
    @Id
    @GeneratedValue
    private short id;

    @Column(name = "provinceID",nullable = false)
    private short provinceID;
    private String province;

    @Column(name = "cityID",nullable = false)
    private short cityID;
    private String city;

    @Column(name = "areaID",nullable = false)
    private short areaID;
    private String areaName;
    private String address;

    @Column(name = "userID",nullable = false)
    private short userID;

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
                ", address=" + address + "\n" +
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
