package com.hansun.server.dto;

import javax.persistence.*;

/**
 * Created by yuanl2 on 2017/4/27.
 */
@Entity
public class PayAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * 该账户累计支付金额
     */
    @Column(name = "balance")
    private float balance;

    /**
     * 该账号首单免费
     */
    @Column(name = "free")
    private short free;

    @Column(name = "type")
    private short type;

    @Column(name = "count")
    private short count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public short getFree() {
        return free;
    }

    public void setFree(short free) {
        this.free = free;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getCount() {
        return count;
    }

    public void setCount(short count) {
        this.count = count;
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
            return obj instanceof PayAccount && this.getName().equals(((PayAccount) obj).getName());

        }
    }
}
