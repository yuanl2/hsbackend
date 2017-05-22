package com.hansun;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by yuanl2 on 2017/3/28.
 */
public class DeviceClient implements Runnable {

    String deviceID;
    /**
     * 0:good
     * 1:fault
     * 2:repairing
     * 3:debug
     */
    byte status = 0;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(byte consumeType) {
        this.consumeType = consumeType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 2: 2 minutes
     * <p>
     * 5: 5 minutes
     * 8: 8 minutes
     * 15:15 minutes
     */
    byte consumeType = 2;


    String server;
    int port;

    public DeviceClient(String deviceID, String server, int port, byte status) {
        this.deviceID = deviceID;
        this.server = server;
        this.port = port;
        this.status = status;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        System.out.println("Attemping to connect to host " +
                getServer() + " on port " + getPort());

        Socket clientSocket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            clientSocket = new Socket(getServer(), getPort());
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + getServer());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + getServer());
            System.exit(1);
        }


        int count = 10;

        try {
            while (count-- > 0) {
                byte[] b = deviceID.getBytes();
                out.write(b.length);
                out.write(deviceID.getBytes());
                out.write(status);
                out.flush();
//                while (in.available() <= 0) {
//                    Thread.sleep(20);
//                }
//                in.read();
//                byte[] b1 = new byte[b.length];
//                in.read(b1);
//                System.out.println("data from server DeviceID : " + new String(b1));
                Thread.sleep(5000);
            }
            out.close();
            in.close();
//            stdIn.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + getServer());
            System.exit(1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public DeviceClient(String deviceID) {
        this.deviceID = deviceID;
    }
}
