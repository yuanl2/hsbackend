package com.hansun.server.commu;

import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.service.HeartBeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanl2 on 2017/5/8.
 */

@Service
public class SocketServerSelectorFor4G {
    private final static Logger logger = LoggerFactory.getLogger(SocketServerSelectorFor4G.class);

    //select方法等待信道准备好的最长时间
    private static final int TIMEOUT = 500;

    private String host;
    private String port;
    private Selector selector;
    private Schedule_Task task = new Schedule_Task();
    private ExecutorService executor = null;

    @Autowired
    private HSServiceProperties hsServiceProperties;

    @Autowired
    private HeartBeatService heartBeatService;

    @Autowired
    private LinkManger linkManger;

    @PostConstruct
    public void init() {
        try {
            logger.info("Server Host for 4G " + hsServiceProperties.getSocketAddress() + " port " + hsServiceProperties.getSocketPort4G());
            host = hsServiceProperties.getSocketAddress();
            port = hsServiceProperties.getSocketPort4G();
            linkManger.addDeviceListener(heartBeatService.getConnectListener());
            initSelector();
        } catch (IOException e) {
            logger.error("init SocketService4G error", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down SocketServerSelectorFor4G");
        executor.shutdown();
    }

    private void initSelector() throws IOException {
        try {
            //创建一个选择器
            selector = Selector.open();
            logger.info("open the selector");
            //实例化一个信道
            ServerSocketChannel listnChannel = ServerSocketChannel.open();
            //将该信道绑定到指定端口
            listnChannel.socket().bind(new InetSocketAddress(host, Integer.parseInt(port)));
            //配置信道为非阻塞模式
            listnChannel.configureBlocking(false);
            //将选择器注册到各个信道
            listnChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("register channel to selector " + host);
        } catch (IOException e) {
            logger.error("init Selector failed", e);
            throw e;
        }

        executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }


    private class Schedule_Task implements Runnable {
        public void run() {
            /* 进入消息循环中 */
            try {
                loop();
            } catch (IOException e) {
                logger.error("loop error", e);
            }
        }
    }

    /**
     * Reactor内部的消息循环方法
     */
    private void loop() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            //一直等待,直至有信道准备好了I/O操作
            if (selector.select(TIMEOUT) == 0) {
                //在等待信道准备的同时，也可以异步地执行其他任务，
                //这里只是简单地打印"."
//                System.out.print(".");
                continue;
            } else {
                //准备好的信道所关联的Key集合的iterator实例
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIter = selectionKeys.iterator();
                //循环取得集合中的每个键值
                while (keyIter.hasNext()) {
                    SelectionKey key = keyIter.next();
                    IHandler handler = null;

                    //这里需要手动从键集中移除当前的key
                    keyIter.remove();

                    try {
                        if (key.isAcceptable()) {
                            //创建一个实现了协议接口的对象
                            handler = new SocketHandler4G();
                            handler.setHasConnected(true);
                            handler.setLinkManger(linkManger);
                            //如果服务端信道感兴趣的I/O操作为accept
                            handler.handleAccept(key);
                        }
                        //如果该键值有效，并且其对应的客户端信道感兴趣的I/O操作为write
                        if (key.isValid() && key.isWritable()) {
                            handler = ((IHandler) key.attachment());
                            handler.handleWrite(key);
                        }
                        //如果客户端信道感兴趣的I/O操作为read
                        if (key.isReadable()) {
                            handler = ((IHandler) key.attachment());
                            handler.handleRead(key);
                        }

                        if (!key.isValid()) {
                            handler = ((IHandler) key.attachment());
                            handler.handleClose();
                        }
                    } catch (IOException e) {
                        if (handler != null) {
                            logger.error("process error " + handler.getDeviceName(), e);
                            handler.handleClose();
                        } else {
                            logger.error("process error ", e);
                        }
                    } catch (Exception e) {
                        if (handler != null) {
                            logger.error("process other error " + handler.getDeviceName(), e);
//                            handler.handleClose();
                        } else {
                            logger.error("process error ", e);
                        }
                    }
                }
            }
        }
    }
}
