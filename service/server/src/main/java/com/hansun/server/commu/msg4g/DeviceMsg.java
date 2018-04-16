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
 * Created by yuanl2 on 2017/5/9.
 */
public class DeviceMsg extends AbstractMsg {

    public DeviceMsg() {

    }

    private String login_reason;

    private String signal;

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

    private Map<Integer, MsgTime> portMap = new HashMap<>();

    public Map<Integer, MsgTime> getPortMap() {
        return portMap;
    }

    public void setPortMap(Map<Integer, MsgTime> portMap) {
        this.portMap = portMap;
    }

    public String getLogin_reason() {
        return login_reason;
    }

    public void setLogin_reason(String login_reason) {
        this.login_reason = login_reason;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    @Override
    public void validate() throws InvalidMsgException {
        int checkxor = getXOR();

        String body = new String(msgBody);
        String[] content = body.split(DEVICE_SEPARATOR_FIELD);

        setSeq(content[0]);
        setDup(content[1]);
        setDeviceType(content[2]);

        int portNum = 1;

        //device type
        String status = content[3];
        logger.info("msg status = " + status);
        if (getDeviceType().equals("000")) {
            portNum = 4;
            for (int i = 1; i <= status.length(); i++) {
                if (status.charAt(i - 1) == 49) {//'1'
                    getMap().put(i, DeviceStatus.SERVICE);
                } else if (status.charAt(i - 1) == 48) { //'0'
                    getMap().put(i, DeviceStatus.IDLE);
                } else if (status.charAt(i - 1) == 88) { //'X'
                    getMap().put(i, DeviceStatus.INVALID);
                }
            }
        } else if (getDeviceType().equals("100")) {

            /** status
             *  __ __ __
             *  first:  device power      1: on      0: off
             *  second: device run status 1: running 0: idle // reserve
             *  third:  device status     1: fine    0: error
             *  001  device idle
             *  011  device stop but in reset
             *  111  device running
             *  if the third status is '0', device error
             *
             */
            if (status.charAt(2) == 48) {
                getMap().put(1, DeviceStatus.FAULT);
            } else if (status.equals("001") || status.equals("011")) {
                getMap().put(1, DeviceStatus.IDLE);
            } else if (status.equals("101") || status.equals("111")) {
                getMap().put(1, DeviceStatus.SERVICE);
            } else {
                getMap().put(1, DeviceStatus.INVALID);
            }
        } else {
            throw new InvalidMsgException("not support device type = " + getDeviceType(), ErrorCode.DEVICE_TYPE_ERROR.getCode());
        }


        String times = content[4];
        for (int i = 0; i < portNum; i++) {
            int time = Integer.valueOf(times.substring(i * 4, i * 4 + 2));
            int runTime = Integer.valueOf(times.substring(i * 4 + 2, i * 4 + 4));
            portMap.put(i + 1, new MsgTime(time, runTime));
        }

        //add preseq
        String preSeq = content[5];
        for (int i = 0; i < portNum; i++) {
            preSeqMap.put(i + 1, preSeq.substring(i * 3, i * 3 + 3));
        }

        String simName = content[6];

        if (!isLetterDigit(simName)) {
            throw new InvalidMsgException("device sim name is invalid " + simName, ErrorCode.DEVICE_SIM_FORMAT_ERROR.getCode());
        }

        setDeviceName(simName);

        String login_reason = content[7];
        setLogin_reason(login_reason);
        String signal = content[8];
        setSignal(signal);

        int xor = Integer.valueOf(content[9]);
        if (xor != checkxor) {
            throw new InvalidMsgException("message check xor error! checkxor = " + checkxor + " xor = " + xor, ErrorCode.DEVICE_XOR_ERROR.getCode());
        }
        content = null;
        body = null;
    }
}
