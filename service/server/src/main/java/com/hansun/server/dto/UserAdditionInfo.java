package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hansun.utils.JsonConvert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserAdditionInfo {

    @JsonProperty
    private List<StoreInfo> stores = new ArrayList<StoreInfo>();

    @JsonProperty
    private String userName;

    public List<StoreInfo> getStores() {
        return stores;
    }

    public void setStores(List<StoreInfo> stores) {
        this.stores = stores;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
