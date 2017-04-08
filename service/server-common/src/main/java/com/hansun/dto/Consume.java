package com.hansun.dto;

/**
 * Created by yuanl2 on 2017/4/6.
 */
public class Consume {

    private int id;
    private int price;
    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "consume{" +
                "id=" + id +
                ", price=" + price +
                ", duration=" + duration + "\n" +
                "}";
    }

    @Override
    public int hashCode() {
        return (this.id * 31 + this.price) * 31
                + this.duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Consume && this.getId() == ((Consume) obj).getId()
                    && this.getDuration() == (((Consume) obj).getDuration())
                    && this.getPrice() == (((Consume) obj).getPrice());
        }
    }
}
