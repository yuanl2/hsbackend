package com.hansun.server.dto;

import javax.persistence.*;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(nullable = false)
    private String name;

    @Column(name = "districtName",nullable = false)
    private String districtName;

    @Column(name = "provinceID",nullable = false)
    private short provinceID;

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

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public short getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(short provinceID) {
        this.provinceID = provinceID;
    }

    @Override
    public String toString() {
        return "city{" +
                "id=" + id +
                ", name=" + name +
                ", districtName=" + districtName + "\n" +
                "}";
    }

    @Override
    public int hashCode() {
        return this.districtName.hashCode() * 31 + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof City && this.getDistrictName().equals(((City) obj).getDistrictName())
                    && this.getName().equals(((City) obj).getName());
        }
    }
}
