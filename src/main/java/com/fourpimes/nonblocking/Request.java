package com.fourpimes.nonblocking;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public final class Request {

    public final SocketChannel socketChannel;
    public final String method;
    public final String uri;
    public final Map<String, String> params;

    private Request(SocketChannel socketChannel, String method, String uri, Map<String, String> params) {
        this.socketChannel = socketChannel;
        this.method = method;
        this.uri = uri;
        this.params = params;
    }

    public static Request parse(final SocketChannel socketChannel, final ByteBuffer buffer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer.array())));
        final String uri;
        final Map<String, String> params = new HashMap<String, String>();

        String initial = reader.readLine();
        if (initial == null || initial.length() == 0 || Character.isWhitespace(initial.charAt(0))) {
            throw new RuntimeException("Bad request");
        }

        String cmd[] = initial.split("\\s");
        System.out.println("initial=> " + initial);
        if (cmd.length != 3) {
            throw new RuntimeException("Bad request");
        }

        final String method = cmd[0];

        int idx = cmd[1].indexOf('?');
        if (idx < 0) {
            uri = cmd[1];
        } else {
            uri = URLDecoder.decode(cmd[1].substring(0, idx), "ISO-8859-1");
            final String[] prms = cmd[1].substring(idx + 1).split("&");

            for (String param : prms) {
                String[] temp = param.split("=");
                if (temp.length == 2) {
                    // we use ISO-8859-1 as temporary charset and then
                    // String.getBytes("ISO-8859-1") to get the data
                    params.put(URLDecoder.decode(temp[0], "ISO-8859-1"),
                               URLDecoder.decode(temp[1], "ISO-8859-1"));
                } else if (temp.length == 1 && param.indexOf('=') == param.length() - 1) {
                    // handle empty string separatedly
                    params.put(URLDecoder.decode(temp[0], "ISO-8859-1"), "");
                }
            }
        }

        return new Request(socketChannel, method, uri, params);
    }
}
