package com.fourpimes.nonblocking;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author fehmicansaglam
 */
public class Fatality extends Thread {

    private final BlockingQueue<Response> responses = new LinkedBlockingQueue<Response>();
    private final Selector selector;

    public Fatality() throws IOException {
        this.selector = Selector.open();
    }

    public void register(Response response) {
        System.out.println("[Fatality] Registering response.");
        responses.add(response);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            System.out.println("[Fatality] Waiting for bloody operations.");
            int keysAdded;
            while ((keysAdded = selector.select()) >= 0) {
                System.out.println("[Fatality] Waked up.");
                final List<Response> responseList = new LinkedList<Response>();
                responses.drainTo(responseList);

                System.out.println("[Fatality] Processing response list.");
                for (Response response : responseList) {
                    response.request.socketChannel.register(selector, SelectionKey.OP_WRITE);
                }

                System.out.println("[Fatality] " + keysAdded + " event(s) happened.");
                Set readyKeys = selector.selectedKeys();
                Iterator it = readyKeys.iterator();

                while (it.hasNext()) {
                    SelectionKey sk = (SelectionKey) it.next();
                    it.remove();

                    if (!sk.isValid()) {
                        continue;
                    }

                    if (sk.isWritable()) {
                        System.out.println("[Fatality] Selection is writeable.");
                        SocketChannel socketChannel = (SocketChannel) sk.channel();
                        socketChannel.socket().close();
                        System.out.println("[Fatality] Target destroyed.");
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
