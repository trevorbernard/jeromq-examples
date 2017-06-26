package com.trevorbernard;

import org.zeromq.ZMQ;
import org.zeromq.ZSocket;

public class PushPull {
  public static void main(String[] args) {
    int count = 100;
    
    try (ZSocket push = new ZSocket(ZMQ.PUSH);
         ZSocket pull = new ZSocket(ZMQ.PULL)) {
      
      push.bind("tcp://*:12345");
      pull.connect("tcp://0.0.0.0:12345");

      for (int i = 0; i < count; i++) {
        push.sendStringUtf8("msg:" + i);
        System.out.println(pull.receiveStringUtf8());
      }
    }
  }
}
