package com.hansun.dto;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class City {
    private int id;
    private String name;
    private String districtName;
    private int provinceID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
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
