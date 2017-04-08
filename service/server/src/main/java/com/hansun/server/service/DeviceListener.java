package com.hansun.server.service;

/**
 * Created by yuanl2 on 2017/3/28.
 */
public interface DeviceListener<T> {

    void connnect(T t);

    void disconnect(T t);

    int getCount();
}
