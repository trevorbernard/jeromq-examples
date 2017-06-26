package com.trevorbernard;

import org.zeromq.ZMQ;

public class PubSub {
  public static void main(String[] args) throws Exception {
    ZMQ.Context ctx = ZMQ.context(1);
    
    ZMQ.Socket pub = ctx.socket(ZMQ.PUB);
    ZMQ.Socket sub = ctx.socket(ZMQ.SUB);
    sub.subscribe("".getBytes());
    
    pub.bind("tcp://*:12345");
    sub.connect("tcp://127.0.0.1:12345");
    Thread.sleep(100);
    pub.send("Hello, world!");
    System.out.println("SUB: " + sub.recvStr());
    
    sub.close();
    pub.close();
    ctx.close();
  }
}
