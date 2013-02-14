mazlum
======

Very simple nonblocking http server implementation, calculating the factorial of any given number.

## DO NOT USE THIS SOFTWARE NOT ONLY IN PRODUCTION BUT ALSO FOR DEVELOPMENT
Being only a sample, it is just written badly(yeah, written by my ass), containing lots of `System.out.println`s, `e.printStackTrace`s and etc.

## Prerequisities

* JDK 1.6 or above
* Maven 

## How to run

* mvn package
* java -jar target/nonblocking-http-server-1.0-SNAPSHOT.jar
* curl "http://localhost:8080/index.html?sayi=11 // You get 11!


## Architecture

* 1 thread for accepting connections and reading from them: **Server**
* number of processors + 1 threads for execution(calculating factorial): **Router**
* 1 thread for closing connections: **Fatality**
