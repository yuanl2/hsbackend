package com.hansun.server.commu.msg4g;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.util.MsgUtil;

import java.nio.ByteBuffer;

import static com.hansun.server.common.MsgConstant.BODY_LENGTH_FIELD_SIZE;
import static com.hansun.server.common.MsgConstant.DEVICE_SEPARATOR_FIELD;

/**
 * Created by yuanl2 on 2017/5/16.
 */
public class ServerErrorMsg extends AbstractMsg {

    public ServerErrorMsg(String msgType) {
        setMsgType(msgType);
    }

    @Override
    public void validate() throws InvalidMsgException {

    }

    @Override
    public ByteBuffer toByteBuffer() {
        ByteBuffer sendBuffer = ByteBuffer.allocate(22);
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append(getTitle()).append(DEVICE_SEPARATOR_FIELD).append(getMsgType()).append(DEVICE_SEPARATOR_FIELD);

        StringBuilder builder = new StringBuilder();
        builder.append(getDeviceType()).append(DEVICE_SEPARATOR_FIELD);
        byte[] body = builder.toString().getBytes();//4 byte

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
}
