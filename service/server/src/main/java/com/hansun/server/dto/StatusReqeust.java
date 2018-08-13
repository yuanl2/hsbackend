package com.hansun.server.dto;

import java.util.List;

/**
 * @author yuanl2
 */
public class StatusReqeust {
    public List<Long> getLists() {
        return lists;
    }

    public void setLists(List<Long> lists) {
        this.lists = lists;
    }

    private List<Long> lists;
    private byte status;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
}
