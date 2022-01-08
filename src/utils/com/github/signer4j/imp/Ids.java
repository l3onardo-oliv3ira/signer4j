package com.github.signer4j.imp;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Ids {
  private Ids() {}
  
  private static final AtomicLong next = new AtomicLong(System.nanoTime());
  private static long last = -1;

  public static String next() {
    return next("");
  }
  
  public static String next(String prefix) {
    return next(prefix, "");
  }

  public static synchronized String next(String prefix, String suffix) {
    long n = next.getAndIncrement();
    if (last == n)
      last = ++n;
    return prefix + Long.toString(last = n) + suffix;
  }
}
