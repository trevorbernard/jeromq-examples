package com.trevorbernard;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class TaskDistribution {
  private static final Context ctx = ZMQ.context(1);

  static double calculatePI(int n) {
    double result = 0.0;
    if (n < 0) {
      return 0.0;
    }
    for (int i = 0; i <= n; i++) {
      result += Math.pow(-1, i) / ((2 * i) + 1);
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    Socket push = ctx.socket(ZMQ.PUSH);
    push.bind("tcp://*:7210");

    for (int i = 0; i < 5; i++) {
      Thread t = new Thread(() -> {
        try (Socket s = ctx.socket(ZMQ.PULL)) {
          s.connect("tcp://127.0.0.1:7210");
        }
      });
      t.setName("Thread" + i);
      t.start();
    }
  }
}
