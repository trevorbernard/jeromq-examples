package com.trevorbernard;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import zmq.io.mechanism.curve.Curve;

public class Security2 {
  public static void main(String[] args) throws Exception {
    Curve curve = new Curve();
    String[] serverKeys = curve.keypairZ85();
    String[] clientKeys = curve.keypairZ85();

    ZContext context = new ZContext();

    Socket server = context.createSocket(ZMQ.PUSH);
    server.setAsServerCurve(true);
    server.setCurvePublicKey(serverKeys[0].getBytes());
    server.setCurveSecretKey(serverKeys[1].getBytes());

    server.bind("tcp://*:7210");

    Socket client = context.createSocket(ZMQ.PULL);
    client.setCurvePublicKey(clientKeys[0].getBytes());
    client.setCurveSecretKey(clientKeys[1].getBytes());
    client.setCurveServerKey(serverKeys[0].getBytes());

    client.connect("tcp://127.0.0.1:7210");

    server.send("Hello, World!");
    System.out.println(client.recvStr());

    client.close();
    server.close();
    context.close();
  }
}
