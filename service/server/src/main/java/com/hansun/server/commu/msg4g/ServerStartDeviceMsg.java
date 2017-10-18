package com.hansun.server.commu.msg4g;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.commu.common.MsgOutputStream;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.hansun.server.common.MsgConstant4g.*;

/**
 * Created by yuanl2 on 2017/5/16.
 */
public class ServerStartDeviceMsg extends AbstractMsg {

    public ServerStartDeviceMsg(String msgType) {
        setMsgType(msgType);
    }

    /**
     * 每个端口启动时长
     */
    private Map<Integer, String> startMap = new HashMap<>();

    public Map<Integer, String> getStartMap() {
        return startMap;
    }

    public void setStartMap(Map<Integer, String> startMap) {
        this.startMap = startMap;
    }

    @Override
    public void validate() throws InvalidMsgException {

    }

    @Override
    public ByteBuffer toByteBuffer() {
        int portNum = 1;

        if(getDeviceType().equals("000")){
            portNum = 4;
        }
        else if (getDeviceType().equals("100")) {
            portNum = 1;
        }


        ByteBuffer sendBuffer = ByteBuffer.allocate(32 + 3 * portNum);
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append(getTitle()).append(DEVICE_SEPARATOR_FIELD).append(getMsgType()).append(DEVICE_SEPARATOR_FIELD);
        //5+1+2+1 = 9

        MsgOutputStream builder1 = new MsgOutputStream();
        builder1.writeString(MsgUtil.getMsgBodyLength(Integer.valueOf(getSeq()), DEVICE_SEQ_FIELD_SIZE)).writeString(DEVICE_SEPARATOR_FIELD)
                .writeString(getDup()).writeString(DEVICE_SEPARATOR_FIELD)
                .writeString(getDeviceType()).writeString(DEVICE_SEPARATOR_FIELD);
        //3+1+2+1+3+1 = 11

        map.forEach((k, v) -> builder1.writeString(v+""));
        builder1.writeString(DEVICE_SEPARATOR_FIELD);//5 bytes
        startMap.forEach((k, v) -> builder1.writeString(v));
        builder1.writeString(DEVICE_SEPARATOR_FIELD);//9 bytes

        byte[] body = builder1.toBytes();//25 byte
        int bodySize = body.length + 5;
        headBuilder.append(MsgUtil.getMsgBodyLength(bodySize, BODY_LENGTH_FIELD_SIZE)).append(DEVICE_SEPARATOR_FIELD);//4
        byte[] head = headBuilder.toString().getBytes();

        sendBuffer.put(head);// 14 byte
        sendBuffer.put(body);//30
        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(DEVICE_SEPARATOR_FIELD);

        sendBuffer.put(sb.toString().getBytes()); // 4 byte
        sendBuffer.put((byte) '#'); // 1 byte
        sendBuffer.rewind();// 这个很关键
        return sendBuffer;
    }
}
