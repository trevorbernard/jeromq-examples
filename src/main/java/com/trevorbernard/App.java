package com.trevorbernard;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

public class App {
  public static void main(String[] args) throws Exception {
    Context context = ZMQ.context(1);
    String endpoint = "tcp://localhost:7210";
    byte[] topic = "PING:".getBytes();

    Worker worker = new Worker(context, endpoint, topic);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      worker.close();
    }));

    worker.start();
    worker.join();
    context.close();
  }
}
