package com.fourprimes.nonblocking;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws Exception {
        Fatality fatality = new Fatality();
        fatality.start();
        Router router = new Router(fatality);
        router.start();
        new Server(router).start();
    }
}
