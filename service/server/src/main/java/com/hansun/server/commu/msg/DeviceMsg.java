package com.hansun.server.commu.msg;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.InvalidMsgException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanl2 on 2017/5/9.
 */
public class DeviceMsg extends AbstractMsg {

    public DeviceMsg() {

    }

    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private Map<Integer, Integer> map = new HashMap<>();

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
        setDeviceType(msgInputStream.readString(3));
        msgInputStream.skipBytes(1);
        setDeviceName(msgInputStream.readString(27));
        msgInputStream.skipBytes(1);
        byte[] status = msgInputStream.readBytes(5);
        for (int i = 1; i <= status.length; i++) {
            if (status[i - 1] == 49) {//'1'
                getMap().put(i, DeviceStatus.CONNECT);
            } else if (status[i - 1] == 48) { //'0'
                getMap().put(i, DeviceStatus.DISCONNECTED);
            } else if (status[i - 1] == 88) { //'X'
                getMap().put(i, DeviceStatus.INVALID);
            }
        }
        String times = msgInputStream.readString(9);
        int xor = Integer.valueOf(msgInputStream.readString(3));
        if (xor != checkxor) {
            throw new InvalidMsgException("message check xor error!");
        }
        msgInputStream = null;
    }
}
