package com.hansun.server.commu.msg4g;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ErrorCode;
import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.commu.common.MsgTime;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.hansun.server.common.MsgConstant4g.*;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class DeviceStartFinishMsg extends AbstractMsg {

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    @Override
    public void validate() throws InvalidMsgException {
        int checkxor = getXOR();

        setSeq(msgInputStream.readString(DEVICE_SEQ_FIELD_SIZE));
        msgInputStream.skipBytes(1);
        setDup(msgInputStream.readString(DEVICE_DUP_FIELD_SIZE));
        msgInputStream.skipBytes(1);

        setDeviceType(msgInputStream.readString(DEVICE_TYPE_FIELD_SIZE));
        msgInputStream.skipBytes(1);

        byte[] status = msgInputStream.readBytes(DEVICE_STATUS_FIELD_SIZE);
        for (int i = 1; i <= status.length; i++) {
            if (status[i - 1] == 49) {//'1'
                getMap().put(i, DeviceStatus.SERVICE);
            } else if (status[i - 1] == 48) { //'0'
                getMap().put(i, DeviceStatus.IDLE);
            } else if (status[i - 1] == 88) { //'X'
                getMap().put(i, DeviceStatus.INVALID);
            }
        }
        msgInputStream.skipBytes(1);
        String times = msgInputStream.readString(DEVICE_RUNNING_TIME_FIELD_SIZE);
        for (int i = 0; i < 4; i++) {
            int time = Integer.valueOf(times.substring(i * 4, i * 4 + 2));
            int runTime = Integer.valueOf(times.substring(i * 4 + 2, i * 4 + 4));
            getPortMap().put(i+1,new MsgTime(time,runTime));
        }
        msgInputStream.skipBytes(1);

        //add preseq
        String preSeq = msgInputStream.readString(DEVICE_PRE_SEQ_FIELD_SIZE);
        for (int i = 0; i < 4; i++) {
            preSeqMap.put(i + 1, preSeq.substring(i * 3, i * 3 + 3));
        }
        msgInputStream.skipBytes(1);

        String simName = msgInputStream.readString(DEVICE_NAME_FIELD_SIZE);

        if (!isLetterDigit(simName)) {
            throw new InvalidMsgException("device sim name is invalid " + simName, ErrorCode.DEVICE_SIM_FORMAT_ERROR.getCode());
        }

        setDeviceName(simName);
        msgInputStream.skipBytes(1);

        int xor = Integer.valueOf(msgInputStream.readString(DEVICE_XOR_FIELD_SIZE));
        if (xor != checkxor) {
            throw new InvalidMsgException("message check xor error!", ErrorCode.DEVICE_XOR_ERROR.getCode());
        }
        msgInputStream = null;
    }

}
