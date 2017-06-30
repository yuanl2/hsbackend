package com.hansun.server.service;

import java.util.Map;

/**
 * Created by yuanl2 on 2017/3/28.
 */
public interface DeviceListener<T> {

    void connnect(T t, Map portMap, String dup);

    void disconnect(T t);

}
