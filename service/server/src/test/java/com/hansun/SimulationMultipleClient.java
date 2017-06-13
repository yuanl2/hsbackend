package com.hansun;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanl2 on 2017/5/19.
 */
public class SimulationMultipleClient {

    public static void main(String[] args) {
        if ((args.length < 2) || (args.length > 3))
            throw new IllegalArgumentException("参数不正确");
        //第一个参数作为要连接的服务端的主机名或IP
        String server = args[0];
        int count = Integer.valueOf(args[1]);
        //如果有第三个参数，则作为端口号，如果没有，则端口号设为7
        int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
        //创建一个信道，并设为非阻塞模式

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<String> devices = new ArrayList<>();
        devices.add("SIM800_898602b6101740350829,");
//        devices.add("SIM800_89860266101740350825,");
        for (String s : devices
                ) {
            executorService.submit(new ClientTask(s, 1, server, servPort));
        }
    }
}
