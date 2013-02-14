package com.fourprimes.nonblocking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router extends Thread {

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().
            availableProcessors() + 1);
    private final CompletionService<Response> completionService = new ExecutorCompletionService<Response>(executor);
    private final Fatality fatality;

    public Router(final Fatality fatality) {
        this.fatality = fatality;
    }

    public void process(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        completionService.submit(new FactorialWorker(socketChannel, buffer));
    }

    @Override
    public void run() {
        System.out.println("[Router] Started...");
        final Charset charset = Charset.forName("UTF-8");
        while (true) {
            try {
                System.out.println("[Router] Waiting for any task to be complete...");
                Response response = completionService.take().get();
                System.out.println(String.format("[Router] Task is complete. Sending response."));
                final CharsetEncoder encoder = charset.newEncoder();
                response.request.socketChannel.write(encoder.encode(CharBuffer.wrap(response.body)));
                fatality.register(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
