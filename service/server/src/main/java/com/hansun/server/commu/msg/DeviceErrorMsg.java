package com.hansun.server.commu.msg;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.InvalidMsgException;

import java.nio.ByteBuffer;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class DeviceErrorMsg extends AbstractMsg {

    public DeviceErrorMsg() {

    }

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    @Override
    public void validate() throws InvalidMsgException {
        int checkxor = getXOR();
        setDeviceType(msgInputStream.readString(3));
        msgInputStream.skipBytes(1);
        int xor = Integer.valueOf(msgInputStream.readString(3));
        if (xor != checkxor) {
            throw new InvalidMsgException("message check xor error!");
        }
        msgInputStream = null;
    }
}
