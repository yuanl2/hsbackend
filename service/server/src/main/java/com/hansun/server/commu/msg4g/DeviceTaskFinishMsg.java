package com.hansun.server.commu.msg4g;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ErrorCode;
import com.hansun.server.common.InvalidMsgException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hansun.server.common.MsgConstant.*;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class DeviceTaskFinishMsg extends AbstractMsg {
    private Map<Integer, Integer> map = new HashMap<>();

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Integer> map) {
        this.map = map;
    }

    private List<Integer> list = new ArrayList<Integer>();

    @Override
    public ByteBuffer toByteBuffer() {
        return null;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
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
            int time = Integer.valueOf(times.substring(i * 4, i * 4 + 2));
            int runTime = Integer.valueOf(times.substring(i * 4 + 2, i * 4 + 4));
            portMap.put(i+1,new MsgTime(time,runTime));
        }
        msgInputStream.skipBytes(1);
        int xor = Integer.valueOf(msgInputStream.readString(DEVICE_XOR_FIELD_SIZE));
        if (xor != checkxor) {
            throw new InvalidMsgException("message check xor error!", ErrorCode.DEVICE_XOR_ERROR.getCode());
        }
        msgInputStream = null;
    }
}
