package com.trevorbernard;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


// Receives information from the server
public class Worker extends Thread implements Closeable, OverC {
  enum State {
    INIT, RUNNING, ERROR, QUIT
  }

  private AtomicReference<State> state = new AtomicReference<>(State.INIT);

  private final Context context;
  private final String endpoint;
  private final byte[] topic;

  private Socket socket;

  public Worker(Context context, String endpoint, byte[] topic) {
    this.context = context;
    this.endpoint = endpoint;
    this.topic = topic;
  }

  public void processMessage(Socket socket) {
    // This shouldn't block.. return immediately
    String s = socket.recvStr();
    if (s != null)
      System.out.println("SUB:" + s);
  }


  public void run() {
    init();
    // Fix slow subscriber
    try {
      Thread.sleep(100);
    } catch (Exception e) {
    }

    while (state.get() != State.QUIT) {
      processMessage(socket);
    }
    // Good bye
    shutdown();
  }

  private void init() {
    System.out.println("Initializing worker");
    shutdown();

    this.socket = context.socket(ZMQ.SUB);
    this.socket.setReceiveTimeOut(0);
    this.socket.subscribe(topic);
    this.socket.connect(endpoint);
  }

  private void shutdown() {
    System.out.println("Destroying worker");
    if (this.socket != null) {
      this.socket.close();
      this.socket = null;
    }
  }

  @Override
  public void close() {
    state.getAndSet(State.QUIT);
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
  }

  @Override
  public void onInitialization() {
    // Send message to socket
  }

  @Override
  public void onError(Throwable t) {
    System.err.println(t);
    // Send message to socket
  }
}
