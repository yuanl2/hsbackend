package com.hansun.dto;

import java.time.Instant;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class User {
    private int id;
    private String name;
    private int userType;
    private String password;
    private String addtionInfo;
    private Instant expiredTime;

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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddtionInfo() {
        return addtionInfo;
    }

    public void setAddtionInfo(String addtionInfo) {
        this.addtionInfo = addtionInfo;
    }

    public Instant getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Instant expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", userType=" + userType +
                ", name=" + name + "\n" +
                ", addtionInfo=" + addtionInfo +
                ", expiredTime=" + expiredTime.toString() +
                "}";
    }

    @Override
    public int hashCode() {
        return (this.id * 31 + this.name.hashCode()) * 31
                + this.password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof User && this.getId() == ((User) obj).getId()
                    && this.getName().equals(((User) obj).getName())
                    && this.getPassword().equals(((User) obj).getPassword());
        }
    }
}
