package com.fourpimes.nonblocking;

import java.io.UnsupportedEncodingException;

public final class Response {

    public final Request request;
    public final String body;

    public Response(Request request, String body) {
        this.request = request;
        try {
            this.body = "HTTP/1.1 200 OK\n"
                    + "Content-Type: text/html; charset=utf-8\n"
                    + "Content-Length: " + body.getBytes("UTF-8").length
                    + "\n\n" + body;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
