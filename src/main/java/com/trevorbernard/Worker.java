package com.trevorbernard;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;


// Receives information from the server
public class Worker extends Thread implements Closeable {
  private final AtomicBoolean isRunning = new AtomicBoolean(true);
  private final ZContext context;
  private final String endpoint;
  private final byte[] topic;

  private Socket socket;
  private Poller poller;
  private int heartBeats;

  public Worker(ZContext context, String endpoint, byte[] topic) {
    this.context = context;
    this.endpoint = endpoint;
    this.topic = topic;
  }

  public void processMessage(Socket socket) {
    this.heartBeats = 0;
    // This shouldn't block.. return immediately
    String s = socket.recvStr();
    if (s != null)
      System.out.println("SUB:" + s);
  }


  public void run() {
    init();
    // Fix slow subscriber
    Utils.sleep(100);
    while (isRunning.get()) {
      this.poller.poll(1000);
      if (poller.pollin(0)) {
        processMessage(socket);
      } else {
        System.out.println("TICK MISSED");
        if (++this.heartBeats > 3) {
          System.out.println("CONNECTION TIMEDOUT");
          init();
        }
      }
    }
    // Good bye
    shutdown();
  }

  private void init() {
    System.out.println("Initializing worker");
    shutdown();
    this.heartBeats = 0;
    this.socket = context.createSocket(ZMQ.SUB);
    this.socket.setReceiveTimeOut(0);
    this.socket.subscribe(topic);
    this.socket.connect(endpoint);

    this.poller = context.createPoller(1);
    this.poller.register(this.socket, Poller.POLLIN);
  }

  private void shutdown() {
    System.out.println("Destroying worker");
    this.heartBeats = 0;
    if (this.socket != null) {
      this.socket.close();
      this.socket = null;
    }
  }

  @Override
  public void close() {
    this.isRunning.set(false);
    Utils.sleep(100);
  }
}
