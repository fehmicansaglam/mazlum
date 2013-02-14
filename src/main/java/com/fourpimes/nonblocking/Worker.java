package com.fourpimes.nonblocking;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

public abstract class Worker implements Callable<Response> {

    private final SocketChannel socketChannel;
    private final ByteBuffer buffer;

    public Worker(SocketChannel socketChannel, ByteBuffer buffer) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
    }

    public Response call() throws Exception {
        return work(Request.parse(this.socketChannel, this.buffer));
    }

    public abstract Response work(Request request);
}
