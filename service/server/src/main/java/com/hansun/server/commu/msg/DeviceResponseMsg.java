package com.hansun.server.commu.msg;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static com.hansun.server.common.MsgConstant.BODY_LENGTH_FIELD_SIZE;
import static com.hansun.server.common.MsgConstant.DEVICE_SEPARATOR_FIELD;

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
        headBuilder.append(getTitle()).append(getMsgType()).append(DEVICE_SEPARATOR_FIELD);

        StringBuilder builder = new StringBuilder();
        builder.append(getDeviceType()).append(DEVICE_SEPARATOR_FIELD).append(getTime(getTime())).append(DEVICE_SEPARATOR_FIELD);
        byte[] body = builder.toString().getBytes();//17 byte

        int bodySize = body.length + 5;
        headBuilder.append(MsgUtil.getMsgBodyLength(bodySize,BODY_LENGTH_FIELD_SIZE)).append(DEVICE_SEPARATOR_FIELD);
        byte[] head = headBuilder.toString().getBytes();
        sendBuffer.put(head);// 12 byte
        sendBuffer.put(body);

        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(DEVICE_SEPARATOR_FIELD);

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
