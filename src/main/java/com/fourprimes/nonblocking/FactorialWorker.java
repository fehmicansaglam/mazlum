/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fourprimes.nonblocking;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 * @author fehmicansaglam
 */
public class FactorialWorker extends Worker {

    public FactorialWorker(SocketChannel socketChannel, ByteBuffer byteBuffer) {
        super(socketChannel, byteBuffer);
    }

    @Override
    public Response work(Request request) {
        final String sayi = request.params.get("sayi");
        return new Response(request, factorial(Integer.parseInt(sayi)).toString());
    }

    private BigInteger factorial(int n) {
        System.out.println("Calculating factorial => " + n);
        if (n < 2) {
            return BigInteger.valueOf(1);
        }
        BigInteger result = BigInteger.valueOf(2);
        for (int i = 3; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
