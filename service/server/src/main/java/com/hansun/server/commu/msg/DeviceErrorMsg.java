package com.hansun.server.commu.msg;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.InvalidMsgException;

import java.nio.ByteBuffer;

import static com.hansun.server.common.MsgConstant.DEVICE_TYPE_FIELD_SIZE;
import static com.hansun.server.common.MsgConstant.DEVICE_XOR_FIELD_SIZE;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class DeviceErrorMsg extends AbstractMsg {

    public DeviceErrorMsg() {

    }

    int checkxor = getXOR();

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    @Override
    public void validate() throws InvalidMsgException {
        setDeviceType(msgInputStream.readString(DEVICE_TYPE_FIELD_SIZE));
        msgInputStream.skipBytes(1);
        int xor = Integer.valueOf(msgInputStream.readString(DEVICE_XOR_FIELD_SIZE));
        if (xor != checkxor) {
            throw new InvalidMsgException("message check xor error!");
        }
        msgInputStream = null;
    }
}
