package com.trevorbernard.heartbeat;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.trevorbernard.Utils;

import org.zeromq.ZMsg;

public class App {
  public static void main(String[] args) {

    ZContext ctx = new ZContext();
    Socket router = ctx.createSocket(ZMQ.ROUTER);
    router.bind("tcp://*:23456");

    while(true) {
      Utils.sleep(2000);
      System.out.println("Sending message");
      ZMsg msg = new ZMsg();
      msg.add("a");
      msg.add("I");
      msg.add("FOOBAR");
      msg.send(router);
    }
  }
}
