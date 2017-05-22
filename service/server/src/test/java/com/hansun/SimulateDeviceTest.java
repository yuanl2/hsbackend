package com.hansun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuanl2 on 2017/3/28.
 */
public class SimulateDeviceTest {

    private static AtomicInteger generator = new AtomicInteger(10000);

    public static void main(String[] args) {

        String deviceID = args[0];
        byte status = Byte.parseByte(args[1]);

        ExecutorService executors = Executors.newFixedThreadPool(20);


        String server = "42.159.117.91";
        int port = 9090;
        executors.submit(new DeviceClient(deviceID,server,port,status));
//        executors.submit(new DeviceClient("201704090002",server,port));
//        executors.submit(new DeviceClient("201704090003",server,port));
//        executors.submit(new DeviceClient("201704090004",server,port));

//        for (int i = 0; i < 1000; i++) {
//            executors.submit(createDevice(server,port));
//        }
    }


    private static int generateDeviceID() {
        return generator.incrementAndGet();
    }

//    private static DeviceClient createDevice(String server, int port) {
////        return new DeviceClient(generateDeviceID(), server, port);
//    }
}
