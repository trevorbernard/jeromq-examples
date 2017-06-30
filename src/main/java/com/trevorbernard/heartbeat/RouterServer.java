package com.trevorbernard.heartbeat;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class RouterServer extends Thread {
  private final ZContext context;

  private AtomicBoolean stop = new AtomicBoolean(false);

  private ZMQ.Socket broker;
  private ZMQ.Socket mailbox;

  private ZMQ.Poller poller;

  public RouterServer() {
    this.setName("routerserver");
    this.context = new ZContext();
  }

  public void init() {
    this.broker = this.context.createSocket(ZMQ.ROUTER);
    this.broker.bind("tcp://*:23456");

    this.mailbox = this.context.createSocket(ZMQ.PULL);
    this.mailbox.bind("tcp://*:7210");

    this.poller = this.context.createPoller(1);
    this.poller.register(this.broker, ZMQ.Poller.POLLIN);
    this.poller.register(this.mailbox, ZMQ.Poller.POLLIN);
  }

  @Override
  public void run() {
    System.out.println("Starting ROUTER");
    init();
    while (!stop.get()) {
      this.poller.poll(1000);
      System.out.println("TICK");
      if (poller.pollin(0)) {
        System.out.println("**");
        ZMsg msg = ZMsg.recvMsg(broker);
        System.out.println("MSG:" + msg);
      }
      // else if (poller.pollout(1)) {
      // System.out.println("PROXING MSG");
      // String identifier = mailbox.recvStr();
      // String payload = mailbox.recvStr();
      // broker.sendMore(identifier);
      // broker.send(payload);
      // }
    }
  }

  public void shutdown() {
    stop.set(true);
    broker.close();
    mailbox.close();
    context.close();
  }
}
