package com.hansun.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hansun.server.common.InstantSerialization;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class User implements UserDetails {
    private short id;
    private String name;
    private short userType;
    private String password;
    private UserAdditionInfo additionInfo;
    private String role;
    private boolean locked;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant expiredTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = InstantSerialization.ISOInstantSerializerFasterXML.class)
    @JsonDeserialize(using = InstantSerialization.ISOInstantDeserializerFasterXML.class)
    private Instant createTime;

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

    public short getUserType() {
        return userType;
    }

    public void setUserType(short userType) {
        this.userType = userType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return expiredTime.compareTo(Instant.now()) > 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserAdditionInfo getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(UserAdditionInfo additionInfo) {
        this.additionInfo = additionInfo;
    }

    public Instant getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Instant expiredTime) {
        this.expiredTime = expiredTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", userType=" + userType +
                ", name=" + name + "\n" +
                ", additionInfo=" + additionInfo +
                ", createTime=" + (createTime == null ? null : createTime.toString()) +
                ", expiredTime=" + (expiredTime == null ? null : expiredTime.toString()) +
                ", locked=" + locked +
                ", role=" + role +
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
