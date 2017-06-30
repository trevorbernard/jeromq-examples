package com.trevorbernard.perf;

import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import zmq.ZMQ;

public class Client {

  public static void main(String[] argv) {
    String connectTo;
    long messageCount;
    int messageSize;
    ZContext ctx;
    Socket s;
    boolean rc;
    long i;
    ZMsg msg;

    if (argv.length != 3) {
      printf("usage: remote_thr <connect-to> <message-size> <message-count>\n");
      return;
    }
    connectTo = argv[0];
    messageSize = atoi(argv[1]);
    messageCount = atol(argv[2]);

    ctx = new ZContext();
    if (ctx == null) {
      printf("error in init");
      return;
    }

    s = ctx.createSocket(ZMQ.ZMQ_PUSH);
    if (s == null) {
      printf("error in socket");
    }

    // Add your socket options here.
    // For example ZMQ_RATE, ZMQ_RECOVERY_IVL and ZMQ_MCAST_LOOP for PGM.
    String clientPublic = "Yne@$w-vo<fVvi]a<NY6T1ed:M$fCG*[IaLV{hID";
    String clientSecret = "D:)Q[IlAW!ahhC2ac:9*A}h:p?([4%wOTJ%JR%cs";
    String serverPublic = ".s&p=^57A5>h-Xgyv%mJX58}C)]fLp9&t{xl[LR]";
    
    s.setCurvePublicKey(clientPublic.getBytes());
    s.setCurveSecretKey(clientSecret.getBytes());
    s.setCurveServerKey(serverPublic.getBytes());

    rc = s.connect(connectTo);
    if (!rc) {
      printf("error in connect: %s\n");
      return;
    }

    for (i = 0; i != messageCount; i++) {
      msg = new ZMsg();
      msg.add(new byte[messageSize]);
      if (msg == null) {
        printf("error in msg_init: %s\n");
        return;
      }

      boolean n = msg.send(s);
      if (!n) {
        printf("error in sendmsg: %s\n");
        return;
      }
    }

    s.close();
    ctx.close();
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
