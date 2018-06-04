package com.hansun.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hansun.server.common.InstantSerialization;
import com.hansun.utils.CustomDateSerializer;

import java.time.Instant;
import java.util.Date;

public class StoreInfo {

    @JsonProperty
    private String userName;

    @JsonProperty
    private String name;

    @JsonProperty
    private String address;

    @JsonProperty
    private String tel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant promoDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Instant getPromoDate() {
        return promoDate;
    }

    public void setPromoDate(Instant promoDate) {
        this.promoDate = promoDate;
    }
}
