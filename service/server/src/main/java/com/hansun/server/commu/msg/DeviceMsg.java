package com.hansun.server.commu.msg;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ErrorCode;
import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.commu.common.MsgTime;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.hansun.server.common.MsgConstant.*;

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

    private Map<Integer, Byte> map = new HashMap<>();

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    public Map<Integer, Byte> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Byte> map) {
        this.map = map;
    }

    private Map<Integer, MsgTime> portMap = new HashMap<>();

    public Map<Integer, MsgTime> getPortMap() {
        return portMap;
    }

    public void setPortMap(Map<Integer, MsgTime> portMap) {
        this.portMap = portMap;
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
            int time = Integer.valueOf(times.substring(i * 4, i * 4 + 1));
            int runTime = Integer.valueOf(times.substring(i * 4 + 2, i * 4 + 3));
            portMap.put(i + 1, new MsgTime(time, runTime));
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
            throw new InvalidMsgException("message check xor error! checkxor = " + checkxor + " xor = " + xor, ErrorCode.DEVICE_XOR_ERROR.getCode());
        }
        msgInputStream = null;
    }
}
