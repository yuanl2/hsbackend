package com.hansun.dto;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class Location {
    private int id;
    private int provinceID;
    private String province;
    private int cityID;
    private String city;
    private int areaID;
    private String areaName;
    private String address;
    private int userID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
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
