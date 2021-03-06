package com.hansun.server.commu;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.commu.common.MsgInputStream;
import com.hansun.server.commu.msg4g.AbstractMsg;
import com.hansun.server.commu.common.IMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


import static com.hansun.server.common.MsgConstant4g.*;


/**
 * Created by yuanl2 on 2017/5/9.
 */
public class SocketHandler4G extends AbstractHandler implements IHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer, Byte> portStatus = new ConcurrentHashMap<>();

    private String deviceName;

    //缓冲区的长度
    private static final int BUFSIZE = 13;
    /**
     * 用来接收消息
     */
    private ByteBuffer headBuffer = null;

    private ByteBuffer bodyBuffer = null;

    private boolean needResponse;

    private Object object = new Object();

    /**
     * 用于收发消息
     */
    private SocketChannel socketChannel;
    /**
     * 保存了该SocketChannel对应的selectionKey
     * 之所以要保存下来，是由于在接收和发送的回调以后，随时需要更新这个key的ops属性的
     * 如果发现不需要发送了。
     */
    private SelectionKey selectionKey;


    private Instant lastDeviceMsgTime;

//    private boolean needResponse;
//
//    private Object object = new Object();
//
//
//    private boolean needSend;
//
//    private ReentrantLock lock = new ReentrantLock();
//
//    private Condition condition = lock.newCondition();

    private AtomicInteger seq = new AtomicInteger();

    public int setSeq(int value) {
        if (value > 254) {
            seq.set(0);
            return 0;
        } else {
            seq.set(value);
            return value;
        }
    }

    public int getSeq() {
        if (seq.get() > 254) {
            seq.set(0);
        }
        return seq.incrementAndGet();
    }

    public SocketHandler4G() {
        for (int i = 1; i <= 4; i++) {
            portStatus.put(i, DeviceStatus.DISCONNECTED);
        }
    }

//    public boolean isNeedSend() {
//        try {
//            lock.lock();
//            if (needSend) {
//                logger.info(deviceName + " isNeedSend wait for sending");
//                condition.await(5, TimeUnit.SECONDS);
//                logger.info(deviceName + " isNeedSend now can send");
//            }
//        } catch (Exception e) {
//            logger.error(deviceName + " setNeedSend " + needSend, e);
//        } finally {
//            lock.unlock();
//        }
//        return needSend;
//    }
//
//    public void setNeedSend(boolean needSend) {
//        try {
//            lock.lock();
//            this.needSend = needSend;
//            logger.info(deviceName + " setNeedSend = " + needSend);
//            if (!needSend) {
//                condition.signalAll();
//            }
//        } catch (Exception e) {
//            logger.error(deviceName + " setNeedSend " + needSend, e);
//        } finally {
//            lock.unlock();
//        }
//
//    }
//
//    public boolean isNeedResponse() {
//        synchronized (object) {
//            if (needResponse) {
//                try {
//                    logger.info(deviceName + " isNeedResponse wait for sending");
//                    object.wait();
//                    logger.info(deviceName + " isNeedResponse now can send");
//                } catch (InterruptedException e) {
//                    logger.error(deviceName + " object.wait() error", e);
//                }
//            }
//            return needResponse;
//        }
//    }
//
//    public void setNeedResponse(boolean needResponse) {
//        synchronized (object) {
//            this.needResponse = needResponse;
//            logger.info(deviceName + " setNeedResponse = " + needResponse);
//            if (!needResponse) {//如果设置为true，则说明回文发送了，等待的业务消息可以接着发送
//                object.notifyAll();
//            }
//        }
//    }

    /**
     * 设置socketChannel
     *
     * @param socketChannel
     */
    protected void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * 返回socketChannel
     *
     * @return
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * 用来保存等待着发送的消息队列
     */
    private LinkedList<ByteBuffer> sendList = new LinkedList<ByteBuffer>();
    /**
     * 表示是否连接成功
     */
    private volatile boolean hasConnected = false;

    private LinkManger linkManger;

    private boolean fistMsg;

    public LinkManger getLinkManger() {
        return linkManger;
    }

    public void setLinkManger(LinkManger linkManger) {
        this.linkManger = linkManger;
    }

    /**
     * 返回selectionKey
     *
     * @return
     */
    protected SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public LinkedList<ByteBuffer> getSendList() {
        return sendList;
    }

    public boolean isHasConnected() {
        return hasConnected;
    }

    public void setHasConnected(boolean hasConnected) {
        this.hasConnected = hasConnected;
    }

    public Map<Integer, Byte> getPortStatus() {
        return portStatus;
    }

    public boolean isFistMsg() {
        return fistMsg;
    }

    public void setFistMsg(boolean fistMsg) {
        this.fistMsg = fistMsg;
    }

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
        if (clntChan == null) {
            logger.error("handleAccept SocketChannel is null");
            return;
        }
        logger.info("accept " + clntChan.getRemoteAddress());
        clntChan.configureBlocking(false);
        //将选择器注册到连接到的客户端信道，并指定该信道key值的属性为OP_READ，同时为该信道指定关联的附件
        selectionKey = clntChan.register(key.selector(), SelectionKey.OP_READ, this);
        setSocketChannel(clntChan);
        headBuffer = ByteBuffer.allocate(BUFSIZE);
        hasConnected = true;
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        long bytesRead = getSocketChannel().read(headBuffer);
        if (bytesRead == -1) {
            logger.error("Client close on {}", ((SocketChannel) key.channel()).getRemoteAddress());
            handleClose();
            return;
        }
        headBuffer.rewind();
        byte[] head = new byte[BUFSIZE];
        headBuffer.get(head);
        String headContent = new String(head);
        logger.debug("handleRead head = {}", headContent);

        //ignore msg not with "TRVAP" start
        if(!headContent.startsWith("TRVAP")){
            headBuffer.rewind();
            headBuffer.clear();
            return;
        }

        MsgInputStream headMsgInputStream = new MsgInputStream(head);
        headMsgInputStream.readString(IDENTIFIER_FIELD_SIZE + CMD_FIELD_SIZE + 2);
        int len = -1;
        try {
            len = Integer.valueOf(headMsgInputStream.readString(BODY_LENGTH_FIELD_SIZE));
        } catch (Exception e) {
            logger.error("Msg head parse error! {}", headContent);
            boolean exit = false;
            while (exit) {
                bodyBuffer = ByteBuffer.allocate(1);
                bytesRead = getSocketChannel().read(bodyBuffer);
                if (bytesRead == -1) {
                    exit = true;
                } else {
                    // '#'
                    if (bodyBuffer.get() == 35) {
                        exit = true;
                    }
                }
            }
            headBuffer.rewind();
            headBuffer.clear();
            bodyBuffer.clear();
            return;
        }
        bodyBuffer = ByteBuffer.allocate(len);
        bytesRead = getSocketChannel().read(bodyBuffer);
        if (bytesRead == -1) {
            logger.error("Client close on {}", ((SocketChannel) key.channel()).getRemoteAddress());
            handleClose();
            return;
        }
        bodyBuffer.rewind();
        IMsg msg = AbstractMsg.fromByteBuffer(head, bodyBuffer);

        //设备添加了 x0d x0a 两个byte数据，需要过滤掉
//        getSocketChannel().read(ByteBuffer.allocate(2));
        headBuffer.rewind();
        headBuffer.clear();
        bodyBuffer.clear();
        if (msg != null) {
            logger.info("device = {} deviceBoxName = {} msg = {} ", getSocketChannel().getRemoteAddress(), getDeviceName(), msg.toString());
            linkManger.process(new DeviceTask4G(this, msg, Integer.parseInt(linkManger.getResponseDelay())));
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer sendItem = null;
        synchronized (sendList) {
            sendItem = sendList.getFirst();
        }
        if (sendItem == null) {
            /* 如果为null,则本次不发送消息 */
        } else {
            /* 如果有需要发送的消息,则发送它 */
            SocketChannel socketChannel = getSocketChannel();

            String content = new String(sendItem.array());
            int number = socketChannel.write(sendItem);
            if (sendItem.remaining() == 0) {
                /* 如果已经发送完了该消息,则将它从list中删除 */
                synchronized (sendList) {
                    sendList.removeFirst();
                }
            }
            logger.info("send msg context = {} on {}", content, getSocketChannel().getRemoteAddress());
            updateOps();
        }
    }

    @Override
    public void handleClose() {
        logger.info("handleClose " + getDeviceName());
        if (hasConnected) {
            hasConnected = false;
            try {
                if (getSocketChannel() != null) {
//                    setNeedResponse(false);
                    getSelectionKey().cancel();
                    getSocketChannel().close();
//                    lock.lock();
//
//                    int waitThreadNum = lock.getWaitQueueLength(condition);
//                    //如果有其他线程被wait了，需要唤醒他们结束等待退出
//                    if (waitThreadNum > 0) {
//                        logger.info("waitThreadNum = " + waitThreadNum + " need to wake up");
//                        condition.notifyAll();
//                    }
                }
            } catch (IOException e) {
                logger.error("handleClose IOException error " + getDeviceName(), e);
            } catch (Exception e) {
                logger.error("handleClose Exception error " + getDeviceName(), e);
            } finally {
                linkManger.remove(deviceName, getLastDeviceMsgTime());
                seq = null;
                linkManger = null;
                headBuffer = null;
                bodyBuffer = null;
            }
        } else {
            logger.error(" not connect device ");
        }
    }

    /**
     * 业务消息下行发送
     *
     * @param msg
     */
    public void sendMsg(IMsg msg, int port) {

        if (!hasConnected) {
            logger.error("link is not connected");
            return;
        }

//        //如果当前有设备的响应消息需要优先回复，则wait
        isNeedResponse();
//
//        try {
//            lock.lock();
//            while (needSend && !Thread.currentThread().isInterrupted()) {
//                logger.info(deviceName + " isNeedSend wait for sending");
//                //如果之前的业务消息还未收到设备回复，也需要等待
//                condition.await(10, TimeUnit.SECONDS);
//
//                if (!hasConnected) {
//                    logger.error("link is closed");
//                    return;
//                }
//            }
//            logger.info(deviceName + " isNeedSend now can send");
//            needSend = true;
            getSendList().add(msg.toByteBuffer());
            updateOps();

            //update portStatus
            portStatus.put(port, DeviceStatus.SERVICE);
//
//        } catch (Exception e) {
//            logger.error(deviceName + " setNeedSend " + needSend, e);
//        } finally {
//            lock.unlock();
//        }
    }

    public void updateOps() {
        try {
            if (!hasConnected) {
                return;
            }
            synchronized (sendList) {
                if (sendList.size() == 0) {
                    getSelectionKey().interestOps(SelectionKey.OP_READ);
                } else {
                    getSelectionKey().interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
            }
        } catch (Exception e) {
            logger.error("updateOps error ", e);
            //todo 更新失败，最好不要走到这里
        }
    }


    public boolean isNeedResponse() {
        synchronized (object) {
            if (needResponse) {
                try {
                    logger.debug(deviceName + " isNeedResponse wait for sending");
                    object.wait();
                    logger.debug(deviceName + " isNeedResponse now can send");
                } catch (InterruptedException e) {
                    logger.error(deviceName + " object.wait() error", e);
                }
            }
            return needResponse;
        }
    }

    public void setNeedResponse(boolean needResponse) {
        synchronized (object) {
            this.needResponse = needResponse;
            logger.debug(deviceName + " setNeedResponse = " + needResponse);
            if (!needResponse) {//如果设置为true，则说明回文发送了，等待的业务消息可以接着发送
                object.notifyAll();
            }
        }
    }

    public Instant getLastDeviceMsgTime() {
        return lastDeviceMsgTime;
    }

    @Override
    public void setLastDeviceMsgTime(Instant lastDeviceMsgTime) {
        this.lastDeviceMsgTime = lastDeviceMsgTime;
    }
}