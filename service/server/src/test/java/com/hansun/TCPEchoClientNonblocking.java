package com.hansun;

import com.hansun.server.commu.msg.AbstractMsg;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by yuanl2 on 2017/5/8.
 */
public class TCPEchoClientNonblocking {
    public static void main(String args[]) throws Exception {
        if ((args.length < 2) || (args.length > 3))
            throw new IllegalArgumentException("参数不正确");
        //第一个参数作为要连接的服务端的主机名或IP
        String server = args[0];
        //第二个参数为要发送到服务端的字符串
        byte[] argument = args[1].getBytes();
        //如果有第三个参数，则作为端口号，如果没有，则端口号设为7
        int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
        //创建一个信道，并设为非阻塞模式
        SocketChannel clntChan = SocketChannel.open();
        clntChan.configureBlocking(false);
        //向服务端发起连接
        if (!clntChan.connect(new InetSocketAddress(server, servPort))) {
            //不断地轮询连接状态，直到完成连接
            while (!clntChan.finishConnect()) {
                //在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
                //这里为了演示该方法的使用，只是一直打印"."
                System.out.print(".");
            }
        }

        //为了与后面打印的"."区别开来，这里输出换行符
        System.out.print("\n");
        //分别实例化用来读写的缓冲区
        ByteBuffer writeBuf = createDeviceMsg();
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
        Thread.sleep(2000);

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
        Thread.sleep(2000);

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

        Thread.sleep(2000);

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
    }


    private static ByteBuffer createDeviceMsg() {
        StringBuilder builder = new StringBuilder();
        builder.append("TRV");
        builder.append("AP00,");
        byte[] head = builder.toString().getBytes();
        builder = new StringBuilder();
        builder.append("000,");
        builder.append("SIM800_898602B8191650216485,");
        builder.append("101X,");
        builder.append("101X,");
        byte[] body = builder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);
        writeBuf.put(head);
        writeBuf.putShort((short) (body.length + 3));
        writeBuf.put(body);
        writeBuf.putShort((short) (AbstractMsg.getCheckData(head, body, 0, 0) ^ body.length + 3));
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }


    private static ByteBuffer createHearBeatMsg() {
        StringBuilder builder = new StringBuilder();
        builder.append("TRV");
        builder.append("AP01,");
        byte[] head = builder.toString().getBytes();
        builder = new StringBuilder();
        builder.append("000,");
        builder.append("101X,");
        builder.append("101X,");
        byte[] body = builder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);
        writeBuf.put(head);
        writeBuf.putShort((short) (body.length + 3));
        writeBuf.put(body);
        writeBuf.putShort((short) (AbstractMsg.getCheckData(head, body, 0, 0) ^ body.length + 3));
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }


    private static ByteBuffer createDeviceStartFinishMsg() {
        StringBuilder builder = new StringBuilder();
        builder.append("TRV");
        builder.append("AP03,");
        byte[] head = builder.toString().getBytes();
        builder = new StringBuilder();
        builder.append("000,");
        builder.append("101X,");
        builder.append("101X,");
        byte[] body = builder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);
        writeBuf.put(head);
        writeBuf.putShort((short) (body.length + 3));
        writeBuf.put(body);
        writeBuf.putShort((short) (AbstractMsg.getCheckData(head, body, 0, 0) ^ body.length + 3));
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }


    private static ByteBuffer createDeviceTaskFinishMsg() {
        StringBuilder builder = new StringBuilder();
        builder.append("TRV");
        builder.append("AP05,");
        byte[] head = builder.toString().getBytes();
        builder = new StringBuilder();
        builder.append("000,");
        builder.append("101X,");
        builder.append("101X,");
        byte[] body = builder.toString().getBytes();

        ByteBuffer writeBuf = ByteBuffer.allocate(head.length + body.length + 5);
        writeBuf.put(head);
        writeBuf.putShort((short) (body.length + 3));
        writeBuf.put(body);
        writeBuf.putShort((short) (AbstractMsg.getCheckData(head, body, 0, 0) ^ body.length + 3));
        writeBuf.put((byte) '#');
        writeBuf.rewind();
        return writeBuf;
    }
}