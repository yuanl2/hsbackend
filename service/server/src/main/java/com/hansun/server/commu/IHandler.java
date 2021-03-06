package com.hansun.server.commu;

import com.hansun.server.commu.common.IMsg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by yuanl2 on 2017/5/8.
 */
public interface IHandler {
    //accept I/O形式
    void handleAccept(SelectionKey key) throws IOException;

    //read I/O形式
    void handleRead(SelectionKey key) throws IOException;

    //write I/O形式
    void handleWrite(SelectionKey key) throws IOException;

    void handleClose() throws IOException;

    String getDeviceName();

    void setDeviceName(String deviceName);

    void updateOps();

    LinkedList<ByteBuffer> getSendList();

    LinkManger getLinkManger();

    void setLinkManger(LinkManger linkManger);

    void sendMsg(IMsg msg, int port);

    SocketChannel getSocketChannel();

    void setHasConnected(boolean hasConnected);

    boolean isNeedResponse();

    void setNeedResponse(boolean needResponse);

    boolean isNeedSend();

    void setNeedSend(boolean needSend);

    Map<Integer, Byte> getPortStatus();

    int getSeq();

    int setSeq(int value);

    boolean isHasConnected();

    boolean isFistMsg();

    void setFistMsg(boolean fistMsg);

    void setLastDeviceMsgTime(Instant time);

    Instant getLastDeviceMsgTime();
}