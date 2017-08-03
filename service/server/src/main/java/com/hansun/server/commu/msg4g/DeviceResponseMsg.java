package com.hansun.server.commu.msg4g;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static com.hansun.server.common.MsgConstant4g.*;

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
        ByteBuffer sendBuffer = ByteBuffer.allocate(42);
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append(getTitle()).append(DEVICE_SEPARATOR_FIELD).append(getMsgType()).append(DEVICE_SEPARATOR_FIELD);

        MsgOutputStream outBody = new MsgOutputStream();
        outBody.writeString(MsgUtil.getMsgBodyLength(Integer.valueOf(getSeq()), DEVICE_SEQ_FIELD_SIZE)).writeString(DEVICE_SEPARATOR_FIELD)
                .writeString(getDup()).writeString(DEVICE_SEPARATOR_FIELD)
                .writeString(getDeviceType()).writeString(DEVICE_SEPARATOR_FIELD)
                .writeString(getTime(getTime())).writeString(DEVICE_SEPARATOR_FIELD);
        byte[] body = outBody.toBytes();//17 byte

        int bodySize = body.length + 5;
        headBuilder.append(MsgUtil.getMsgBodyLength(bodySize, BODY_LENGTH_FIELD_SIZE)).append(DEVICE_SEPARATOR_FIELD);
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
        Date date = Date.from(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int value = hour * 3600 + minute * 60 + second;
        return MsgUtil.getMsgBodyLength(value, SERVER_TIME_SECOND_SIZE);
    }

    @Override
    public void validate() throws InvalidMsgException {

    }
}
