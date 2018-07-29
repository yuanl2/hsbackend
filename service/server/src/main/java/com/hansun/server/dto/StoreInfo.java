package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hansun.server.common.InstantSerialization;
import com.hansun.utils.CustomDateSerializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author minxu
 */
public class StoreInfo {

    @JsonProperty
    private String userName;

    @JsonProperty
    private String name;

    @JsonProperty
    private String address;

    @JsonProperty
    private String tel;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime promoDate;

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

    public LocalDateTime getPromoDate() {
        return promoDate;
    }

    public void setPromoDate(LocalDateTime promoDate) {
        this.promoDate = promoDate;
    }
}
