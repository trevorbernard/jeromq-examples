package com.trevorbernard;

import org.zeromq.ZMQ;

public class ReqRep {
  public static void main(String[] args) throws Exception {
    ZMQ.Context ctx = ZMQ.context(1);
    
    ZMQ.Socket req = ctx.socket(ZMQ.REQ);
    ZMQ.Socket rep = ctx.socket(ZMQ.REP);
    
    req.bind("tcp://*:12345");
    rep.connect("tcp://127.0.0.1:12345");
    
    req.send("Hello, world!");
    
    System.out.println("REP: " + rep.recvStr());
    
    rep.close();
    req.close();
    ctx.close();
  }
}
