package com.trevorbernard;

public class Utils {
  private Utils() {}

  public static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }
}
