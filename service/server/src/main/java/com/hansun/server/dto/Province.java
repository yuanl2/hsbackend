package com.hansun.server.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Entity
public class Province {
    @Id
    @GeneratedValue
    private short id;

    @Column(nullable = false)
    private String name;

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

    @Override
    public String toString() {
        return "province{" +
                "id=" + id +
                ", name=" + name +
                "}";
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Province && this.getName().equals(((Province) obj).getName());
        }
    }
}
