package com.hansun;

import com.hansun.server.commu.msg.AbstractMsg;
import com.hansun.server.util.MsgUtil;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by yuanl2 on 2017/5/19.
 */
public class ClientTask implements Runnable {

    private String name;
    private int count;
    private String server;
    private int port;

    public ClientTask(String name, int count, String server, int port) {
        this.name = name;
        this.count = count;
        this.server = server;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {

        int i = 0;
        while (!Thread.currentThread().isInterrupted() && i < getCount()) {
            try {
                //创建一个信道，并设为非阻塞模式
                SocketChannel clntChan = SocketChannel.open();
                clntChan.configureBlocking(false);
                //向服务端发起连接
                if (!clntChan.connect(new InetSocketAddress(getServer(), getPort()))) {
                    //不断地轮询连接状态，直到完成连接
                    while (!clntChan.finishConnect()) {
                        //在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
                        //这里为了演示该方法的使用，只是一直打印"."
                        System.out.print(".");
                        Thread.sleep(500);
                    }
                }

                //为了与后面打印的"."区别开来，这里输出换行符
                System.out.print("\n");
                //分别实例化用来读写的缓冲区
                ByteBuffer writeBuf = createDeviceMsg(getName());
                ByteBuffer readBuf = ByteBuffer.allocate(256);
                //接收到的总的字节数
                int totalBytesRcvd = 0;
                //每一次调用read（）方法接收到的字节数
                int bytesRcvd;
                //循环执行，直到接收到的字节数与发送的字符串的字节数相等
                while (totalBytesRcvd < 30) {
                    //如果用来向通道中写数据的缓冲区中还有剩余的字节，则继续将数据写入信道
                    if (writeBuf.hasRemaining()) {
                        clntChan.write(writeBuf);
                    }
                    Thread.sleep(1000);
//        }
                    //如果read（）接收到-1，表明服务端关闭，抛出异常
                    if ((bytesRcvd = clntChan.read(readBuf)) == -1) {
                        throw new SocketException("Connection closed prematurely");
                    }

                    totalBytesRcvd = totalBytesRcvd + bytesRcvd;
                }

                writeBuf.clear();
                readBuf.clear();
                Thread.sleep(100);

                writeBuf = createDeviceStartFinishMsg();
                readBuf = ByteBuffer.allocate(256);
                //接收到的总的字节数
                totalBytesRcvd = 0;
                //每一次调用read（）方法接收到的字节数
                //循环执行，直到接收到的字节数与发送的字符串的字节数相等
//        while (totalBytesRcvd < 13) {
                //如果用来向通道中写数据的缓冲区中还有剩余的字节，则继续将数据写入信道
                if (writeBuf.hasRemaining()) {
                    clntChan.write(writeBuf);
                }
                Thread.sleep(1000);
//        }
                //如果read（）接收到-1，表明服务端关闭，抛出异常
                if ((bytesRcvd = clntChan.read(readBuf)) == -1) {
                    throw new SocketException("Connection closed prematurely");
                }

                totalBytesRcvd = totalBytesRcvd + bytesRcvd;
//        }

                writeBuf.clear();
                readBuf.clear();
                Thread.sleep(100);

                writeBuf = createHearBeatMsg();
                readBuf = ByteBuffer.allocate(256);
                //接收到的总的字节数
                totalBytesRcvd = 0;
                //每一次调用read（）方法接收到的字节数
                //循环执行，直到接收到的字节数与发送的字符串的字节数相等
                while (totalBytesRcvd < 13) {
                    //如果用来向通道中写数据的缓冲区中还有剩余的字节，则继续将数据写入信道
                    if (writeBuf.hasRemaining()) {
                        clntChan.write(writeBuf);
                    }
                    Thread.sleep(1000);
//        }
                    //如果read（）接收到-1，表明服务端关闭，抛出异常
                    if ((bytesRcvd = clntChan.read(readBuf)) == -1) {
                        throw new SocketException("Connection closed prematurely");
                    }

                    totalBytesRcvd = totalBytesRcvd + bytesRcvd;
                }
                writeBuf.clear();
                readBuf.clear();

                Thread.sleep(100);

                writeBuf = createDeviceTaskFinishMsg();
                readBuf = ByteBuffer.allocate(256);
                //接收到的总的字节数
                totalBytesRcvd = 0;
                //每一次调用read（）方法接收到的字节数
                //循环执行，直到接收到的字节数与发送的字符串的字节数相等
//        while (totalBytesRcvd < 13) {
                //如果用来向通道中写数据的缓冲区中还有剩余的字节，则继续将数据写入信道
                if (writeBuf.hasRemaining()) {
                    clntChan.write(writeBuf);
                }
                Thread.sleep(1000);
//        }
                //如果read（）接收到-1，表明服务端关闭，抛出异常
                if ((bytesRcvd = clntChan.read(readBuf)) == -1) {
                    throw new SocketException("Connection closed prematurely");
                }

                totalBytesRcvd = totalBytesRcvd + bytesRcvd;
//        }
                writeBuf.clear();
                readBuf.clear();


                //关闭信道
                clntChan.close();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    private static ByteBuffer createDeviceMsg(String name) {
        StringBuilder headbuilder = new StringBuilder();
        headbuilder.append("TRV");
        headbuilder.append("AP00,");
        StringBuilder builder = new StringBuilder();
        builder.append("000,");
        builder.append(name);
        builder.append("101X,");
        builder.append("11223344,");
        byte[] body = builder.toString().getBytes();

        int bodySize = body.length + 5;
        headbuilder.append(MsgUtil.getMsgBodyLength(bodySize, 3)).append(",");

        byte[] head = headbuilder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);

        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(",");

        writeBuf.put(head);
        writeBuf.put(body);
        writeBuf.put(sb.toString().getBytes());
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }


    private static ByteBuffer createHearBeatMsg() {
        StringBuilder headbuilder = new StringBuilder();
        headbuilder.append("TRV");
        headbuilder.append("AP01,");
        StringBuilder builder = new StringBuilder();
        builder.append("000,");
        builder.append("101X,");
        builder.append("11223344,");
        byte[] body = builder.toString().getBytes();

        int bodySize = body.length + 5;
        headbuilder.append(MsgUtil.getMsgBodyLength(bodySize, 3)).append(",");
        byte[] head = headbuilder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);

        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(",");

        writeBuf.put(head);
        writeBuf.put(body);
        writeBuf.put(sb.toString().getBytes());
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }


    private static ByteBuffer createDeviceStartFinishMsg() {
        StringBuilder headbuilder = new StringBuilder();
        headbuilder.append("TRV");
        headbuilder.append("AP03,");
        StringBuilder builder = new StringBuilder();
        builder.append("000,");
        builder.append("101X,");
        builder.append("11223344,");
        byte[] body = builder.toString().getBytes();


        int bodySize = body.length + 5;
        headbuilder.append(MsgUtil.getMsgBodyLength(bodySize, 3)).append(",");
        byte[] head = headbuilder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);

        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(",");

        writeBuf.put(head);
        writeBuf.put(body);
        writeBuf.put(sb.toString().getBytes());
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }


    private static ByteBuffer createDeviceTaskFinishMsg() {
        StringBuilder headbuilder = new StringBuilder();
        headbuilder.append("TRV");
        headbuilder.append("AP05,");
        StringBuilder builder = new StringBuilder();
        builder.append("000,");
        builder.append("101X,");
        builder.append("11223344,");
        byte[] body = builder.toString().getBytes();

        int bodySize = body.length + 5;
        headbuilder.append(MsgUtil.getMsgBodyLength(bodySize, 3)).append(",");
        byte[] head = headbuilder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);

        StringBuilder sb = new StringBuilder();
        sb.append(MsgUtil.getMsgBodyLength(AbstractMsg.getCheckData(head, body, 0, 0), 3)).append(",");

        writeBuf.put(head);
        writeBuf.put(body);
        writeBuf.put(sb.toString().getBytes());
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }
}
