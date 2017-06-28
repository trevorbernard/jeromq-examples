package com.trevorbernard.heartbeat;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

public class DealerServer {
  public static void main(String[] args) {
    ZContext ctx = new ZContext();
    Socket router = ctx.createSocket(ZMQ.ROUTER);
    router.bind("tcp://*:23456");

    Socket dealer = ctx.createSocket(ZMQ.DEALER);
    dealer.setIdentity("foobar".getBytes());
    dealer.connect("tcp://127.0.0.1:23456");

    ZMsg msg = new ZMsg();
    msg.add("trevor");
    System.out.println(msg);
    msg.send(dealer);

    System.out.println("asdfsdf");
    msg = ZMsg.recvMsg(router);
    System.out.println(msg);

    msg.send(router);
    System.out.println("***" + ZMsg.recvMsg(dealer));
  }
}
