package com.hansun.server.commu.msg;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.metrics.StatsdReporter;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
        ByteBuffer sendBuffer = ByteBuffer.allocate(32);
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append(getTitle()).append(getMsgType()).append(",");
        byte[] head = headBuilder.toString().getBytes();

        final StringBuilder builder1 = new StringBuilder();
        builder1.append(getDeviceType()).append(",");
        status.forEach((k, v) -> {
            builder1.append(v);
        });
        builder1.append(",");
        map.forEach((k, v) -> {
            builder1.append(v);
        });
        builder1.append(",");

        byte[] body = builder1.toString().getBytes();//14 byte
        int bodySize = body.length +5;
        headBuilder.append(MsgUtil.getMsgBodyLength(bodySize,3)).append(",");
        sendBuffer.put(head);// 12 byte
        sendBuffer.put(body);
        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(",");

        sendBuffer.put(sb.toString().getBytes()); // 4 byte
        sendBuffer.put((byte) '#'); // 1 byte
        sendBuffer.rewind();// 这个很关键
        return sendBuffer;

    }
}
