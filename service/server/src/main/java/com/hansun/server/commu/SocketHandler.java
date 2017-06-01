package com.hansun.server.commu;

import com.hansun.server.commu.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * Created by yuanl2 on 2017/5/9.
 */
public class SocketHandler implements IHandler {

    private final static Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    private String deviceName;

    //缓冲区的长度
    private static final int BUFSIZE = 12;
    /**
     * 用来接收消息
     */
    private ByteBuffer headBuffer = null;

    private ByteBuffer bodyBuffer = null;

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
    private boolean hasConnected = false;

    private LinkManger linkManger;

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

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
        if (clntChan == null) {
            return;
        }
        logger.info("acccept " + clntChan.getRemoteAddress());
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
            logger.info("Client close " + ((SocketChannel) key.channel()).getRemoteAddress());
            handleClose();
            return;
        }
        headBuffer.rewind();
        byte[] head = new byte[BUFSIZE];
        headBuffer.get(head);
        MsgInputStream headMsgInputStream = new MsgInputStream(head);
        headMsgInputStream.readString(8);
        int len = Integer.valueOf(headMsgInputStream.readString(3));
        bodyBuffer = ByteBuffer.allocate(len);
        bytesRead = getSocketChannel().read(bodyBuffer);
        if (bytesRead == -1) {
            logger.info("Client close " + ((SocketChannel) key.channel()).getRemoteAddress());
            handleClose();
            return;
        }
        bodyBuffer.rewind();
        IMsg msg = AbstractMsg.fromByteBuffer(head, bodyBuffer);

        //设备添加了 x0d x0a 两个byte数据，需要过滤掉
        getSocketChannel().read(ByteBuffer.allocate(2));
        headBuffer.clear();
        bodyBuffer.clear();
        if (msg != null) {
            logger.info("device " + getSocketChannel().getRemoteAddress() + " msg " + msg.toString());
            linkManger.process(new DeviceTask(this, msg, Integer.parseInt(linkManger.getResponseDelay())));
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
            socketChannel.write(sendItem);
            if (sendItem.remaining() == 0) {
                /* 如果已经发送完了该消息,则将它从list中删除 */
                synchronized (sendList) {
                    sendList.removeFirst();
                }
            }
            logger.info("send msg context = " + content + " on " + getSocketChannel().getRemoteAddress());
            updateOps();
        }
    }

    @Override
    public void handleClose() {
        if (hasConnected) {
            try {
                if (getSocketChannel() != null) {
                    getSelectionKey().cancel();

                    getSocketChannel().close();

                    linkManger.remove(deviceName);
                    linkManger = null;
                }
            } catch (IOException e) {
                logger.error("handleClose error "  + getDeviceName(),e);
            } catch (Exception e){
                logger.error("handleClose error "  + getDeviceName(),e);
            }
        }
        hasConnected = false;
    }

    public void sendMsg(IMsg msg) {
        getSendList().add(msg.toByteBuffer());
        updateOps();
    }

    public void updateOps() {
        try {
            if (!hasConnected) {
            /* 如果当前还没有连上,则不更改 */
                return;
            }
            synchronized (sendList) {
                if (sendList.size() == 0) {
                /* 如果发送队列长度为0,则关闭发送开关 */
//				ModuleLogger.getCommonLogger().getCommuLogger().debug("need to close write ops");
                    getSelectionKey().interestOps(SelectionKey.OP_READ);
                } else {
                /* 如果有数据需要发送,则打开 */
//				ModuleLogger.getCommonLogger().getCommuLogger().debug("need to open write ops");
                    getSelectionKey().interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
            }
        } catch (Exception e) {
             logger.error("updateOps error ", e);
            //todo 更新失败，最好不要走到这里
        }
    }

}
