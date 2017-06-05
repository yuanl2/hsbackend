package com.hansun.server.commu.msg;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.InvalidMsgException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.hansun.server.common.MsgConstant.*;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class HeartBeatMsg extends AbstractMsg {
    public HeartBeatMsg() {
    }
    private Map<Integer,Integer> map = new HashMap<>();

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Integer> map) {
        this.map = map;
    }
    @Override
    public void validate() throws InvalidMsgException {
        int checkxor = getXOR();
        setDeviceType(msgInputStream.readString(DEVICE_TYPE_FIELD_SIZE));
        msgInputStream.skipBytes(1);
        byte[] status = msgInputStream.readBytes(DEVICE_STATUS_FIELD_SIZE);
        msgInputStream.skipBytes(1);
        for (int i = 1; i <= status.length; i++) {
            if (status[i - 1] == 49) {//'1'
                getMap().put(i, DeviceStatus.CONNECT);
            } else if (status[i - 1] == 48) { //'0'
                getMap().put(i, DeviceStatus.DISCONNECTED);
            } else if (status[i - 1] == 88) { //'X'
                getMap().put(i, DeviceStatus.INVALID);
            }
        }
        String times = msgInputStream.readString(DEVICE_RUNNING_TIME_FIELD_SIZE);
        msgInputStream.skipBytes(1);
        int xor = Integer.valueOf(msgInputStream.readString(DEVICE_XOR_FIELD_SIZE));
        if(xor != checkxor){
            throw new InvalidMsgException("message check xor error!");
        }
        msgInputStream = null;
    }
}
