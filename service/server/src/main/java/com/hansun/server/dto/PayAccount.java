package com.hansun.server.dto;

/**
 * Created by yuanl2 on 2017/4/27.
 */
public class PayAccount {
    private int id;
    private String accountName;
    private float balance;
    private short free;
    private short type;
    private float discount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    @Override
    public int hashCode() {
        return this.accountName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof PayAccount && this.getAccountName().equals(((PayAccount) obj).getAccountName());

        }
    }
}
