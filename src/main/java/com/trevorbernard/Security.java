package com.trevorbernard;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class Security {
  public static void main(String[] args) throws Exception {
    String clientPublic = "Yne@$w-vo<fVvi]a<NY6T1ed:M$fCG*[IaLV{hID";
    String clientSecret = "D:)Q[IlAW!ahhC2ac:9*A}h:p?([4%wOTJ%JR%cs";

    String serverPublic = ".s&p=^57A5>h-Xgyv%mJX58}C)]fLp9&t{xl[LR]";
    String serverSecret = "gJ4jDw>ZdY1eASGv2R{<u/ZMB8:$wYG4km:g{lp>";

    ZContext context = new ZContext();
    Socket server = context.createSocket(ZMQ.PUSH);
    server.setAsServerCurve(true);
    server.setCurvePublicKey(serverPublic.getBytes());
    server.setCurveSecretKey(serverSecret.getBytes());

    server.bind("tcp://*:7210");

    Socket client = context.createSocket(ZMQ.PULL);
    client.setCurvePublicKey(clientPublic.getBytes());
    client.setCurveSecretKey(clientSecret.getBytes());
    client.setCurveServerKey(serverPublic.getBytes());

    client.connect("tcp://127.0.0.1:7210");

    server.send("Hello, World!");

    System.out.println(client.recvStr());
  }
}
