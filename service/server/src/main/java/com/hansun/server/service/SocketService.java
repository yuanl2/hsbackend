package com.hansun.server.service;

import com.hansun.server.HSServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by yuanl2 on 2017/3/31.
 */
@Service
public class SocketService {

    @Autowired
    private HSServiceProperties hsServiceProperties;
    private ThreadPoolSocketServer server;


    @Autowired
    private HeartBeatService heartBeatService;

    @PostConstruct
    public void init() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(hsServiceProperties.getSocketAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        server = new ThreadPoolSocketServer(addr, Integer.parseInt(hsServiceProperties.getSocketPort()));
        server.addDeviceListener(heartBeatService.getConnectListener());
        server.start();
    }

}
