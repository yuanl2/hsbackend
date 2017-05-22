package com.hansun.server.commu.msg;

import com.hansun.server.common.DeviceStatus;

import java.nio.ByteBuffer;

/**
 * Created by yuanl2 on 2017/5/9.
 */
public abstract class AbstractMsg implements IMsg {

    private String title = "TRV";
    private String MsgType;
    private String DeviceType;
    protected MsgInputStream msgInputStream;

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public void setMsgBody(byte[] msgBody) {
        this.msgBody = msgBody;
        msgInputStream = new MsgInputStream(msgBody);
    }

    public String getTitle() {
        return title;
    }

    /**
     * 消息体
     */
    protected byte[] msgBody = new byte[0];

    protected byte[] msgHead = new byte[0];

    @Override
    public String getMsgType() {
        return MsgType;
    }

    @Override
    public String getDeviceType() {
        return DeviceType;
    }

    @Override
    public byte[] getMsgBody() {
        return msgBody;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getMsgHead() {
        return msgHead;
    }

    public void setMsgHead(byte[] msgHead) {
        this.msgHead = msgHead;
    }

    /**
     * 具体消息重载
     *
     * @return
     */
    public abstract ByteBuffer toByteBuffer();

    public boolean isvalidMsg() {
        return true;
    }

    public static IMsg fromByteBuffer(byte[] head, ByteBuffer bodyBuffer) {
        MsgInputStream headMsgInputStream = new MsgInputStream(head);
        String title = headMsgInputStream.readString(3);
        String cmd = headMsgInputStream.readString(4);
        headMsgInputStream.skipBytes(1);
        int len = Integer.valueOf(headMsgInputStream.readString(3));
        byte[] body = new byte[len];
        bodyBuffer.get(body);
        switch (cmd) {
            case "AP00":
                DeviceMsg msg = new DeviceMsg();
                msg.setTitle(title);
                msg.setMsgType(cmd);
                msg.setMsgHead(head);
                msg.setMsgBody(body);
                return msg;
            case "AP01":
                HeartBeatMsg msg1 = new HeartBeatMsg();
                msg1.setTitle(title);
                msg1.setMsgType(cmd);
                msg1.setMsgHead(head);
                msg1.setMsgBody(body);
                return msg1;
            case "AP98":
                DeviceErrorMsg msg2 = new DeviceErrorMsg();
                msg2.setTitle(title);
                msg2.setMsgType(cmd);
                msg2.setMsgHead(head);
                msg2.setMsgBody(body);
                return msg2;
            case "AP03":
                DeviceStartFinishMsg msg3 = new DeviceStartFinishMsg();
                msg3.setTitle(title);
                msg3.setMsgType(cmd);
                msg3.setMsgHead(head);
                msg3.setMsgBody(body);
                return msg3;
            case "AP05":
                DeviceTaskFinishMsg msg4 = new DeviceTaskFinishMsg();
                msg4.setTitle(title);
                msg4.setMsgType(cmd);
                msg4.setMsgHead(head);
                msg4.setMsgBody(body);
                return msg4;
            default:
                return null;
        }
    }

    public static int getCheckData(byte[] data1, byte[] data2, int skipend1, int skipend2) {
        int result = 0;
        for (int i = 0; i < data1.length - skipend1; i++) {
            result = result ^ (data1[i]);
        }
        for (int j = 0; j < data2.length - skipend2; j++) {
            result = result ^ (data2[j]);
        }
        return result;
    }

    protected int getXOR() {
        return AbstractMsg.getCheckData(msgHead, msgBody, 0, 5);
    }


    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();

        strBuffer.append("Msg\n");
        strBuffer.append("msgHeader:msgTitle= " + getTitle());
        strBuffer.append(" msgType=" + getMsgType());
        strBuffer.append(" deviceType=" + getDeviceType());
        strBuffer.append("\n");
        strBuffer.append("msgHead:" + getMsgHeadStr());
        strBuffer.append("\n");
        strBuffer.append("msgBody:" + getMsgBodyStr());
        return strBuffer.toString();
    }


    /**
     * 获得16进制方式的消息体的字符串形式
     *
     * @return
     */
    public String getMsgHeadStr() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msgHead.length; i++) {
            char ch;
            ch = Character.forDigit((msgHead[i] >> 4) & 0xF, 16); //8位的高4位
            sb.append(ch);
            ch = Character.forDigit(msgHead[i] & 0xF, 16); //8位的低4位
            sb.append(ch);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * 获得16进制方式的消息体的字符串形式
     *
     * @return
     */
    public String getMsgBodyStr() {
        StringBuffer sb = new StringBuffer();


        for (int i = 0; i < msgBody.length; i++) {
            char ch;
            ch = Character.forDigit((msgBody[i] >> 4) & 0xF, 16); //8位的高4位
            sb.append(ch);
            ch = Character.forDigit(msgBody[i] & 0xF, 16); //8位的低4位
            sb.append(ch);
            sb.append(" ");
        }
        return sb.toString();
    }
}
