package com.hansun.server.service;

import com.hansun.dto.Device;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * A simple runnable class that performs the basic work of this server.
 * It will read a line from the client, convert it to uppercase, and then
 * write it back to the client.
 *
 * @author Robert Moore
 */
public class ClientHandler implements Runnable {
    /**
     * The socket connected to the client.
     */
    private final Socket clientSock;

    private List<DeviceListener> listeners;

    /**
     * Creates a new ClientHandler thread for the socket provided.
     *
     * @param clientSocket the socket to the client.
     */
    public ClientHandler(final Socket clientSocket, final List<DeviceListener> listeners) {
        this.clientSock = clientSocket;
        this.listeners = listeners;
    }

    /**
     * The run method is invoked by the ExecutorService (thread pool).
     */
    @Override
    public void run() {
        DataInputStream userInput = null;
        DataOutputStream userOutput = null;


        Device d = new Device();
        try {
            userInput = new DataInputStream(
                    this.clientSock.getInputStream());
            userOutput = new DataOutputStream(this.clientSock.getOutputStream());

            while (!this.clientSock.isClosed()) {
                int length = userInput.read();
                byte[] b = new byte[length];
                userInput.read(b);
                String deviceID = new String(b);
                byte status = userInput.readByte();
                d.setId(deviceID);
                d.setStatus(status);
                System.out.println(" receive message from deviceID : " + deviceID);

//                userOutput.write(length);
//                userOutput.write(b);
//                userOutput.flush();

                listeners.forEach(k -> k.connnect(d));

            }
//            }
        } catch (IOException ioe) {

            // Close both streams, wrappers may not be closed by closing the
            // socket
            ioe.printStackTrace();

        }
        try {
            if (userInput != null) {
                userInput.close();
            }
            if (userOutput != null) {
                userOutput.close();
            }
            this.clientSock.close();
            System.err.println("Lost connection to " + this.clientSock.getRemoteSocketAddress());
        } catch (IOException ioe2) {
            // Ignored
        }

    }
}