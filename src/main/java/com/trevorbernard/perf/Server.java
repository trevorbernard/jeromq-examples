package com.trevorbernard.perf;

import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import zmq.ZMQ;

public class Server {
  public static void main(String[] argv) {
    String bindTo;
    long messageCount;
    int messageSize;
    ZContext ctx;
    Socket s;
    boolean rc;
    long i;
    ZMsg msg;
    long watch;
    long elapsed;
    long throughput;
    double megabits;

    if (argv.length != 3) {
      printf("usage: local_thr <bind-to> <message-size> <message-count>\n");
      return;
    }
    bindTo = argv[0];
    messageSize = atoi(argv[1]);
    messageCount = atol(argv[2]);

    ctx = new ZContext();
    if (ctx == null) {
      printf("error in init");
      return;
    }

    s = ctx.createSocket(ZMQ.ZMQ_PULL);
    if (s == null) {
      printf("error in socket");
    }

    // Add your socket options here.
    // For example ZMQ_RATE, ZMQ_RECOVERY_IVL and ZMQ_MCAST_LOOP for PGM.
    String serverPublic = ".s&p=^57A5>h-Xgyv%mJX58}C)]fLp9&t{xl[LR]";
    String serverSecret = "gJ4jDw>ZdY1eASGv2R{<u/ZMB8:$wYG4km:g{lp>";
    
    s.setAsServerCurve(true);
    s.setCurvePublicKey(serverPublic.getBytes());
    s.setCurveSecretKey(serverSecret.getBytes());

    rc = s.bind(bindTo);
    if (!rc) {
      printf("error in bind: %s\n");
      return;
    }

    msg = ZMsg.recvMsg(s);
    if (msg == null) {
      printf("error in recvmsg: %s\n");
      return;
    }

    watch = ZMQ.startStopwatch();

    for (i = 0; i != messageCount - 1; i++) {
      msg = ZMsg.recvMsg(s);
      if (msg == null) {
        printf("error in recvmsg: %s\n");
        return;
      }
      // if (ZMQ.msgSize(msg) != messageSize) {
      // printf("message of incorrect size received " + ZMQ.msgSize(msg));
      // return;
      // }
    }

    elapsed = ZMQ.stopStopwatch(watch);
    if (elapsed == 0) {
      elapsed = 1;
    }

    throughput = (long) ((double) messageCount / (double) elapsed * 1000000L);
    megabits = (double) (throughput * messageSize * 8) / 1000000;

    printf("message elapsed: %.3f \n", (double) elapsed / 1000000L);
    printf("message size: %d [B]\n", (int) messageSize);
    printf("message count: %d\n", (int) messageCount);
    printf("mean throughput: %d [msg/s]\n", (int) throughput);
    printf("mean throughput: %.3f [Mb/s]\n", (double) megabits);

    s.close();

    ctx.close();
  }

  private static void printf(String str, Object... args) {
    // TODO Auto-generated method stub
    System.out.println(String.format(str, args));
  }

  private static int atoi(String string) {
    return Integer.valueOf(string);
  }

  private static long atol(String string) {
    return Long.valueOf(string);
  }

  private static void printf(String string) {
    System.out.println(string);
  }
}
