package com.trevorbernard;

import org.zeromq.ZMQ.Context;

public class HeartBeater implements Runnable {
  private final Context context;
  
  public HeartBeater(Context context) {
    this.context = context;
  }

  @Override
  public void run() {
    
  }
}
