package com.github.signer4j.imp;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
  private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
  private final ThreadGroup group;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;

  public CustomThreadFactory(String baseName) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    namePrefix = baseName + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
  }

  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
    customize(t);
    return t;
  }

  protected void customize(Thread thread) {
    if (thread.isDaemon())
      thread.setDaemon(false);
    if (thread.getPriority() != Thread.NORM_PRIORITY)
      thread.setPriority(Thread.NORM_PRIORITY);
  }
}