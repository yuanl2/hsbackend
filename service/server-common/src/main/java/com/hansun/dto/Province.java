package com.hansun.dto;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class Province {
    private int id;
    private String name;

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

    @Override
    public String toString() {
        return "province{" +
                "id=" + id +
                ", name=" + name +
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
            return obj instanceof Province && this.getId() == ((Province) obj).getId()
                    && this.getName().equals(((Province) obj).getName());
        }
    }
}
