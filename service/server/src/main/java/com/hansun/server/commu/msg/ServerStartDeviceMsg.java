package com.hansun.server.commu.msg;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.metrics.StatsdReporter;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.hansun.server.common.MsgConstant.BODY_LENGTH_FIELD_SIZE;
import static com.hansun.server.common.MsgConstant.DEVICE_SEPARATOR_FIELD;

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
    private Map<Integer, String> map = new HashMap<>();

    /**
     * 每个端口状态
     */
    private Map<Integer, String> status = new HashMap<>();

    public Map<Integer, String> getMap() {
        return map;
    }

    public void setMap(Map<Integer, String> map) {
        this.map = map;
    }

    public Map<Integer, String> getStatus() {
        return status;
    }

    public void setStatus(Map<Integer, String> status) {
        this.status = status;
    }

    @Override
    public void validate() throws InvalidMsgException {

    }

    @Override
    public ByteBuffer toByteBuffer() {
        ByteBuffer sendBuffer = ByteBuffer.allocate(31);
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append(getTitle()).append(getMsgType()).append(DEVICE_SEPARATOR_FIELD);
        byte[] head = headBuilder.toString().getBytes();

        MsgOutputStream builder1 = new MsgOutputStream();
        builder1.writeString(getDeviceType());
        builder1.writeString(DEVICE_SEPARATOR_FIELD);

        status.forEach((k, v) -> {
            builder1.writeString(v);
        });
        builder1.writeString(DEVICE_SEPARATOR_FIELD);//5 bytes
        map.forEach((k, v) -> {
            builder1.writeString(v);
        });
        builder1.writeString(DEVICE_SEPARATOR_FIELD);//9 bytes

        byte[] body = builder1.toBytes();//14 byte
        int bodySize = body.length + 5;
        headBuilder.append(MsgUtil.getMsgBodyLength(bodySize, BODY_LENGTH_FIELD_SIZE)).append(DEVICE_SEPARATOR_FIELD);
        sendBuffer.put(head);// 12 byte
        sendBuffer.put(body);
        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(DEVICE_SEPARATOR_FIELD);

        sendBuffer.put(sb.toString().getBytes()); // 4 byte
        sendBuffer.put((byte) '#'); // 1 byte
        sendBuffer.rewind();// 这个很关键
        return sendBuffer;
    }
}
