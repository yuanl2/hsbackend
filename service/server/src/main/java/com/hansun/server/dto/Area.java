package com.hansun.server.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Entity
public class Area {
    @Id
    @GeneratedValue
    private short id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "cityID",nullable = false)
    private short cityID;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public short getCityID() {
        return cityID;
    }

    public void setCityID(short cityID) {
        this.cityID = cityID;
    }

    @Override
    public String toString() {
        return "location{" +
                "id=" + id +
                ", name=" + name +
                ", address=" + address + "\n" +
                "}";
    }


    @Override
    public int hashCode() {
        return this.address.hashCode() * 31
                + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Area && this.getAddress().equals(((Area) obj).getAddress())
                    && this.getName().equals(((Area) obj).getName());
        }
    }
}
