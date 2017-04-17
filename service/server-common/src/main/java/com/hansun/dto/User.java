package com.hansun.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hansun.server.common.InstantSerialization;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
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
        return this.name.hashCode() * 31 + this.password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof User && this.getName().equals(((User) obj).getName())
                    && this.getPassword().equals(((User) obj).getPassword());
        }
    }
}
