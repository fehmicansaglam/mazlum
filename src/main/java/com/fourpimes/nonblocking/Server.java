package com.fourpimes.nonblocking;

import java.nio.channels.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Server extends Thread {

    private static final int DEFAULT_PORT = 8080;
    private final int port;
    private final Router router;

    public Server(final Router router) throws Exception {
        this(DEFAULT_PORT, router);
    }

    public Server(int port, final Router router) throws Exception {
        this.port = port;
        this.router = router;
    }

    private void acceptConnections() throws Exception {
        final Selector selector = Selector.open();
        // Create a new server socket and set to non blocking mode
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        // Bind the server socket to the local host and port
        InetAddress lh = InetAddress.getByName("0.0.0.0");
        InetSocketAddress isa = new InetSocketAddress(lh, this.port);
        ssc.socket().bind(isa);

        int keysAdded;
        // Here's where everything happens. The select method will
        // return when any operations registered above have occurred, the
        // thread has been interrupted, etc.
        System.out.println("[Server] Waiting for requests on " + isa.toString());
        while ((keysAdded = selector.select()) > 0) {
            System.out.println("[Server] " + keysAdded + " event(s) happened.");
            // Someone is ready for I/O, get the ready keys
            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();

            // Walk through the ready keys collection and process date requests.
            while (it.hasNext()) {
                SelectionKey sk = (SelectionKey) it.next();
                it.remove();

                if (!sk.isValid()) {
                    continue;
                }

                if (sk.isAcceptable()) {
                    System.out.println("[Server] Selection is acceptable.");
                    // The key indexes into the selector so you
                    // can retrieve the socket that's ready for I/O
                    ServerSocketChannel nextReady =
                            (ServerSocketChannel) sk.channel();
                    // Accept the date request and send back the date string
                    SocketChannel socketChannel = nextReady.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    System.out.println("[Server] Selection is readable.");
                    SocketChannel socketChannel = (SocketChannel) sk.channel();
                    ByteBuffer buffer = (ByteBuffer) sk.attachment();
                    if (buffer == null) {
                        buffer = ByteBuffer.allocate(1024);
                        buffer.clear();
                        sk.attach(buffer);
                    }

                    if (socketChannel.read(buffer) < 0) {
                        System.out.println("[Server] Client connection refused");
                        socketChannel.close();
                    } else {
                        buffer.flip();
                        sk.cancel();
                        router.process(socketChannel, buffer);
                    }
                }
            }
            System.out.println("[Server] Waiting for the next selection.");
        }
    }

    @Override
    public void run() {
        try {
            acceptConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}