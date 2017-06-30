package com.trevorbernard.heartbeat;

public class Server {
  public static void main(String[] args) throws Exception {
    RouterServer router = new RouterServer();
    router.start();
    router.join();
  }
}
