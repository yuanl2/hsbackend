package com.hansun.server.commu.msg;

import com.hansun.server.common.InvalidMsgException;

import java.io.InvalidObjectException;
import java.nio.ByteBuffer;

/**
 * Created by yuanl2 on 2017/5/9.
 */
public interface IMsg {

    void setMsgType(String msgType);

    void setDeviceType(String deviceType);

    String getMsgType();

    String getDeviceType();

    /**
     * 得到消息体
     *
     * @return
     */
    public byte[] getMsgBody();

    void setMsgBody(byte[] msgBody);

    void validate() throws InvalidMsgException;

    ByteBuffer toByteBuffer();
}
