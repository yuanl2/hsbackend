package com.hansun.server.dto;

import java.util.List;

/**
 * @author yuanl2
 */
public class UserInfo {

    private short userID;

    private String userName;

    private List<String> access;

    private String avator;

    public short getUserID() {
        return userID;
    }

    public void setUserID(short userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getAccess() {
        return access;
    }

    public void setAccess(List<String> access) {
        this.access = access;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }
}
