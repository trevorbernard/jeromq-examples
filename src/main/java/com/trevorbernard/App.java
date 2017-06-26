package com.trevorbernard;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class App {
  public static void main(String[] args) throws Exception {
    Context context = ZMQ.context(1);
    String endpoint = "tcp://localhost:12345";
    byte[] topic = "A:".getBytes();

    Worker worker = new Worker(context, endpoint, topic);
    new Thread(worker).start();

    Socket pub = context.socket(ZMQ.PUB);
    pub.bind("tcp://*:12345");
    Thread.sleep(100);
    
    System.out.println("Starting publisher");
    for (int i = 0; i < 10; i++) {
      if (i % 3 == 0) {
        pub.send("A:ERROR" + i);
      } else {
        pub.send("A:YAH!" + i);
      }
    }
    System.out.println("Terminating publisher");
    // Happening too fast, need this
    Thread.sleep(1000);
    pub.close();
    worker.close();
    context.close();
  }
}
