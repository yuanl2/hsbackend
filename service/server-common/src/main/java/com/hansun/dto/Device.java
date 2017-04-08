package com.hansun.dto;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class Device {
    private int id;
    private int type;
    private String name;
    private int locationID;
    private String province;
    private String city;
    private String areaName;
    private String address;
    private String addtionInfo;
    private int ownerID;
    private String owner;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
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

    public String getAddtionInfo() {
        return addtionInfo;
    }

    public void setAddtionInfo(String addtionInfo) {
        this.addtionInfo = addtionInfo;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "device{" +
                "id=" + id +
                ", type=" + type +
                ", name=" + name + "\n" +
                ", province=" + province +
                ", city=" + city +
                ", areaName=" + areaName +
                ", address=" + address + "\n" +
                ", addtionInfo=" + addtionInfo +
                ", owner=" + owner +
                "}";
    }

    @Override
    public int hashCode() {
        return this.id * 31 + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Device && this.getId() == ((Device) obj).getId()
                    && this.getName() == (((Device) obj).getName());
        }
    }
}
