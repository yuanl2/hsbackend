package com.hansun.server.commu.common;

import com.hansun.server.common.InvalidMsgException;

import java.nio.ByteBuffer;
import java.util.Map;


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

    String getSeq();

    void setSeq(String seq);

    void setDup(String dup);

    String getDup();

    Map<Integer, Byte> getMap();
}
