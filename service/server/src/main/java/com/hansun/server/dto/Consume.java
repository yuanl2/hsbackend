package com.hansun.server.dto;

import javax.persistence.*;

/**
 * duration support seconds unit
 * Created by yuanl2 on 2017/4/6.
 */
@Entity
public class Consume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private short duration;

    @Column(nullable = false)
    private String description;

    @Column(name = "picpath", nullable = true)
    private String picpath;

    @Column(name = "deviceType", nullable = false)
    private String deviceType;

    /**
     * consume type
     */
    @Column(name = "type", nullable = false)
    private int type = 0;

    @Column(name = "value")
    private String value;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "consume{" +
                "id=" + id +
                ", price=" + price +
                ", duration=" + duration +
                ", description=" + description +
                ", deviceType=" + deviceType +
                ", picpath=" + picpath + "\n" +
                "}";
    }

    @Override
    public int hashCode() {
        return Float.hashCode(this.price) * 31 * deviceType.hashCode()
                + this.duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Consume && this.getDeviceType() == ((Consume) obj).getDeviceType() && this.getDuration() == ((Consume) obj).getDuration()
                    && this.getPrice() == ((Consume) obj).getPrice();
        }
    }
}
