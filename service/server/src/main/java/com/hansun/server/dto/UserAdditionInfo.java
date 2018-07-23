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

//    public static void main(String[] args){
//
//        UserAdditionInfo userAddtionInfo = new UserAdditionInfo();
//
//        userAddtionInfo.setUserName("test");
//        StoreInfo info  = new StoreInfo();
//        info.setAddress("上海徐汇区宜山路926号");
//        info.setName("xxxxxx");
//        info.setPromoDate(Instant.now());
//        info.setTel("34344134");
//        info.setUserName("user1");
//        List<StoreInfo> list = new ArrayList<>();
//        list.add(info);
//        info  = new StoreInfo();
//        info.setAddress("上海徐汇区宜山路900号");
//        info.setName("xxxxxx");
//        info.setPromoDate(Instant.now());
//        info.setTel("2222222");
//        info.setUserName("user1");
//        list.add(info);
//        userAddtionInfo.setStores(list);
//
//        JsonConvert<UserAdditionInfo> convert = new JsonConvert<>();
//        try {
//            System.out.println(convert.objectToJson(userAddtionInfo));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
}
