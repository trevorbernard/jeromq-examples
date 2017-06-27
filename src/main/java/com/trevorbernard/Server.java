package com.trevorbernard;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class Server {
  public static void main(String[] args) throws Exception {

    ZContext ctx = new ZContext();
    Publisher publisher = new Publisher(ctx);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      publisher.shutdown();
    }));
    publisher.start();
    publisher.join();
  }
}


class Publisher extends Thread {
  private AtomicBoolean running = new AtomicBoolean(true);
  private final ZContext context;

  public Publisher(ZContext context) {
    this.context = context;
    this.setName("publisher");
  }

  @Override
  public void run() {
    try (Socket pub = context.createSocket(ZMQ.PUB)) {
      pub.bind("tcp://*:7210");
      while (running.get()) {
        Utils.sleep(1000);
        String msg = "PING:" + Instant.now();
        System.out.println(msg);
        pub.send(msg);
      }
    }
  }

  public void shutdown() {
    System.out.println("Shutting down publisher");
    running.set(false);
  }
}
