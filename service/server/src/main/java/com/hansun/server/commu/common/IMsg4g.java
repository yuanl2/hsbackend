package com.hansun.server.commu.common;

import com.hansun.server.commu.common.IMsg;

/**
 * Created by yuanl2 on 2017/08/06.
 */
public interface IMsg4g extends IMsg {

    String getDeviceName();

    void setDeviceName(String deviceName);
}
