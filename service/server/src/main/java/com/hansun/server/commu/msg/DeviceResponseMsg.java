package com.hansun.server.commu.msg;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class DeviceResponseMsg extends AbstractMsg {

    public DeviceResponseMsg(String msgType) {
        setMsgType(msgType);
    }

    private Instant time;

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        ByteBuffer sendBuffer = ByteBuffer.allocate(34);
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append(getTitle()).append(getMsgType()).append(",");

        StringBuilder builder = new StringBuilder();
        builder.append(getDeviceType()).append(",").append(getTime(getTime())).append(",");
        byte[] body = builder.toString().getBytes();//17 byte

        int bodySize = body.length + 5;
        headBuilder.append(MsgUtil.getMsgBodyLength(bodySize,3)).append(",");
        byte[] head = headBuilder.toString().getBytes();
        sendBuffer.put(head);// 12 byte
        sendBuffer.put(body);

        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(",");

        sendBuffer.put(sb.toString().getBytes()); // 4 byte
        sendBuffer.put((byte) '#'); // 1 byte
        sendBuffer.rewind();// 这个很关键
        return sendBuffer;
    }

    public static String getTime(Instant time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(Date.from(time));
    }

    @Override
    public void validate() throws InvalidMsgException {

    }
}
