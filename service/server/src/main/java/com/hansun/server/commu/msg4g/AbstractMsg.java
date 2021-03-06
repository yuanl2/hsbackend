package com.hansun.server.commu.msg4g;

import com.hansun.server.commu.common.IMsg;
import com.hansun.server.commu.common.IMsg4g;
import com.hansun.server.commu.common.MsgInputStream;
import com.hansun.server.commu.common.MsgTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.hansun.server.common.MsgConstant4g.*;

/**
 * Created by yuanl2 on 2017/5/9.
 */
public abstract class AbstractMsg implements IMsg4g {
    protected final static Logger logger = LoggerFactory.getLogger(AbstractMsg.class);

    protected String title = "TRVBP";
    protected String MsgType;
    protected String DeviceType;
    protected String seq;
    protected String dup = "00";
    protected String deviceName;
    protected Map<Integer, MsgTime> portMap = new HashMap<>();
    protected Map<Integer, Byte> map = new HashMap<>();
    protected Map<Integer,String> preSeqMap = new HashMap<>();

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public void setMsgBody(byte[] msgBody) {
        this.msgBody = msgBody;
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

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getDup() {
        return dup;
    }

    public void setDup(String dup) {
        this.dup = dup;
    }

    public Map<Integer, Byte> getMap() {
        return map;
    }

    public Map<Integer, MsgTime> getPortMap() {
        return portMap;
    }

    public void setMap(Map<Integer, Byte> map) {
        this.map = map;
    }

    public Map<Integer, String> getPreSeqMap() {
        return preSeqMap;
    }

    public void setPreSeqMap(Map<Integer, String> preSeqMap) {
        this.preSeqMap = preSeqMap;
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

        logger.debug("head content = " + new String(head));

        MsgInputStream headMsgInputStream = new MsgInputStream(head);
        String title = headMsgInputStream.readString(IDENTIFIER_FIELD_SIZE);
        headMsgInputStream.skipBytes(1);
        String cmd = headMsgInputStream.readString(CMD_FIELD_SIZE);
        headMsgInputStream.skipBytes(1);
        int len = Integer.valueOf(headMsgInputStream.readString(BODY_LENGTH_FIELD_SIZE));
        byte[] body = new byte[len];
        bodyBuffer.get(body);
        logger.debug("body content = " + new String(body));

        switch (cmd) {
            case DEVICE_REGISTER_MSG:
                DeviceMsg msg = new DeviceMsg();
                msg.setTitle(title);
                msg.setMsgType(cmd);
                msg.setMsgHead(head);
                msg.setMsgBody(body);
                return msg;
            case DEVICE_HEARTBEAT_MSG:
                HeartBeatMsg msg1 = new HeartBeatMsg();
                msg1.setTitle(title);
                msg1.setMsgType(cmd);
                msg1.setMsgHead(head);
                msg1.setMsgBody(body);
                return msg1;
            case DEVICE_ERROR_MSG:
                DeviceErrorMsg msg2 = new DeviceErrorMsg();
                msg2.setTitle(title);
                msg2.setMsgType(cmd);
                msg2.setMsgHead(head);
                msg2.setMsgBody(body);
                return msg2;
            case DEVICE_START_FINISH_MSG:
                DeviceStartFinishMsg msg3 = new DeviceStartFinishMsg();
                msg3.setTitle(title);
                msg3.setMsgType(cmd);
                msg3.setMsgHead(head);
                msg3.setMsgBody(body);
                return msg3;
            case DEVICE_TASK_FINISH_MSG:
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
        byte result = 0;
        for (int i = 0; i < data1.length - skipend1; i++) {
            result = (byte) (result ^ (data1[i]));
        }
        for (int j = 0; j < data2.length - skipend2; j++) {
            result = (byte) (result ^ (data2[j]));
        }
        return getUnsignedBytes(result);
    }

    public static int getUnsignedBytes(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data & 0x0FF;
    }

    protected int getXOR() {
        return AbstractMsg.getCheckData(msgHead, msgBody, 0, 5);
    }


    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();

        strBuffer.append("Msg = ");
        strBuffer.append("msgHeader:msgTitle= " + getTitle());
        strBuffer.append(" msgType=" + getMsgType());
        strBuffer.append(" deviceType=" + getDeviceType());
        strBuffer.append(" msgHead:" + new String(msgHead));
        strBuffer.append(" msgBody:" + new String(msgBody));
        return strBuffer.toString();
    }


    /**
     * 获得16进制方式的消息体的字符串形式
     *
     * @return
     */
    public static String getMsgHeadStr(byte[] msgHead) {
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
    public static String getMsgBodyStr(byte[] msgBody) {
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


    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    public void setPortMap(Map<Integer, MsgTime> portMap) {
        this.portMap = portMap;
    }
}
