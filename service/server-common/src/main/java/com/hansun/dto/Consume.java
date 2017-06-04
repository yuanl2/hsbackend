package com.hansun.dto;

/**
 * Created by yuanl2 on 2017/4/6.
 */
public class Consume {

    private int id;
    private float price;
    private int duration;
    private String description;
    private String picpath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    @Override
    public String toString() {
        return "consume{" +
                "id=" + id +
                ", price=" + price +
                ", duration=" + duration +
                ", description=" + description +
                ", picpath=" + picpath + "\n" +
                "}";
    }

    @Override
    public int hashCode() {
        return Float.hashCode(this.price) * 31
                + this.duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Consume && this.getDuration() == (((Consume) obj).getDuration())
                    && this.getPrice() == (((Consume) obj).getPrice());
        }
    }
}
