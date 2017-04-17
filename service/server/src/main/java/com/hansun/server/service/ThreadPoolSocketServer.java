package com.hansun.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSocketServer extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//
//    /**
//     * Parses the parameter (listen port) and accepts TCP connections on that
//     * port. Each client is serviced by an independent thread, managed by a CachedThreadPool ExecutorService.
//     * The thread will read a line from the client, convert it to uppercase, and then write the converted
//     * line back to the client until the client disconnects or the server exits.
//     *
//     * @param args <listen port>
//     */
//    public static void main(String[] args) {
//
//        // Make sure both arguments are present
//        if (args.length < 1) {
//            ThreadPoolSocketServer.printUsage();
//            System.exit(1);
//        }
//
//        // Try to parse the port number
//        int port = -1;
//        try {
//            port = Integer.parseInt(args[0]);
//        } catch (NumberFormatException nfe) {
//            System.err.println("Invalid listen port value: \"" + args[1]
//                    + "\".");
//            ThreadPoolSocketServer.printUsage();
//            System.exit(1);
//        }
//
//        // Make sure the port number is valid for TCP.
//        if (port <= 0 || port > 65536) {
//            System.err.println("Port value must be in (0, 65535].");
//            System.exit(1);
//        }
//
//
//        InetAddress addr = null;
//        try {
//            addr = InetAddress.getByName("10.140.80.164");
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        final ThreadPoolSocketServer server = new ThreadPoolSocketServer(addr, port);
//        // Starts the server's independent thread
//        server.start();
//
//        try {
//            // Wait for the server to shutdown
//            server.join();
//            System.out.println("Completed shutdown.");
//        } catch (InterruptedException e) {
//            // Exit with an error condition
//            System.err.println("Interrupted before accept thread completed.");
//            System.exit(1);
//        }
//
//    }

    /**
     * Prints a simple usage string to standard error that describes the
     * command-line arguments for this class.
     */
    private static void printUsage() {
        System.err.println("Echo server requires 1 argument: <Listen Port>");
    }

    /**
     * Pool of worker threads of unbounded size. A new thread will be created
     * for each concurrent connection, and old threads will be shut down if they
     * remain unused for about 1 minute.
     */
    private final ExecutorService workers = Executors.newCachedThreadPool();

    /**
     * Server socket on which to accept incoming client connections.
     */
    private ServerSocket listenSocket;

    /**
     * Flag to keep this server running.
     */
    private volatile boolean keepRunning = true;


    private List<DeviceListener> listeners = new ArrayList<>();

    public void addDeviceListener(DeviceListener listener) {
        listeners.add(listener);
    }

    /**
     * Creates a new threaded echo server on the specified TCP port.  Calls {@code System.exit(1)} if
     * it is unable to bind to the specified port.
     *
     * @param port the TCP port to accept incoming connections on.
     */
    public ThreadPoolSocketServer(final InetAddress host, final int port) {

        // Capture shutdown requests from the Virtual Machine.
        // This can occur when a user types Ctrl+C at the console
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ThreadPoolSocketServer.this.shutdown();
            }
        });

        try {
            this.listenSocket = new ServerSocket(port, 0, host);
            logger.info("listen on " + port + " address " + host.getHostAddress());
        } catch (IOException e) {
            logger.error("An exception occurred while creating the listen socket: ", e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This is executed when ThreadPoolSocketServer.start() is invoked by another thread.  Will listen for incoming connections
     * and hand them over to the ExecutorService (thread pool) for the actual handling of client I/O.
     */
    @Override
    public void run() {
        // Set a timeout on the accept so we can catch shutdown requests
        try {
            this.listenSocket.setSoTimeout(1000);
        } catch (SocketException e1) {
            logger.error("Unable to set acceptor timeout value.  The server may not shutdown gracefully.");
        }

        logger.info("Accepting incoming connections on port " + this.listenSocket.getLocalPort());

        // Accept an incoming connection, handle it, then close and repeat.
        while (this.keepRunning) {
            try {
                // Accept the next incoming connection
                final Socket clientSocket = this.listenSocket.accept();
                logger.info("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(clientSocket, listeners);
                this.workers.execute(handler);

            } catch (SocketTimeoutException te) {
                // Ignored, timeouts will happen every 1 second
            } catch (IOException ioe) {
                logger.error("Exception occurred while handling client request: "
                        + ioe.getMessage());
                // Yield to other threads if an exception occurs (prevent CPU
                // spin)
                Thread.yield();
            }
        }
        try {
            // Make sure to release the port, otherwise it may remain bound for several minutes
            this.listenSocket.close();
        } catch (IOException ioe) {
            // Ignored
        }
        logger.info("Stopped accepting incoming connections.");
    }

    /**
     * Shuts down this server.  Since the main server thread will time out every 1 second,
     * the shutdown process should complete in at most 1 second from the time this method is invoked.
     */
    public void shutdown() {
        logger.info("Shutting down the server.");
        this.keepRunning = false;
        this.workers.shutdownNow();
        try {
            this.listenSocket.close();
            this.join();
        } catch (InterruptedException e) {
            // Ignored, we're exiting anyway
        } catch (IOException e){

        }
    }
}
