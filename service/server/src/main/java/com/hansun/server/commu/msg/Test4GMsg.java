package com.hansun.server.commu.msg;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.commu.common.IMsg;

import java.nio.ByteBuffer;

/**
 * Created by yuanl2 on 2017/7/21.
 */
public class Test4GMsg extends  AbstractMsg implements IMsg {
    @Override
    public void setMsgType(String msgType) {

    }

    @Override
    public void setDeviceType(String deviceType) {

    }

    @Override
    public String getMsgType() {
        return null;
    }

    @Override
    public String getDeviceType() {
        return null;
    }

    @Override
    public byte[] getMsgBody() {
        return msgBody;
    }

    @Override
    public void setMsgBody(byte[] msgBody) {
        this.msgBody = msgBody;
    }

    @Override
    public void validate() throws InvalidMsgException {

    }

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    @Override
    public String getSeq() {
        return null;
    }

    @Override
    public void setSeq(String seq) {

    }
}
