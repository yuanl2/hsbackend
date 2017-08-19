package com.hansun.server.commu.msg4g;

import com.hansun.server.common.ErrorCode;
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

    }
}
